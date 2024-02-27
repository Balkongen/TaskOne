package com.example.TaskOne;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightParser {

    public void getFlightInfo(String airport) {

        System.out.println("Airport: " + airport);

        System.out.println("Arrivals: ");
        printFlightInfo(airport, FlightInfoGetter.Type.ARRIVALS);

        System.out.println("-----------------------------");

        System.out.println("Departures");
        printFlightInfo(airport, FlightInfoGetter.Type.DEPARTURES);
    }

    private void printFlightInfo(String airport, FlightInfoGetter.Type type) {
        FlightInfoGetter flightInfoGetter = new FlightInfoGetter(airport, type);

        JsonNode flightInfo = flightInfoGetter.getInfo();

        if (flightInfo == null) {
            System.out.println("No info");
        } else {

            if (type == FlightInfoGetter.Type.DEPARTURES) {
                List<Flight> flights = createFlightsInCorrectFormat(flightInfo);
                saveToDatabase(flights);
            }
            printFlights(flightInfo);
        }
    }

    private List<Flight> createFlightsInCorrectFormat(JsonNode flightInfo) {
        List<Flight> flights = new ArrayList<>();
        int counter = 0;

        for (JsonNode jsonNode : flightInfo) {
            if (counter == 10)
                return flights;

            flights.add(new Flight(jsonNode.get("flightId").asText(), jsonNode.get("arrivalAirportSwedish").asText()));
            counter++;
        }

        return flights;
    }

    private void printFlights(JsonNode flightInfo) {
        for (JsonNode jsonNode : flightInfo) {
            System.out.println(jsonNode);
        }
    }

    private void saveToDatabase(List<Flight> flights) {
        try {
            Connection connection = DriverManager.getConnection(Configuration.DB_URL, Configuration.USERNAME, Configuration.PASSWORD);
            String sqlQuery = "INSERT INTO departures(\"flightId\", \"arrivalAirport\", \"time\") VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            for (Flight flight : flights) {
                preparedStatement.setString(1,flight.id());
                preparedStatement.setString(2, flight.arrivalAirport());
                preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            preparedStatement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FlightParser flightParser = new FlightParser();
        flightParser.getFlightInfo(args[0]);
    }
}

package com.example.TaskOne;

import org.junit.jupiter.api.Test;

class FlightParserTest {

    @Test
    void flightParser() {
        FlightParser flightParser = new FlightParser();
        flightParser.getFlightInfo("arn");
    }

}
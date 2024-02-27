package com.example.TaskOne;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TimeZone;

public class FlightInfoGetter {

    private static final String BASE_URL = "https://api.swedavia.se/flightinfo/v2/";
    private static final Map<Type, String> typeToStringEquivalent = Map.of(
            Type.ARRIVALS, "arrivals",
            Type.DEPARTURES, "departures"
    );

    private String url;

    enum Type {
        ARRIVALS,
        DEPARTURES
    }

    public FlightInfoGetter(String airport, Type type) {
        buildURL(airport, type);
    }

    private void buildURL(String airport, Type type) {
        this.url = BASE_URL +
                airport +
                "/" +
                typeToStringEquivalent.get(type) +
                "/" +
                getCurrentTime();
    }

    private String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        return now.format(dtf);
    }

    public JsonNode getInfo() {
        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Ocp-Apim-Subscription-Key", Configuration.SWEDAVIA_API_KEY)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode node = objectMapper.readTree(response.body());

            return node.get("flights");

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

}

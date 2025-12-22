package com.charleswang.analytics.app.client;

import java.util.ArrayList;
import java.util.List;

import com.charleswang.analytics.app.model.DailyPoint;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataServiceClient {

    private static final String DATA_URL =
        System.getenv().getOrDefault(
            "DATASERVICE_URL",
            "http://localhost:8080"
        ) + "/api/summary/daily";

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<DailyPoint> fetchDailyPoints() throws Exception {

        Request request = new Request.Builder()
            .url(DATA_URL)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to fetch data-service summary");
            }

            JsonNode root = mapper.readTree(response.body().string());
            List<DailyPoint> points = new ArrayList<>();

            for (JsonNode node : root) {

                JsonNode tempNode = node.get("temperature_max");
                JsonNode countNode = node.get("incident_count");

                if (tempNode == null || countNode == null) {
                    continue; // skip malformed rows
                }

                points.add(new DailyPoint(
                    tempNode.asDouble(),
                    countNode.asInt()
                ));
            }

            return points;
        }
    }
}

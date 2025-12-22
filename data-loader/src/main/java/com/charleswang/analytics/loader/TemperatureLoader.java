package com.charleswang.analytics.loader;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TemperatureLoader {

    private static final String TEMP_URL =
        "https://archive-api.open-meteo.com/v1/archive?"
      + "latitude=40.7&longitude=-74"
      + "&start_date=2006-01-01&end_date=2025-10-09"
      + "&daily=temperature_2m_max"
      + "&timezone=America/New_York";

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .callTimeout(Duration.ofMinutes(2))
        .connectTimeout(Duration.ofSeconds(20))
        .readTimeout(Duration.ofSeconds(30))
        .build();

    private static final ObjectMapper mapper = new ObjectMapper();

    private List<String> dates = new ArrayList<>();
    private List<Double> maxTemps = new ArrayList<>();

    public void fetch() throws Exception {

        Request request = new Request.Builder()
            .url(TEMP_URL)
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Temperature API failed: " + response);
            }

            JsonNode root = mapper.readTree(response.body().string());
            JsonNode daily = root.get("daily");

            JsonNode time = daily.get("time");
            JsonNode temps = daily.get("temperature_2m_max");

            for (int i = 0; i < time.size(); i++) {
                dates.add(time.get(i).asText());
                maxTemps.add(temps.get(i).asDouble());
            }
        }
    }

    public List<String> getDates() {
        return dates;
    }

    public List<Double> getMaxTemps() {
        return maxTemps;
    }
}

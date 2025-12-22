package com.charleswang.analytics.loader;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IncidentLoader {

    private static final String INCIDENT_URL =
        "https://data.cityofnewyork.us/resource/833y-fsy8.json?$limit=100000";

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .callTimeout(Duration.ofMinutes(2))
        .connectTimeout(Duration.ofSeconds(20))
        .readTimeout(Duration.ofSeconds(30))
        .build();

    private static final ObjectMapper mapper = new ObjectMapper();

    private final String credential;

    public IncidentLoader(String username, String password) {
        this.credential = Credentials.basic(username, password);
    }

    private static String extractDate(String isoDateTime) {
        if (isoDateTime == null) return null;
        return isoDateTime.length() >= 10
            ? isoDateTime.substring(0, 10)
            : isoDateTime;
    }


    public List<IncidentRow> fetch() throws Exception {

        Request request = new Request.Builder()
            .url(INCIDENT_URL)
            .header("Authorization", credential)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Incident API failed: " + response);
            }

            JsonNode root = mapper.readTree(response.body().string());
            List<IncidentRow> rows = new ArrayList<>();

            for (JsonNode node : root) {

                rows.add(new IncidentRow(
                    node.path("incident_key").asText(null),
                    extractDate(node.path("occur_date").asText(null)),
                    node.path("boro").asText(null),
                    node.path("precinct").asInt(0),
                    node.path("vic_sex").asText(null),
                    node.path("vic_age_group").asText(null),
                    node.path("perp_sex").asText(null),
                    node.path("perp_age_group").asText(null)
                ));
            }

            return rows;
        }

    }

    // small inner holder (simpler than a public record)
    static class IncidentRow {
        String incidentKey, occurDate, borough, vicSex, vicAgeGroup, perpSex, perpAgeGroup;
        int precinct;

        IncidentRow(String k, String d, String b, int p,
                    String vs, String va, String ps, String pa) {
            incidentKey = k;
            occurDate = d;
            borough = b;
            precinct = p;
            vicSex = vs;
            vicAgeGroup = va;
            perpSex = ps;
            perpAgeGroup = pa;
        }
    }
}

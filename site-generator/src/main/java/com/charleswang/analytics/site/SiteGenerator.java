package com.charleswang.analytics.site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SiteGenerator {

    private static final Path OUTPUT_DIR =
        Paths.get("site-generator/target/site").toAbsolutePath();

    private static final Path RESOURCES =
        Paths.get("site-generator/src/main/resources").toAbsolutePath();

    private static final String SUMMARY_URL =
        "http://localhost:8080/api/summary/daily";

    private static final String ANALYTICS_URL =
        "http://localhost:8090/analytics/regression";

    public static void main(String[] args) {
        try {
            Files.createDirectories(OUTPUT_DIR);

            OkHttpClient client = new OkHttpClient();

            // ---- Fetch API data ----
            String summaryJson = fetch(client, SUMMARY_URL);
            String analyticsJson = fetch(client, ANALYTICS_URL);

            // ---- Write JSON assets ----
            Files.writeString(OUTPUT_DIR.resolve("summary.json"), summaryJson);
            Files.writeString(OUTPUT_DIR.resolve("analytics.json"), analyticsJson);

            // ---- Copy HTML shell (NO DATA INJECTION) ----
            Files.copy(
                RESOURCES.resolve("index_template.html"),
                OUTPUT_DIR.resolve("index.html"),
                StandardCopyOption.REPLACE_EXISTING
            );

            // ---- Copy static assets ----
            copy("chart_scatter.js");
            copy("chart_bin.js");
            copy("search.js");
            copy("style.css");

            System.out.println(" Site generated at:");
            System.out.println(OUTPUT_DIR);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String fetch(OkHttpClient client, String url) throws IOException {
        Request req = new Request.Builder().url(url).build();
        try (Response res = client.newCall(req).execute()) {
            if (!res.isSuccessful()) {
                throw new RuntimeException("Failed to fetch: " + url);
            }
            return res.body().string();
        }
    }

    private static void copy(String file) throws IOException {
        Files.copy(
            RESOURCES.resolve(file),
            OUTPUT_DIR.resolve(file),
            StandardCopyOption.REPLACE_EXISTING
        );
    }
}

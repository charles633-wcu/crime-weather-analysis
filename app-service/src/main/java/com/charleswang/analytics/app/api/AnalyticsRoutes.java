package com.charleswang.analytics.app.api;

import com.charleswang.analytics.app.service.RegressionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.get;

public class AnalyticsRoutes {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void register() {

        get("/analytics/regression", (req, res) -> {
            res.type("application/json");
            return mapper.writeValueAsString(
                RegressionService.computeRegression()
            );
        });
    }
}

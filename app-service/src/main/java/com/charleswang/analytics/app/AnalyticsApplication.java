package com.charleswang.analytics.app;

import com.charleswang.analytics.app.api.AnalyticsRoutes;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.options;
import static spark.Spark.port;

public class AnalyticsApplication {

    public static void main(String[] args) {

        port(8090);

        options("/*",
            (request, response) -> {

                String reqHeaders = request.headers("Access-Control-Request-Headers");
                if (reqHeaders != null) {
                    response.header("Access-Control-Allow-Headers", reqHeaders);
                }

                String reqMethod = request.headers("Access-Control-Request-Method");
                if (reqMethod != null) {
                    response.header("Access-Control-Allow-Methods", reqMethod);
                }

                return "OK";
            }
        );

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
        });


        exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
            res.status(500);
            res.body("{\"error\":\"internal server error\"}");
        });

        AnalyticsRoutes.register();
        printEndpoints();
    }


    private static void printEndpoints() {
        System.out.println("======================================");
        System.out.println(" Analytics Service started on port 8090");
        System.out.println(" Available endpoints:");
        System.out.println("  GET http://localhost:8090/analytics/regression");
        System.out.println("======================================");
    }

    private static void enableCORS() {
        options("/*", (request, response) -> {
            String headers = request.headers("Access-Control-Request-Headers");
            if (headers != null) {
                response.header("Access-Control-Allow-Headers", headers);
            }

            String method = request.headers("Access-Control-Request-Method");
            if (method != null) {
                response.header("Access-Control-Allow-Methods", method);
            }
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
        });
    }
}

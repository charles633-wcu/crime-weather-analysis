package com.charleswang.analytics.data;

import com.charleswang.analytics.data.dao.IncidentDao;
import com.charleswang.analytics.data.dao.SummaryDao;
import com.charleswang.analytics.data.dao.TemperatureDao;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.port;


public class DataServiceApplication {

    public static void main(String[] args) {

        port(8080);
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


        ObjectMapper mapper = new ObjectMapper();

        System.out.println("======================================");
        System.out.println(" Data Service started on port 8080");
        System.out.println(" Available endpoints:");
        System.out.println("  GET http://localhost:8080/api/temperature");
        System.out.println("  GET http://localhost:8080/api/incidents");
        System.out.println("  GET http://localhost:8080/api/incidents/by-date?date=YYYY-MM-DD");
        System.out.println("  GET http://localhost:8080/api/summary/daily");
        System.out.println("======================================");


        exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
            res.status(500);
            res.body("{\"error\":\"internal server error\"}");
        });

        get("/api/temperature", (req, res) -> {
            res.type("application/json");
            return mapper.writeValueAsString(TemperatureDao.getAll());
        });

        get("/api/incidents", (req, res) -> {
            res.type("application/json");
            return mapper.writeValueAsString(IncidentDao.getAll());
        });

        get("/api/incidents/by-date", (req, res) -> {
            res.type("application/json");
            String date = req.queryParams("date");
            if (date == null) {
                res.status(400);
                return "{\"error\":\"missing date parameter\"}";
            }
            return mapper.writeValueAsString(IncidentDao.getByDate(date));
        });

        get("/api/summary/daily", (req, res) -> {
            res.type("application/json");
            return mapper.writeValueAsString(SummaryDao.getDailySummary());
        });
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


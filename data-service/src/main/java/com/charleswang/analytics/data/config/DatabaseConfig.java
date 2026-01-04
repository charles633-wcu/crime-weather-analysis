package com.charleswang.analytics.data.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConfig {

    private static final String DB_URL;

    static {
        String dbPathEnv = System.getenv("DB_PATH");

        Path dbPath;
        if (dbPathEnv != null) {
            dbPath = Paths.get(dbPathEnv);
        } else {
            // 2. Fallback (local dev)
            dbPath = Paths.get("").toAbsolutePath()
                          .resolve("crime_weather.db");
        }

        DB_URL = "jdbc:sqlite:" + dbPath.toString();
        System.out.println("SQLite DB: " + DB_URL);
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(DB_URL);
    }
}

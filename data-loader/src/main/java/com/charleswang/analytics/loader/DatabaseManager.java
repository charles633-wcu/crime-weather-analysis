package com.charleswang.analytics.loader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL;

    static {
        String dbPathEnv = System.getenv("DB_PATH");

        Path dbPath;
        if (dbPathEnv != null) {
            dbPath = Paths.get(dbPathEnv);
        } else {
            dbPath = Paths.get("").toAbsolutePath()
                          .resolve("crime_weather.db");
        }

        DB_URL = "jdbc:sqlite:" + dbPath.toString();
        System.out.println("SQLite DB (loader): " + DB_URL);
    }

    public static Connection connect() throws Exception {
        return DriverManager.getConnection(DB_URL);
    }

    public static void createTables() throws Exception {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS incidents (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    incident_key TEXT,
                    occur_date TEXT,
                    borough TEXT,
                    precinct INTEGER,
                    vic_sex TEXT,
                    vic_age_group TEXT,
                    perp_sex TEXT,
                    perp_age_group TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS temperature (
                    date TEXT PRIMARY KEY,
                    temperature_max REAL
                );
            """);
        }
    }

    public static void insertTemperature(List<String> dates,
                                         List<Double> maxTemps) throws Exception {

        String sql = """
            INSERT OR REPLACE INTO temperature (date, temperature_max)
            VALUES (?, ?)
        """;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (int i = 0; i < dates.size(); i++) {
                ps.setString(1, dates.get(i));
                ps.setDouble(2, maxTemps.get(i));
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
        }
    }

    public static void insertIncidents(List<IncidentLoader.IncidentRow> rows)
            throws Exception {

        String sql = """
            INSERT OR IGNORE INTO incidents (
                incident_key,
                occur_date,
                borough,
                precinct,
                vic_sex,
                vic_age_group,
                perp_sex,
                perp_age_group
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (IncidentLoader.IncidentRow r : rows) {
                ps.setString(1, r.incidentKey);
                ps.setString(2, r.occurDate);
                ps.setString(3, r.borough);
                ps.setInt(4, r.precinct);
                ps.setString(5, r.vicSex);
                ps.setString(6, r.vicAgeGroup);
                ps.setString(7, r.perpSex);
                ps.setString(8, r.perpAgeGroup);
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
        }
    }
}

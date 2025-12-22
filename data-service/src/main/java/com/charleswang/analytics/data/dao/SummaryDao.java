package com.charleswang.analytics.data.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.charleswang.analytics.data.config.DatabaseConfig;

public class SummaryDao {

    public static List<Map<String, Object>> getDailySummary()
            throws Exception {

        List<Map<String, Object>> rows = new ArrayList<>();

        String sql = """
            SELECT
              i.occur_date AS date,
              COUNT(*) AS incident_count,
              t.temperature_max
            FROM incidents i
            LEFT JOIN temperature t
              ON i.occur_date = t.date
            GROUP BY i.occur_date
            ORDER BY i.occur_date
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("date", rs.getString("date"));
                row.put("incident_count", rs.getInt("incident_count"));
                row.put("temperature_max", rs.getDouble("temperature_max"));
                rows.add(row);
            }
        }

        return rows;
    }
}

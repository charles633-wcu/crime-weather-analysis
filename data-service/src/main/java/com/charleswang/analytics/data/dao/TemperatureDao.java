package com.charleswang.analytics.data.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.charleswang.analytics.data.config.DatabaseConfig;

public class TemperatureDao {

    public static List<Map<String, Object>> getAll() throws Exception {

        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT date, temperature_max FROM temperature ORDER BY date"
             )) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("date", rs.getString("date"));
                row.put("temperature_max", rs.getDouble("temperature_max"));
                rows.add(row);
            }
        }

        return rows;
    }
}

package com.charleswang.analytics.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.charleswang.analytics.data.config.DatabaseConfig;

public class IncidentDao {

    public static List<Map<String, Object>> getAll() throws Exception {

        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             ResultSet rs = conn.createStatement()
                 .executeQuery("SELECT * FROM incidents")) {

            while (rs.next()) {
                rows.add(mapRow(rs));
            }
        }

        return rows;
    }

    public static List<Map<String, Object>> getByDate(String date)
            throws Exception {

        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT * FROM incidents WHERE occur_date = ?"
             )) {

            ps.setString(1, date);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapRow(rs));
                }
            }
        }

        return rows;
    }

    private static Map<String, Object> mapRow(ResultSet rs)
            throws Exception {

        Map<String, Object> row = new HashMap<>();
        row.put("incident_key", rs.getString("incident_key"));
        row.put("occur_date", rs.getString("occur_date"));
        row.put("borough", rs.getString("borough"));
        row.put("precinct", rs.getInt("precinct"));
        row.put("vic_sex", rs.getString("vic_sex"));
        row.put("vic_age_group", rs.getString("vic_age_group"));
        row.put("perp_sex", rs.getString("perp_sex"));
        row.put("perp_age_group", rs.getString("perp_age_group"));
        return row;
    }
}

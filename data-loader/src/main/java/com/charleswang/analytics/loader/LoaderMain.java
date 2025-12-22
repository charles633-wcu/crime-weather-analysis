package com.charleswang.analytics.loader;

public class LoaderMain {

    private static final String DEFAULT_USERNAME = "a5jn079zj7ccuxwyo5mxfn70l";
    private static final String DEFAULT_PASSWORD = "540ul3dt0g36ddicw4mbsv38gcyh8jxwupk9qfgemvqbqppqlq";

    public static void main(String[] args) throws Exception {

        DatabaseManager.createTables();

        String username = System.getenv("NYC_API_USERNAME");
        String password = System.getenv("NYC_API_PASSWORD");

        if (username == null || password == null) {
            System.out.println("Env vars not found, using fallback credentials");
            username = DEFAULT_USERNAME;
            password = DEFAULT_PASSWORD;
        }

        // -------- TEMPERATURE --------
        TemperatureLoader tempLoader = new TemperatureLoader();
        tempLoader.fetch();

        DatabaseManager.insertTemperature(
            tempLoader.getDates(),
            tempLoader.getMaxTemps()
        );

        System.out.println("Temperature rows inserted: " +
            tempLoader.getDates().size());

        // -------- INCIDENTS --------
        IncidentLoader incidentLoader =
            new IncidentLoader(username, password);

        var incidentRows = incidentLoader.fetch();

        DatabaseManager.insertIncidents(incidentRows);

        System.out.println("Incident rows inserted: " + incidentRows.size());
        System.out.println("DONE");
    }
}

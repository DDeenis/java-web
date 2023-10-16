package step.learning.services.db;


import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class PlanetScaleDbProvider implements DbProvider {
    private static Connection connection;

    @Override
    public Connection getConnection() {
        if(connection == null) {
            JsonObject config;
            try(Reader streamReader = new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("db_config.json")))) {
                config = JsonParser.parseReader(streamReader).getAsJsonObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            JsonObject dbProvider = config.get("DataProviders").getAsJsonObject().get("PlanetScale").getAsJsonObject();

            try {
                //Class.forName("com.mysql.cj.jdbc.Driver");
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                connection = DriverManager.getConnection(
                        dbProvider.get("url").getAsString(),
                        dbProvider.get("user").getAsString(),
                        dbProvider.get("password").getAsString()
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return connection;
    }
}

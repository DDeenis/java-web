package step.learning.services.db;

import com.mysql.cj.jdbc.MysqlPooledConnection;

import java.sql.Connection;

public class PlanetScaleDbProvider implements DbProvider {
    private static Connection connection;

    @Override
    public Connection getConnection() {
        return null;
    }
}

package step.learning.dao;

import com.google.inject.Inject;
import step.learning.services.db.DbProvider;

import javax.inject.Named;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthTokenDao {
    private final DbProvider dbProvider;
    private final String dbPrefix;
    private final Logger logger;

    @Inject
    public AuthTokenDao(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, Logger logger) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.logger = logger;
    }

    public boolean install() {
        String sql = String.format(
            "create table if not exists %sauth_tokens (%s,%s,%s,%s) engine = InnoDB, default charset = utf8mb4 collate utf8mb4_unicode_ci",
            dbPrefix,
            "jti binary(16) primary key default (uuid_to_bin(uuid()))",
            "sub bigint unsigned not null comment 'user-id'",
            "exp datetime not null",
            "iat datetime not null default current_timestamp"
        );
        try(Statement statement = dbProvider.getConnection().createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
            return false;
        }

        return true;
    }
}

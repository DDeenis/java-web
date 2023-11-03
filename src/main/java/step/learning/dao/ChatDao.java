package step.learning.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dto.entities.ChatMessage;
import step.learning.services.db.DbProvider;

import javax.inject.Named;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class ChatDao {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(5);
    private final Object addSyncObject = new Object();

    private final DbProvider dbProvider;
    private final String dbPrefix;
    private final Logger logger;

    @Inject
    public ChatDao(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, Logger logger) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.logger = logger;
    }

    public boolean install() {
        String sql = String.format(
                "create table %schat (%s,%s,%s,%s) engine InnoDB, default charset = utf8mb4 collate utf8mb4_unicode_ci",
                dbPrefix,
                "id bigint unsigned primary key default (uuid_short())",
                "user char(4) not null",
                "message text not null",
                "moment datetime default current_timestamp"
        );
        try(Statement statement = dbProvider.getConnection().createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
            return false;
        }

        return true;
    }

    public boolean add(ChatMessage message) {
        if(message == null) return false;

        String sql = "insert into " + dbPrefix + "chat (`user`, `message`) values (?,?)";
        try(PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
            statement.setString(1, message.getUser());
            statement.setString(2, message.getMessage());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
            return false;
        }

        return true;
    }

    public Future<Boolean> addAsync(ChatMessage message) {
        return threadPool.submit(() -> {
            if(message == null) return false;

            String sql = "insert into " + dbPrefix + "chat (`user`, `message`) values (?,?)";
            try(PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
                statement.setString(1, message.getUser());
                statement.setString(2, message.getMessage());

                synchronized (addSyncObject) {
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
                return false;
            }

            return true;
        });
    }
}

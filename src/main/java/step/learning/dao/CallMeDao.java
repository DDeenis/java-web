package step.learning.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dto.entities.CallMe;
import step.learning.services.db.DbProvider;

import javax.inject.Named;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class CallMeDao {
    private final DbProvider dbProvider;
    private final String dbPrefix;
    private final Logger logger;

    @Inject
    public CallMeDao(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, Logger logger) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.logger = logger;
    }

    public List<CallMe> getAll(boolean includeDeleted) {
        List<CallMe> calls = new ArrayList<>();
        String sql = "select C.* from " + dbPrefix + "call_me C";

        if(!includeDeleted) {
            sql += " where C.delete_moment is null";
        }

        try(
            Statement statement = dbProvider.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
        ) {
            while (resultSet.next()) {
                calls.add(new CallMe(resultSet));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
        }

        return calls;
    }

    public List<CallMe> getAll() {
        return getAll(false);
    }

    public CallMe getById(String id, boolean includeDeleted) {
        if(id == null) return null;

        String sql = "select C.* from " + dbPrefix + "call_me C where C.id=?";

        if(!includeDeleted) {
            sql += "and C.delete_moment is null";
        }

        try(PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return new CallMe(resultSet);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
            return null;
        }

        return null;
    }

    public CallMe getById(String id) {
        return getById(id, false);
    }

    public boolean updateCallMoment(CallMe item) {
        if(item == null) return false;

        String sql = "select current_timestamp";
        Timestamp moment;
        try(Statement statement = dbProvider.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            moment = resultSet.getTimestamp(1);
        } catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
            return false;
        }

        sql = "update " + dbPrefix + "call_me set call_moment=? where id=?";
        try(PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
            statement.setTimestamp(1, moment);
            statement.setString(2, item.getId());
            statement.executeUpdate();
            item.setCallMoment(new Date(moment.getTime()));
        } catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
            return false;
        }

        return true;
    }

    public boolean delete(CallMe item, boolean softDelete) {
        if(item == null || item.getId() == null) {
            return false;
        }

        String sql = softDelete ?
                "update " + dbPrefix + "call_me set delete_moment=current_timestamp where id=?"
                : "delete from " + dbPrefix + "call_me where id=?";

        try(PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
            statement.setString(1, item.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    public boolean restore(String id) {
        if(id == null) {
            return false;
        }

        String sql = "update " + dbPrefix + "call_me set delete_moment=null where id=?";

        try(PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            return false;
        }

        return true;
    }
}

package step.learning.dao;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import step.learning.dto.entities.AuthToken;
import step.learning.dto.entities.User;
import step.learning.services.db.DbProvider;

import javax.inject.Named;
import java.sql.*;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthTokenDao {
    private final DbProvider dbProvider;
    private final String dbPrefix;
    private final Logger logger;
    private final UserDao userDao;

    @Inject
    public AuthTokenDao(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, Logger logger, UserDao userDao) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.logger = logger;
        this.userDao = userDao;
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

    public AuthToken getTokenByBearer(String bearer) {
        String jti;
        try {
            jti = JsonParser.parseString(
                new String(Base64.getUrlDecoder().decode(bearer.getBytes()))
            ).getAsJsonObject().get("jti").getAsString();
        } catch (Exception e) {
            return null;
        }

        String sql = "select bin_to_uuid(t.jti) as `jti`, t.sub, t.iat, t.exp, u.`login` as `nik` from "
                + dbPrefix
                + "auth_tokens t join " + dbPrefix + "users u on u.id = t.sub "
                + "where jti=uuid_to_bin(?) and t.exp > current_timestamp";
        try(PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
            statement.setString(1, jti);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return new AuthToken(resultSet);
            }
        }
        catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
        }

        return null;
    }

    public AuthToken getTokenByCredentials(String login, String password) {
        User user = userDao.getUserByCredentials(login, password);
        if(user == null) {
            return null;
        }

        AuthToken authToken = getActiveToken(user.getId());
        if(authToken != null) return authToken;

        Date dbTimestamp = new Date(getDbTimestamp().getTime());
        authToken = new AuthToken();
        authToken.setIat(dbTimestamp);
        authToken.setExp(new Date(dbTimestamp.getTime() + 1000 * 60 * 60 * 24));
        authToken.setSub(user.getId());
        authToken.setJti(UUID.randomUUID().toString());

        String sql = "insert into " + dbPrefix + "auth_tokens (`jti`,`sub`,`iat`,`exp`) values (uuid_to_bin(?),?,?,?)";
        try(PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
            statement.setString(1, authToken.getJti());
            statement.setString(2, authToken.getSub());
            statement.setTimestamp(3, new Timestamp(authToken.getIat().getTime()));
            statement.setTimestamp(4, new Timestamp(authToken.getExp().getTime()));
            statement.executeUpdate();
            return authToken;
        } catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
        }

        return null;
    }

    public AuthToken getActiveToken(String userId) {
        String sql = "select bin_to_uuid(A.jti) as jti, A.sub, A.iat, A.exp from " + dbPrefix + "auth_tokens A where A.exp > current_timestamp and A.sub=?";
        try(PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return new AuthToken(resultSet);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
        }

        return null;
    }

    private Timestamp getDbTimestamp() {
        String sql = "select current_timestamp";
        try(Statement statement = dbProvider.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getTimestamp(1);
        } catch (SQLException ignored) {
        }

        return null;
    }

    private String getDbIdentity() {
        String sql = "select uuid_to_bin(uuid())";
        try(Statement statement = dbProvider.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getString(1);
        } catch (SQLException ignored) {
        }

        return null;
    }
}

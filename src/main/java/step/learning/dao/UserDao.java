package step.learning.dao;

import com.google.inject.Inject;
import step.learning.dto.entities.User;
import step.learning.dto.models.RegFormModel;
import step.learning.services.db.DbProvider;
import step.learning.services.kdf.KdfService;
import step.learning.services.random.RandomService;

import javax.inject.Named;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDao {
    private final DbProvider dbProvider;
    private final String dbPrefix;
    private final Logger logger;
    private final RandomService randomService;
    private final KdfService kdfService;

    @Inject
    public UserDao(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, Logger logger, RandomService randomService, KdfService kdfService) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.logger = logger;
        this.randomService = randomService;
        this.kdfService = kdfService;
    }

    public boolean install() {
        String sql = String.format(
                "create table if not exists %susers (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) engine = InnoDB, default charset = utf8mb4 collate utf8mb4_unicode_ci",
                dbPrefix,
                "`id` bigint unsigned primary key default (uuid_short())",
                "`name` varchar(128) not null",
                "`login` varchar(64) not null unique",
                "`salt` varchar(16) not null comment 'RFC 2898 -- Salt'",
                "`passDK` varchar(32) not null comment 'RFC 2898 -- Derived key'",
                "`email` varchar(96) not null",
                "`email_code` char(6) null",
                "`avatar_url` varchar(64) null",
                "`birthdate` date null",
                "`reg_at` datetime not null default current_timestamp",
                "`del_at` datetime null"
        );
        try(Statement statement = dbProvider.getConnection().createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
            return false;
        }

        return true;
    }

    public boolean addFromForm(RegFormModel formModel) {
        String salt = randomService.randomHex(16);
        String passDK = kdfService.getDerivedKey(formModel.getPassword(), salt);
        String emailCode = randomService.randomHex(6);
        String sql = "insert into " + dbPrefix + "users (`name`,`salt`,`passDK`,`email`,`email_code`,`avatar_url`,`birthdate`,`login`) values (?,?,?,?,?,?,?,?)";
        try(PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
            statement.setString(1, formModel.getRealName());
            statement.setString(2, salt);
            statement.setString(3, passDK);
            statement.setString(4, formModel.getEmail());
            statement.setString(5, emailCode);
            statement.setString(6, formModel.getAvatar());
            statement.setString(7, formModel.getBirthDateAsString());
            statement.setString(8, formModel.getLogin());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
            return false;
        }

        return true;
    }

    public User getUserByCredentials(String login, String password) {
        if(login == null || password == null) return null;

        String sql = "select u.* from " + dbPrefix + "users u where u.`login`=?";
        try(PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                User user = new User(resultSet);
                String salt = user.getSalt();
                String passDK = kdfService.getDerivedKey(password, salt);
                if (passDK.equals(user.getPassDK())) {
                    return user;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, String.format("%s | %s", e.getMessage(), sql));
        }

        return null;
    }
}

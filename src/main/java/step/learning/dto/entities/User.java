package step.learning.dto.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class User {
    private String id;
    private String name;
    private String login;
    private String salt;
    private String passDK;
    private String email;
    private String emailCode;
    private Date birthDate;
    private String avatarUrl;
    private Date registerAt;
    private Date deleteAt;

    public User(ResultSet resultSet) throws SQLException {
        setId(resultSet.getString("id"));
        setLogin(resultSet.getString("login"));
        setName(resultSet.getString("name"));
        setEmail(resultSet.getString("email"));
        setEmailCode(resultSet.getString("email_code"));
        setSalt(resultSet.getString("salt"));
        setPassDK(resultSet.getString("passDK"));
        setAvatarUrl(resultSet.getString("avatar_url"));
        setBirthDate(resultSet.getDate("birthdate"));

        Timestamp moment = resultSet.getTimestamp("reg_at");
        this.setRegisterAt(moment == null ? null : new Date(moment.getTime()));
        moment = resultSet.getTimestamp("del_at");
        this.setDeleteAt(moment == null ? null : new Date(moment.getTime()));
    }

    public Date getRegisterAt() {
        return registerAt;
    }

    public void setRegisterAt(Date registerAt) {
        this.registerAt = registerAt;
    }

    public Date getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(Date deleteAt) {
        this.deleteAt = deleteAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPassDK() {
        return passDK;
    }

    public void setPassDK(String passDK) {
        this.passDK = passDK;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }
}

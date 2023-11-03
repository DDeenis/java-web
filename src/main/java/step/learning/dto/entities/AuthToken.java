package step.learning.dto.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class AuthToken {
    private String jti;
    private String sub;
    private Date exp;
    private Date iat;
    private String nik;

    public AuthToken() {}

    public AuthToken(String jti, String sub, Date exp, Date iat) {
        this.jti = jti;
        this.sub = sub;
        this.exp = exp;
        this.iat = iat;
    }

    public AuthToken(ResultSet resultSet) throws SQLException {
        setJti(resultSet.getString("jti"));
        setSub(resultSet.getString("sub"));
        Timestamp moment = resultSet.getTimestamp("exp");
        setExp(new Date(moment.getTime()));
        moment = resultSet.getTimestamp("iat");
        setIat(new Date(moment.getTime()));
        try {
            this.nik = resultSet.getString("nik");
        }
        catch (Exception ignored) {}
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public Date getExp() {
        return exp;
    }

    public void setExp(Date exp) {
        this.exp = exp;
    }

    public Date getIat() {
        return iat;
    }

    public void setIat(Date iat) {
        this.iat = iat;
    }

    public String getNik() {
        return nik;
    }
}

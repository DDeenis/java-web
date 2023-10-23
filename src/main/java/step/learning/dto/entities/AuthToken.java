package step.learning.dto.entities;

import java.util.Date;

public class AuthToken {
    private String jti;
    private String sub;
    private Date exp;
    private Date iat;

    public AuthToken(String jti, String sub, Date exp, Date iat) {
        this.jti = jti;
        this.sub = sub;
        this.exp = exp;
        this.iat = iat;
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
}

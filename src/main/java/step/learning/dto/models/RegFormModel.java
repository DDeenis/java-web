package step.learning.dto.models;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegFormModel {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public RegFormModel(HttpServletRequest req) throws ParseException {
        setRealName(req.getParameter("reg-name"));
        setLogin(req.getParameter("reg-login"));
        setPassword(req.getParameter("reg-password"));
        setRepeatPassword(req.getParameter("reg-repeat-password"));
        setEmail(req.getParameter("reg-email"));
        setBirthDate(req.getParameter("reg-birthday"));
        setAgree(req.getParameter("reg-agree"));
    }

    public Map<String, String> getErrorMessages() {
        Map<String, String> result = new HashMap<>();

        if(isNullOrEmpty(login)) {
            result.put("login", "Login can't be null");
        }

        if(isNullOrEmpty(realName)) {
            result.put("realName", "Real name can't be null");
        }

        if(isNullOrEmpty(password)) {
            result.put("password", "Password can't be null");
        }

        if(isNullOrEmpty(repeatPassword)) {
            result.put("repeatPassword", "Repeat password can't be null");
        }

        if(isNullOrEmpty(email)) {
            result.put("email", "Email can't be null");
        }

        if(birthDate == null) {
            result.put("birthDate", "Birth date can't be null");
        }

        if(!isAgree) {
            result.put("isAgree", "You need to agree with site rules");
        }

        return result;
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    // region fields
    private String realName;
    private String login;
    private String email;
    private String password;
    private String repeatPassword;
    private Date birthDate;
    private Boolean isAgree;
    // endregion

    // region accessors
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getBirthDateAsString() {
        return birthDate == null ? "" : dateFormat.format(getBirthDate());
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    public void setBirthDate(String birthDate) throws ParseException {
        this.birthDate = isNullOrEmpty(birthDate) ? null : dateFormat.parse(birthDate);
    }

    public Boolean getAgree() {
        return isAgree;
    }

    public void setAgree(Boolean agree) {
        isAgree = agree;
    }

    public void setAgree(String agree) {
        isAgree = ("on").equalsIgnoreCase(agree) || ("checked").equalsIgnoreCase(agree) || ("true").equalsIgnoreCase(agree);
    }
    // endregion
}

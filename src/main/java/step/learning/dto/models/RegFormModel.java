package step.learning.dto.models;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import step.learning.services.formparse.FormParseResult;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class RegFormModel {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String[] allowedAvatarExtensions = new String[] { "jpg", "png", "webp", "avif", "bmp", "gif" };

    public RegFormModel(FormParseResult result) throws ParseException {
        Map<String, String> fields = result.getFields();
        setRealName(fields.get("reg-name"));
        setLogin(fields.get("reg-login"));
        setPassword(fields.get("reg-password"));
        setRepeatPassword(fields.get("reg-repeat-password"));
        setEmail(fields.get("reg-email"));
        setBirthDate(fields.get("reg-birthday"));
        setAgree(fields.get("reg-agree"));

        Map<String, FileItem> files = result.getFiles();
        if(files.containsKey("reg-avatar")) {
            setAvatar(files.get("reg-avatar"));
        }
    }

    public Map<String, String> getErrorMessages() {
        Map<String, String> result = new HashMap<>();

        if(isNullOrEmpty(login)) {
            result.put("login", "Login can't be null");
        }
        else if(!Pattern.matches("^[a-zA-Z0-9]+$", login)) {
            result.put("login", "Login has invalid format");
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
    private String avatar;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(FileItem item) throws ParseException {
        String submittedFileName = item.getName();

        String ext = FilenameUtils.getExtension(submittedFileName);
        boolean extAllowed = false;
        for (String extension : allowedAvatarExtensions) {
            extAllowed = extension.equalsIgnoreCase(ext);
            if(extAllowed) break;
        }

        if(!extAllowed) throw new ParseException("File extentions is not supported", 0);

        String savedFileName;
        File savedFile;
        String uploadPath = "D:\\Java\\JavaWeb201\\public\\avatars";

        do {
            savedFileName = String.format("%s.%s", UUID.randomUUID().toString().substring(0, 8), ext);
            savedFile = new File(String.format("%s\\%s", uploadPath, savedFileName));
        } while (savedFile.exists());

        File avatarsDirectory = new File(uploadPath);
        if(!avatarsDirectory.exists()) {
            avatarsDirectory.mkdirs();
        }

        try {
            item.write(savedFile);
            this.avatar = savedFileName;
        } catch (Exception e) {
            throw new ParseException("File upload error", 0);
        }
    }
    // endregion
}

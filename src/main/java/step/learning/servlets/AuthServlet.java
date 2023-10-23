package step.learning.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.AuthTokenDao;
import step.learning.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Singleton
public class AuthServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final AuthTokenDao authTokenDao;
    private final UserDao userDao;

    @Inject
    public AuthServlet(AuthTokenDao authTokenDao, UserDao userDao) {
        this.authTokenDao = authTokenDao;
        this.userDao = userDao;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(userDao.install()) {
            sendResponse(resp, 201, "Created");
        }
        else {
            sendResponse(resp, 500, "Server error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String contentType = req.getContentType();
        if(!contentType.startsWith("application/json")) {
            sendResponse(resp, 415, "Unsupported Media Type: 'application/json' only");
            return;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        String json;

        try(InputStream body = req.getInputStream()) {
            while ((len = body.read(buffer)) > 0) {
                bytes.write(buffer, 0, len);
            }
            json = bytes.toString(StandardCharsets.UTF_8.name());
        }
        catch (Exception ex) {
            sendResponse(resp, 500, "Server error");
            return;
        }

        String login;
        String password;
        try {
            JsonObject result = JsonParser.parseString(json).getAsJsonObject();
            JsonElement loginObj = result.get("login");
            JsonElement passwordObj = result.get("password");

            if(loginObj == null || loginObj.getAsString().isEmpty()) {
                throw new NullPointerException("login");
            }
            else if(passwordObj == null || passwordObj.getAsString().isEmpty()) {
                throw new NullPointerException("password");
            }

            login = loginObj.getAsString();
            password = passwordObj.getAsString();
        }
        catch (NullPointerException e) {
            resp.setStatus(400);
            sendResponse(resp, 400, String.format(gson.toJson("Missing required parameter: '%s'"), e.getMessage()));
            return;
        }
    }

    private void sendResponse(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(status);
        resp.getWriter().print(String.format(gson.toJson(body)));
    }
}
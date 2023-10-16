package step.learning.servlets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.db.DbProvider;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

@Singleton
public class DbServlet extends HttpServlet {
    private final DbProvider dbProvider;
    private final String dbPrefix;

    @Inject
    public DbServlet(DbProvider dbProvider, @Named("db-prefix") String dbPrefix) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String connectionStatus;
        try {
            dbProvider.getConnection();
            connectionStatus = "Connection ඞK";
        }
        catch (RuntimeException ex) {
            connectionStatus = "Connection error: " + ex.getMessage();
        }

        req.setAttribute("connectionStatus", connectionStatus);
        req.setAttribute("page-body", "db.jsp");
        req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int status;
        String message;
        String sql = String.format(
                "create table %scall_me (%s,%s,%s,%s) engine = InnoDB default charset = UTF8",
                dbPrefix,
                "id BINARY(16) primary key default (UUID_TO_BIN(UUID()))",
                "name varchar(128) null",
                "phone char(13) not null comment '+38 098 111 11 11'",
                "moment datetime default CURRENT_TIMESTAMP"
        );
        try(Statement statement = dbProvider.getConnection().createStatement()) {
            statement.execute(sql);
            status = 201;
            message = "Table created";
        } catch (SQLException e) {
            status = 500;
            message = e.getMessage();
        }

        JsonObject result = new JsonObject();
        result.addProperty("message", message);

        resp.setHeader("Content-Type", "application/json");
        resp.setStatus(status);
        resp.getWriter().print(result);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        String json;
        String message;
        int status = 201;
        JsonObject result = new JsonObject();
        try(InputStream body = req.getInputStream()) {
            while ((len = body.read(buffer)) > 0) {
                bytes.write(buffer, 0, len);
            }
            json = bytes.toString(StandardCharsets.UTF_8.name());
            JsonObject data = JsonParser.parseString(json).getAsJsonObject();
            String name = data.get("name").getAsString();
            String phone = data.get("name").getAsString();

            if(name == null || !Pattern.matches("^[a-zA-Z ]+$", name)) {
                message = "Name is empty or have wrong format";
                status = 400;
            }
            else if(phone == null || !Pattern.matches("\\+?[0-9 ]+$", phone)) {
                message = "Phone is empty or have wrong format";
                status = 400;
            }
            else {
                message = "Created";
            }

            result.addProperty("message", message);
        }
        catch (Exception ex) {
            json = ex.getMessage();
        }

        resp.setHeader("Content-Type", "application/json");
        resp.setStatus(status);
        resp.getWriter().print(result);
    }
}

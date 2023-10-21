package step.learning.servlets;

import com.google.gson.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.CallMeDao;
import step.learning.dto.entities.CallMe;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

@Singleton
public class DbServlet extends HttpServlet {
    private final DbProvider dbProvider;
    private final String dbPrefix;
    private final CallMeDao callMeDao;

    @Inject
    public DbServlet(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, CallMeDao callMeDao) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.callMeDao = callMeDao;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();

        switch (method) {
            case "PATCH": {
                doPatch(req, resp);
                break;
            }
            case "COPY": {
                doCopy(req, resp);
                break;
            }
            case "LINK": {
                doLink(req, resp);
                break;
            }
            case "RESTORE": {
                doRestore(req, resp);
                break;
            }
            default: super.service(req, resp);
        }
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
                "create table %scall_me (%s,%s,%s,%s,%s,%s) engine = InnoDB default charset = UTF8",
                dbPrefix,
                //"id BINARY(16) primary key default (UUID_TO_BIN(UUID()))",
                "`id` bigint unsigned primary key default (UUID_SHORT())",
                "`name` varchar(128) null",
                "`phone` char(13) not null comment '+38 098 111 11 11'",
                "`moment` datetime default CURRENT_TIMESTAMP",
                "`call_moment` datetime null",
                "`delete_moment` datetime null"
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
        String contentType = req.getContentType();
        if(!contentType.startsWith("application/json")) {
            resp.setStatus(415);
            resp.getWriter().print("\"Unsupported Media Type: 'application/json' only\"");
            return;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        String json;
        JsonObject data;
        int status = 201;
        JsonObject result = new JsonObject();

        try(InputStream body = req.getInputStream()) {
            while ((len = body.read(buffer)) > 0) {
                bytes.write(buffer, 0, len);
            }
            json = bytes.toString(StandardCharsets.UTF_8.name());


        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            resp.setStatus(500);
            resp.getWriter().print("\"Server error\"");
            return;
        }

        String name;
        String phone;
        String message;
        try {
            data = JsonParser.parseString(json).getAsJsonObject();
            JsonElement nameElem = data.get("name");
            JsonElement phoneElem = data.get("phone");

            if(nameElem == null) throw new NullPointerException("Field 'name' is required");
            if(phoneElem == null) throw new NullPointerException("Field 'phone' is required");

            name = nameElem.getAsString();
            phone = phoneElem.getAsString();

            if(name == null || !Pattern.matches("^[a-zA-Z ]+$", name)) {
                throw new NullPointerException("Name is empty or have wrong format");
            }
            else if(phone == null || !Pattern.matches("\\+?((\\d{12})|(\\d{2}(\\s?)\\(\\d{3}\\)(\\s?)\\d{3}[\\s-]\\d{2}[\\s-]\\d{2}))$", phone)) {
                throw new NullPointerException("Phone is empty or have wrong format");
            }
            else {
                message = "Created";
                phone = phone.replaceAll("[\\s()-]+", "");
            }

            resp.setHeader("Content-Type", "application/json");
        }
        catch (JsonParseException ex) {
            resp.setStatus(400);
            resp.getWriter().print("\"Invalid JSON object\"");
            return;
        }
        catch (NullPointerException ex) {
            result.addProperty("message", ex.getMessage());
            resp.setStatus(400);
            resp.getWriter().print(result);
            return;
        }

        String sqlInsert = "insert into " + dbPrefix + "call_me (name, phone) values (?, ?)";
        try(PreparedStatement preparedStatement = dbProvider.getConnection().prepareStatement(sqlInsert)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phone);
            preparedStatement.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
            resp.setStatus(500);
            resp.getWriter().print("\"Server error\"");
            return;
        }

        result.addProperty("name", name);
        result.addProperty("phone", phone);
        result.addProperty("message", message);

        resp.setStatus(status);
        resp.getWriter().print(result);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("call-id");
        resp.setContentType("application/json");

        if(id == null) {
            resp.setStatus(400);
            resp.getWriter().print("\"Missing parameter: 'call-id'\"");
            return;
        }

        CallMe callMe = callMeDao.getById(id);
        if(callMe == null) {
            resp.setStatus(404);
            resp.getWriter().print(String.format("\"Item with id %s not found\"", id));
            return;
        }

        boolean success = callMeDao.delete(callMe, true);
        if(!success) {
            resp.setStatus(500);
            resp.getWriter().print("\"Failed to delete\"");
            return;
        }

        resp.setStatus(204);
    }

    protected void doRestore(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("call-id");
        resp.setContentType("application/json");

        if(id == null) {
            resp.setStatus(400);
            resp.getWriter().print("\"Missing parameter: 'call-id'\"");
            return;
        }

        CallMe callMe = callMeDao.getById(id, true);
        if(callMe == null) {
            resp.setStatus(404);
            resp.getWriter().print(String.format("\"Item with id %s not found\"", id));
            return;
        }

        if(callMe.getDeleteMoment() == null) {
            resp.setStatus(409);
            resp.getWriter().print(String.format("\"Item with id %s is not deleted\"", id));
            return;
        }

        boolean success = callMeDao.restore(callMe.getId());
        if(!success) {
            resp.setStatus(500);
            resp.getWriter().print("\"Failed to restore\"");
            return;
        }

        resp.setStatus(204);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("call-id");
        resp.setContentType("application/json");

        if(id == null) {
            resp.setStatus(400);
            resp.getWriter().print("\"Missing parameter: 'call-id'\"");
            return;
        }

        CallMe callMe = callMeDao.getById(id);
        if(callMe == null) {
            resp.setStatus(404);
            resp.getWriter().print(String.format("\"Item with parameter %s not found\"", id));
            return;
        }

        boolean success = callMeDao.updateCallMoment(callMe);
        if(!success) {
            resp.setStatus(500);
            resp.getWriter().print("\"Failed to update call moment\"");
            return;
        }

        String response = new Gson().toJson(callMe);
        resp.setStatus(200);
        resp.getWriter().print(response);
    }

    protected void doCopy(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //boolean includeDeleted = req.getParameter("includeDeleted") != null;
        boolean includeDeleted = req.getParameter("includeDeleted") != null && req.getParameter("includeDeleted").equals("true");
        List<CallMe> calls = callMeDao.getAll(includeDeleted);
        Gson gson = new GsonBuilder().create();
        resp.getWriter().print(gson.toJson(calls));
    }

    protected void doLink(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String contentType = req.getContentType();
        resp.setContentType("application/json");
        if(!contentType.startsWith("application/json")) {
            resp.setStatus(415);
            resp.getWriter().print("\"Unsupported Media Type: 'application/json' only\"");
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
            System.err.println(ex.getMessage());
            resp.setStatus(500);
            resp.getWriter().print("\"Server error\"");
            return;
        }

        long id;
        try {
            JsonObject result = JsonParser.parseString(json).getAsJsonObject();
            id = result.get("id").getAsLong();
        }
        catch (NullPointerException e) {
            System.err.println(e.getMessage());
            resp.setStatus(400);
            resp.getWriter().print("\"Missing required parameter: 'id'\"");
            return;
        }

        String sql = "update " + dbPrefix + "call_me set call_moment=? where id=?";
        Timestamp callTimestamp;
        try(PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
            callTimestamp = new Timestamp(getUTCDate().getTime());
            statement.setTimestamp(1, callTimestamp);
            statement.setLong(2, id);
            statement.execute();
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            resp.setStatus(500);
            resp.getWriter().print("\"Server error\"");
            return;
        }

        Gson gson = new GsonBuilder().create();
        JsonObject response = new JsonObject();
        response.addProperty("timestamp", callTimestamp.getTime());

        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().print(gson.toJson(response));
    }

    private Date getUTCDate() throws ParseException {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        //Local time zone
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

        //Time in GMT
        return dateFormatLocal.parse( dateFormatGmt.format(new Date()) );
    }
}

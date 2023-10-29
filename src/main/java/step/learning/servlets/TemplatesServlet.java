package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.dao.AuthTokenDao;
import step.learning.dto.entities.AuthToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class TemplatesServlet extends HttpServlet {
    final static byte[] buffer = new byte[16384];
    private static final String[] dtSymbols = new String[] {"..", "../", "\\", ":", "+"};
    private final AuthTokenDao tokenDao;
    private final Logger logger;

    @Inject
    public TemplatesServlet(AuthTokenDao tokenDao, Logger logger) {
        this.tokenDao = tokenDao;
        this.logger = logger;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authHeader = req.getHeader("Authorization");
        if(authHeader == null) {
            sendResponse(resp, 400, "'Authorization' header required");
            return;
        }
        String authScheme = "Bearer ";
        if(!authHeader.startsWith(authScheme)) {
            sendResponse(resp, 400, "'Bearer' scheme required");
            return;
        }

        String tokenBase64 = authHeader.substring(authScheme.length());
        AuthToken token = tokenDao.getTokenByBearer(tokenBase64);

        if(token == null) {
            sendResponse(resp, 401, "Token rejected");
            return;
        }


        String requestedTemplate = req.getPathInfo();
        for (String sym : dtSymbols) {
            if(requestedTemplate.contains(sym)) {
                sendResponse(resp, 403, "Invalid symbols in file name");
                return;
            }
        }

        URL url = getClass().getClassLoader().getResource("tpl" + requestedTemplate);
        if(url == null) {
            resp.setStatus(404);
            return;
        }

        File template = new File(url.getPath());
        if(!template.exists() || !template.isFile()) {
            resp.setStatus(404);
            return;
        }

        resp.setContentType(URLConnection.getFileNameMap().getContentTypeFor(requestedTemplate));
        OutputStream outputStream = resp.getOutputStream();
        try(InputStream inputStream = Files.newInputStream(template.toPath())) {
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
        }
        catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            resp.setStatus(500);
            return;
        }

        resp.setStatus(200);
    }

    private void sendResponse(HttpServletResponse resp, int status, String body) throws IOException {
        resp.setContentType("text/plain");
        resp.setStatus(status);
        resp.getWriter().print(body);
    }
}

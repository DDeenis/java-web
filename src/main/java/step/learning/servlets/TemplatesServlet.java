package step.learning.servlets;

import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

@Singleton
public class TemplatesServlet extends HttpServlet {
    final static byte[] buffer = new byte[16384];

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //resp.setContentType("text/html");
        String requestedTemplate = req.getPathInfo();
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
        OutputStream outputStream = resp.getOutputStream();
        try(InputStream inputStream = Files.newInputStream(template.toPath())) {
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        catch (IOException e) {
            resp.setStatus(500);
            return;
        }

        resp.setStatus(200);
    }
}

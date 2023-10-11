package step.learning.servlets;

import com.google.inject.Singleton;
import step.learning.dto.models.RegFormModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;

@Singleton
public class SignUpServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer regStatus = (Integer)session.getAttribute("reg-status");
        if(regStatus != null) {
            session.removeAttribute("reg-status");
            String message;
            if(regStatus == 0) {
                message = "Failed to process form";
            }
            else if(regStatus == 1) {
                message = "Failed to validate form";
                req.setAttribute("reg-model", session.getAttribute("reg-model"));
                session.removeAttribute("reg-model");
            }
            else {
                message = "Registration successful";
            }
            req.setAttribute("reg-message", message);
        }

        req.setAttribute("page-body", "signup.jsp");
        req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RegFormModel model = null;
        try {
            model = new RegFormModel(req);
        } catch (ParseException ignored) {
        }

        HttpSession session = req.getSession();
        if(model == null) {
            session.setAttribute("reg-status", 0);
            //resp.setStatus(422);
        }
        else if(!model.getErrorMessages().isEmpty()) {
            req.getSession().setAttribute("reg-model", model);
            session.setAttribute("reg-status", 1);
            //resp.setStatus(400);
        }
        else {
            session.setAttribute("reg-status", 2);
            //resp.setStatus(201);
        }

        resp.sendRedirect(req.getRequestURI());
    }
}

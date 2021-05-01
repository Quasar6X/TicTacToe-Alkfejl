package hu.alkfejl.controller.exception;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ExceptionHandler")
public final class ExceptionHandler extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.getSession().invalidate();
            var out = resp.getWriter();
            out.write("<html lang=\"en\"><head><title>Our bad :(</title><link href=\"img/logo.png\" rel=\"icon\"><link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6\" crossorigin=\"anonymous\"></head><body>");
            out.write("<div class=\"container d-grid gap-2 col-3 mx-auto mt-3\"><div class=\"card\"><div class=\"card-body\"><h5 class=\"card-title\">Oh-Oh something went wrong on our side...</h5><p class=\"card-text\">We have safely logged you out.</p><a href=\"../index.jsp\" class=\"btn btn-primary\">Back to home</a>");
            out.write("</div></div></div></body></html>");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}

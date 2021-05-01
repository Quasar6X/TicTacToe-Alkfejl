package hu.alkfejl.controller.page;

import hu.alkfejl.constants.Constants;
import hu.alkfejl.listener.OnlineUserListener;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/WelcomeController")
public final class WelcomeController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var session = req.getSession(true);
        var listener = (OnlineUserListener) session.getServletContext().getAttribute(OnlineUserListener.KEY);
        var players = listener.onlinePlayers();
        final int parameterID = Integer.parseInt(req.getParameter(Constants.FORM_INPUT_NAME_PLAYER_ID));

        for (var player: players) {
            if (player.getId() == parameterID) {
                session.invalidate();
                resp.setContentType("text/html");
                resp.getWriter().append("<html lang=\"en\"><script>alert('Player is already logged in!'); window.location.replace('index.jsp');</script></html>");
                resp.getWriter().flush();
                return;
            }
        }

        session.setAttribute(Constants.SESSION_PLAYER_ID, parameterID);
        session.setAttribute(Constants.SESSION_PLAYER_NAME, req.getParameter(Constants.FORM_INPUT_NAME_PLAYER_NAME));
        resp.sendRedirect("pages/welcome.jsp");
    }
}

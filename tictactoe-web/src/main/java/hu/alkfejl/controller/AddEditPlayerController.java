package hu.alkfejl.controller;

import hu.alkfejl.connection.ConnectionManagerServer;
import hu.alkfejl.constants.Constants;
import hu.alkfejl.model.PlayerHuman;
import hu.alkfejl.model.dao.SimplePlayerDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/AddEditPlayerController")
public final class AddEditPlayerController extends HttpServlet {

    @Override
    public void init() {
        var manager = new ConnectionManagerServer();
        if (!SimplePlayerDAO.getInstance().isValid()) SimplePlayerDAO.getInstance().setConnectionManager(manager);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var names = new ArrayList<String>();
        SimplePlayerDAO.getInstance().findAll().forEach(player -> names.add(player.getName().toLowerCase()));
        boolean f = false;
        for (var name : names)
            if (name.equals(req.getParameter(Constants.FORM_INPUT_NAME_PLAYER_NAME).toLowerCase()))
                f = true;

        if (f) {
            resp.setContentType("text/html");
            resp.getWriter().append("<html lang=\"en\"><script>alert('The name is already taken!'); window.location.replace('index.jsp');</script></html>");
            resp.getWriter().flush();
        } else {
            PlayerHuman player;

            if (req.getParameter(Constants.FORM_INPUT_NAME_PLAYER_ID) != null)
                player = (PlayerHuman) SimplePlayerDAO.getInstance().findAll().stream().filter(p -> p.getId() == Integer.parseInt(req.getParameter(Constants.FORM_INPUT_NAME_PLAYER_ID))).findFirst().orElseThrow();
            else
                player = new PlayerHuman();

            player.setName(req.getParameter(Constants.FORM_INPUT_NAME_PLAYER_NAME));
            SimplePlayerDAO.getInstance().save(player);
            resp.sendRedirect("index.jsp");
        }
    }
}

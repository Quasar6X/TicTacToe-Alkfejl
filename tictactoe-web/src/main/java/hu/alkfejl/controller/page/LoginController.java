package hu.alkfejl.controller.page;

import hu.alkfejl.connection.ConnectionManagerServer;
import hu.alkfejl.constants.Constants;
import hu.alkfejl.listener.OnlineUserListener;
import hu.alkfejl.model.PlayerHuman;
import hu.alkfejl.model.dao.SimplePlayerDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

@WebServlet("/LoginController")
public final class LoginController extends HttpServlet {

    @Override
    public void init() {
        SimplePlayerDAO.getInstance().setConnectionManager(new ConnectionManagerServer());
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        var players = SimplePlayerDAO.getInstance().findAll();
        var session = req.getSession(true);
        var loggedInPlayerNames = ((OnlineUserListener) session.getServletContext().getAttribute(OnlineUserListener.KEY)).onlinePlayers().stream().map(PlayerHuman::getName).collect(Collectors.toList());
        req.setAttribute(Constants.ATTRIBUTE_PLAYER_LIST, players.stream().filter(player -> !loggedInPlayerNames.contains(player.getName())).collect(Collectors.toList()));
        session.invalidate();
    }
}

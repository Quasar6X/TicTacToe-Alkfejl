package hu.alkfejl.controller.page;

import hu.alkfejl.constants.Constants;
import hu.alkfejl.listener.OnlineUserListener;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.stream.Collectors;

@WebServlet("/SetupGameController")
public final class SetupGameController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        int p1ID = (int) req.getSession().getAttribute(Constants.SESSION_PLAYER_ID);
        var onlinePlayers = ((OnlineUserListener) req.getSession().getServletContext()
                .getAttribute(OnlineUserListener.KEY))
                .onlinePlayers().stream().filter(playerHuman -> playerHuman.getId() != p1ID).collect(Collectors.toList());

        var playerIDs = new ArrayList<Integer>();
        var playerNames = new ArrayList<String>();
        onlinePlayers.forEach(player -> {
            playerIDs.add(player.getId());
            playerNames.add(player.getName());
        });

        if (onlinePlayers.isEmpty())
            req.setAttribute(Constants.ATTRIBUTE_IS_EMPTY, true);
        else
            req.setAttribute(Constants.ATTRIBUTE_IS_EMPTY, false);

        req.setAttribute(Constants.ATTRIBUTE_PLAYER_ID_LIST, playerIDs);
        req.setAttribute(Constants.ATTRIBUTE_PLAYER_NAME_LIST, playerNames);
    }
}

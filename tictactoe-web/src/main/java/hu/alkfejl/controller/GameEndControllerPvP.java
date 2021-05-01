package hu.alkfejl.controller;

import com.google.gson.Gson;
import hu.alkfejl.connection.ConnectionManagerServer;
import hu.alkfejl.constants.Constants;
import hu.alkfejl.listener.OnlineUserListener;
import hu.alkfejl.model.Player;
import hu.alkfejl.model.dao.SimpleMoveDAO;
import hu.alkfejl.model.dao.SimplePartyDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/GameEndControllerPvP")
public final class GameEndControllerPvP extends AbstractPvPBase implements StateFinder {

    private static final SimplePartyDAO partyDAO = SimplePartyDAO.getInstance();
    private static final SimpleMoveDAO moveDAO = SimpleMoveDAO.getInstance();

    @Override
    public void init() {
        var manager = new ConnectionManagerServer();
        if (!moveDAO.isValid()) moveDAO.setConnectionManager(manager);
        if (!partyDAO.isValid()) partyDAO.setConnectionManager(manager);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (req.getParameter(Constants.AJAX_DATA_SAVE_GAME).equals("true")) {
            var state = getStateFromOrdinal(Integer.parseInt(req.getParameter(Constants.AJAX_DATA_STATE)));
            var moves = getMoveList(req);
            var gameLogic = getGameLogic(req);

            String responseText;
            final var listener = (OnlineUserListener) req.getSession().getServletContext().getAttribute(OnlineUserListener.KEY);
            final var otherSession = listener.findSessionFromPlayerID((int) req.getSession().getAttribute(Constants.SESSION_OPPONENT_PLAYER_ID));

            switch (state) {
                case XWin:
                    gameLogic.getParty().setWinnerID(req.getSession().getAttribute(Constants.SESSION_PLAYER_SYMBOL) == Player.Symbol.X ?
                            (int) req.getSession().getAttribute(Constants.SESSION_PLAYER_ID) :
                            (int) req.getSession().getAttribute(Constants.SESSION_OPPONENT_PLAYER_ID));
                    if (req.getSession().getAttribute(Constants.SESSION_PLAYER_SYMBOL) == Player.Symbol.X) {
                        responseText = req.getSession().getAttribute(Constants.SESSION_PLAYER_NAME) + " wins!";
                    }
                    else {
                        responseText = otherSession.getAttribute(Constants.SESSION_PLAYER_NAME) + " wins!";
                    }
                    break;
                case OWin:
                    gameLogic.getParty().setWinnerID(req.getSession().getAttribute(Constants.SESSION_PLAYER_SYMBOL) == Player.Symbol.O ?
                            (int) req.getSession().getAttribute(Constants.SESSION_PLAYER_ID) :
                            (int) req.getSession().getAttribute(Constants.SESSION_OPPONENT_PLAYER_ID));
                    if (req.getSession().getAttribute(Constants.SESSION_PLAYER_SYMBOL) == Player.Symbol.X) {
                        responseText = req.getSession().getAttribute(Constants.SESSION_PLAYER_NAME) + " wins!";
                    }
                    else {
                        responseText = otherSession.getAttribute(Constants.SESSION_PLAYER_NAME) + " wins!";
                    }
                    break;
                default:
                    responseText = "It's a draw";
            }

            var savedParty = partyDAO.save(gameLogic.getParty());
            if (savedParty != null)
                moves.forEach(move -> {
                    move.setPartyID(savedParty.getId());
                    moveDAO.save(move);
                });
            otherSession.setAttribute(Constants.SESSION_GAME_RESULT, responseText);
            req.getSession().setAttribute(Constants.SESSION_GAME_RESULT, responseText);
        } else {
            req.getSession().setAttribute(Constants.SESSION_GAME_RESULT, "It's a draw");
        }
        resp.getWriter().print(new Gson().toJson("game_result_page.jsp"));
        resp.getWriter().flush();
    }
}

package hu.alkfejl.controller;

import com.google.gson.Gson;
import hu.alkfejl.connection.ConnectionManagerServer;
import hu.alkfejl.constants.Constants;
import hu.alkfejl.model.Move;
import hu.alkfejl.model.SimpleGameLogic;
import hu.alkfejl.model.dao.SimpleMoveDAO;
import hu.alkfejl.model.dao.SimplePartyDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/GameEndControllerPvA")
public final class GameEndControllerPvA extends HttpServlet implements StateFinder {

    private static final SimplePartyDAO partyDAO = SimplePartyDAO.getInstance();
    private static final SimpleMoveDAO moveDAO = SimpleMoveDAO.getInstance();

    private final List<Move> moves = new ArrayList<>();

    @Override
    public void init() {
        var manager = new ConnectionManagerServer();
        if (!moveDAO.isValid()) moveDAO.setConnectionManager(manager);
        if (!partyDAO.isValid()) partyDAO.setConnectionManager(manager);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var state = getStateFromOrdinal(Integer.parseInt(req.getParameter(Constants.AJAX_DATA_STATE)));
        fillMoves(Collections.list(req.getParameterNames()).stream().filter(name -> !Constants.AJAX_DATA_STATE.equals(name)).collect(Collectors.toList()), req);

        var gameLogic = (SimpleGameLogic) req.getSession().getAttribute(Constants.SESSION_GAME_STATE);

        String responseText;

        switch (state) {
            case XWin:
                gameLogic.getParty().setWinnerID((int) req.getSession().getAttribute(Constants.SESSION_PLAYER_ID));
                responseText = "You WIN!";
                break;
            case OWin:
                responseText = "You LOST :'(";
                gameLogic.getParty().setWinnerID(1);
                break;
            default:
                responseText = "It's a draw";
        }

        var savedParty = partyDAO.save(gameLogic.getParty());
        if (savedParty != null) {
            moves.forEach(move -> {
                move.setPartyID(savedParty.getId());
                moveDAO.save(move);
            });
        }

        req.getSession().setAttribute(Constants.SESSION_GAME_RESULT, responseText);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(200);
        String link = "game_result_page.jsp";
        resp.getWriter().print(new Gson().toJson(link));
        resp.getWriter().flush();
    }

    private void fillMoves(final List<String> names, final HttpServletRequest req) {
        int id = -1;
        String tile = "";
        int playerID = -1;
        int partyID;
        int index = 0;

        for (var name : names) {
            if (name.contains("[" + index + "]")) {
                if (name.contains("[id]"))
                    id = Integer.parseInt(req.getParameter(name));
                else if (name.contains("[tile]"))
                    tile = req.getParameter(name);
                else if (name.contains("[playerID]"))
                    playerID = Integer.parseInt(req.getParameter(name));
                else if (name.contains("[partyID]")) {
                    partyID = Integer.parseInt(req.getParameter(name));
                    if (id != -1 && !tile.isEmpty() && playerID != -1 && partyID != -1) {
                        var move = new Move();
                        move.setId(id);
                        move.setTile(tile);
                        move.setPlayerID(playerID);
                        move.setPartyID(partyID);
                        moves.add(move);
                        ++index;
                    }
                }
            }
        }
    }
}

package hu.alkfejl.controller;

import com.google.gson.Gson;
import hu.alkfejl.constants.Constants;
import hu.alkfejl.model.*;
import hu.alkfejl.response.PvAResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PvAController")
public final class PvAController extends HttpServlet {

    @Override
    @SuppressWarnings("DuplicatedCode")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        final var gson = new Gson();
        var gameLogic = (SimpleGameLogic) req.getSession().getAttribute(Constants.SESSION_GAME_STATE);
        if (req.getParameter(Constants.FORM_INPUT_NAME_COORDINATES) != null) {
            try {
                var coords = req.getParameter(Constants.FORM_INPUT_NAME_COORDINATES);
                var xy = coords.split("_");
                int x = Integer.parseInt(xy[0]);
                int y = Integer.parseInt(xy[1]);
                if (!gameLogic.getBoard()[x][y].getValue().equals(GameLogic.Tile.NONE))
                    return;
                var p1 = new PlayerHuman();
                p1.setId((int) req.getSession().getAttribute(Constants.SESSION_PLAYER_ID));
                p1.setName((String) req.getSession().getAttribute(Constants.SESSION_PLAYER_NAME));
                p1.setSymbol(Player.Symbol.X);

                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                var pvAResponse = new PvAResponse();
                var out = resp.getWriter();

                var move = gameLogic.move(p1, x, y);
                pvAResponse.setPlayerMove(SimpleMove.fromMove(move));
                var state = gameLogic.checkState();
                if (state != GameLogic.State.NONE) {
                    pvAResponse.setState(state.ordinal());
                    out.print(gson.toJson(pvAResponse));
                    out.flush();
                    return;
                }

                var aiMove = aiMakeMove(gameLogic);
                pvAResponse.setAiMove(SimpleMove.fromMove(aiMove));
                var state2 = gameLogic.checkState();
                if (state2 != GameLogic.State.NONE) {
                    pvAResponse.setState(state2.ordinal());
                }
                out.print(gson.toJson(pvAResponse));
                out.flush();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        } else if (req.getParameter(Constants.AJAX_DATA_SKIP_TURN) != null && req.getParameter(Constants.AJAX_DATA_SKIP_TURN).equals("true")) {
            try {
                gameLogic.flipTurn();
                var aiMove = aiMakeMove(gameLogic);
                var pvAResponse = new PvAResponse();
                pvAResponse.setAiMove(SimpleMove.fromMove(aiMove));
                var state = gameLogic.checkState();
                if (state != GameLogic.State.NONE)
                    pvAResponse.setState(state.ordinal());
                resp.getWriter().print(gson.toJson(pvAResponse));
                resp.getWriter().flush();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Move aiMakeMove(final GameLogic gameLogic) {
        var ai = new PlayerAI();
        var aiXY = ai.randomMove(gameLogic.getParty().getBoardSize());

        while (!(gameLogic.getBoard()[aiXY.getKey()][aiXY.getValue()].getValue().equals(GameLogic.Tile.NONE)))
            aiXY = ai.randomMove(gameLogic.getParty().getBoardSize());

        return gameLogic.move(ai, aiXY.getKey(), aiXY.getValue());
    }
}

package hu.alkfejl.controller;

import com.google.gson.Gson;
import hu.alkfejl.constants.Constants;
import hu.alkfejl.listener.OnlineUserListener;
import hu.alkfejl.model.*;
import hu.alkfejl.response.MoveListResponse;
import hu.alkfejl.response.PvPResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PvPController")
public final class PvPController extends AbstractPvPBase {

    @Override
    @SuppressWarnings("DuplicatedCode")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        final var gson = new Gson();
        if (req.getParameter(Constants.AJAX_SEND_MOVES) != null && req.getParameter(Constants.AJAX_SEND_MOVES).equals("true")) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            try {
                final var moveListResponse = new MoveListResponse();

                final var listener = (OnlineUserListener) req.getSession().getServletContext().getAttribute(OnlineUserListener.KEY);
                final var otherSession = listener.findSessionFromPlayerID((int) req.getSession().getAttribute(Constants.SESSION_OPPONENT_PLAYER_ID));

                if (otherSession == null || otherSession.getAttribute(Constants.SESSION_OPPONENT_PLAYER_ID) == null) {
                    moveListResponse.setState(GameLogic.State.DRAW.ordinal());
                    moveListResponse.setOtherPlayerLeft(true);
                    resp.getWriter().print(gson.toJson(moveListResponse));
                    resp.getWriter().flush();
                    return;
                }

                final var gameLogic = getGameLogic(req);
                if (gameLogic.getTurn() == Player.Symbol.X)
                    moveListResponse.setTurn("X");
                else
                    moveListResponse.setTurn("O");

                final var moves = getMoveList(req);
                moveListResponse.setState(gameLogic.checkState().ordinal());

                moves.forEach(move -> {
                    moveListResponse.getMoves().add(SimpleMove.fromMove(move));
                    if (move.getPlayerID() == (int) req.getSession().getAttribute(Constants.SESSION_PLAYER_ID)) {
                        if (req.getSession().getAttribute(Constants.SESSION_PLAYER_SYMBOL) == Player.Symbol.X)
                            moveListResponse.getSymbols().add("X");
                        else
                            moveListResponse.getSymbols().add("O");
                    } else {
                        if (otherSession.getAttribute(Constants.SESSION_PLAYER_SYMBOL) == Player.Symbol.X)
                            moveListResponse.getSymbols().add("X");
                        else
                            moveListResponse.getSymbols().add("O");
                    }
                });

                resp.getWriter().print(gson.toJson(moveListResponse));
                resp.getWriter().flush();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        } else if (req.getParameter(Constants.FORM_INPUT_NAME_COORDINATES) != null) {
            var gameLogic = getGameLogic(req);
            var symbol = (Player.Symbol) req.getSession().getAttribute(Constants.SESSION_PLAYER_SYMBOL);
            try {
                if (gameLogic.getTurn() == symbol) {
                    var coords = req.getParameter(Constants.FORM_INPUT_NAME_COORDINATES);
                    var xy = coords.split("_");
                    int x = Integer.parseInt(xy[0]);
                    int y = Integer.parseInt(xy[1]);
                    if (!gameLogic.getBoard()[x][y].getValue().equals(GameLogic.Tile.NONE))
                        return;
                    var player = new PlayerHuman();
                    player.setId((int) req.getSession().getAttribute(Constants.SESSION_PLAYER_ID));
                    player.setName((String) req.getSession().getAttribute(Constants.SESSION_PLAYER_NAME));
                    player.setSymbol(symbol);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    final var pvpResponse = new PvPResponse();
                    final var out = resp.getWriter();

                    var move = gameLogic.move(player, x, y);
                    getMoveList(req).add(move);

                    pvpResponse.setPlayerMove(SimpleMove.fromMove(move));
                    pvpResponse.setSymbol(symbol.ordinal());
                    var state = gameLogic.checkState();
                    if (state != GameLogic.State.NONE) {
                        pvpResponse.setState(state.ordinal());
                        pvpResponse.setValid(true);
                        out.print(gson.toJson(pvpResponse));
                        out.flush();
                        return;
                    }
                    pvpResponse.setState(state.ordinal());
                    pvpResponse.setValid(true);
                    out.print(gson.toJson(pvpResponse));
                    out.flush();
                } else {
                    final var pvpResponse = new PvPResponse();
                    pvpResponse.setValid(false);
                    resp.getWriter().print(gson.toJson(pvpResponse));
                    resp.getWriter().flush();
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}

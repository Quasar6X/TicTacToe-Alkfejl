package hu.alkfejl.controller;

import hu.alkfejl.constants.Constants;
import hu.alkfejl.listener.OnlineUserListener;
import hu.alkfejl.model.Move;
import hu.alkfejl.model.SimpleGameLogic;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public abstract class AbstractPvPBase extends HttpServlet {

    public SimpleGameLogic getGameLogic(final HttpServletRequest req) {
        var ret = (SimpleGameLogic) req.getSession().getAttribute(Constants.SESSION_GAME_STATE);
        if (ret != null)
            return ret;
        else {
            var listener = (OnlineUserListener) req.getSession().getServletContext().getAttribute(OnlineUserListener.KEY);
            var otherSession = listener.findSessionFromPlayerID((int) req.getSession().getAttribute(Constants.SESSION_OPPONENT_PLAYER_ID));
            return (SimpleGameLogic) otherSession.getAttribute(Constants.SESSION_GAME_STATE);
        }
    }

    // type erasure ノಠ益ಠノ彡┻━┻
    @SuppressWarnings("unchecked")
    public ArrayList<Move> getMoveList(final HttpServletRequest req) {
        var list = (ArrayList<Move>) req.getSession().getAttribute(Constants.SESSION_MOVE_LIST);
        if (list != null)
            return list;
        else {
            var listener = (OnlineUserListener) req.getSession().getServletContext().getAttribute(OnlineUserListener.KEY);
            var otherSession = listener.findSessionFromPlayerID((int) req.getSession().getAttribute(Constants.SESSION_OPPONENT_PLAYER_ID));
            return (ArrayList<Move>) otherSession.getAttribute(Constants.SESSION_MOVE_LIST);
        }
    }
}

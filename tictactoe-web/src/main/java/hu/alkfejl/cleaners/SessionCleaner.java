package hu.alkfejl.cleaners;

import hu.alkfejl.constants.Constants;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Called after a PvA to cleanup the session variables of the player
 */
@WebServlet("/SessionCleaner")
public final class SessionCleaner extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getSession() != null) {
            if (req.getSession().getAttribute(Constants.SESSION_GAME_STATE) != null)
                req.getSession().removeAttribute(Constants.SESSION_GAME_STATE);
            if (req.getSession().getAttribute(Constants.SESSION_GAME_RESULT) != null)
                req.getSession().removeAttribute(Constants.SESSION_GAME_RESULT);
            if (req.getSession().getAttribute(Constants.SESSION_BOARD_SIZE) != null)
                req.getSession().removeAttribute(Constants.SESSION_BOARD_SIZE);
            if (req.getSession().getAttribute(Constants.SESSION_MOVE_LIST) != null)
                req.getSession().removeAttribute(Constants.SESSION_MOVE_LIST);
            if (req.getSession().getAttribute(Constants.SESSION_OPPONENT_PLAYER_ID) != null)
                req.getSession().removeAttribute(Constants.SESSION_OPPONENT_PLAYER_ID);
            if (req.getSession().getAttribute(Constants.SESSION_PLAYER_SYMBOL) != null)
                req.getSession().removeAttribute(Constants.SESSION_PLAYER_SYMBOL);
        }
    }
}

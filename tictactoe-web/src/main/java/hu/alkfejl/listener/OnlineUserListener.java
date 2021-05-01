package hu.alkfejl.listener;

import hu.alkfejl.constants.Constants;
import hu.alkfejl.model.PlayerHuman;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.*;

/**
 * Stores the online sessios in a set so you can retrive players, and their sessions
 * Synchronized access to the set is required!
 */
@WebListener("OnlineUserListener")
public final class OnlineUserListener implements HttpSessionListener {

    private final Set<HttpSession> activeSessions = Collections.synchronizedSet(new HashSet<>());
    public static final String KEY = OnlineUserListener.class.getName();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        var session = se.getSession();
        registerInServletContext(session.getServletContext());
        synchronized (activeSessions) {
            activeSessions.add(session);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        synchronized (activeSessions) {
            activeSessions.remove(se.getSession());
        }
    }

    private synchronized void registerInServletContext(final ServletContext servletContext) {
        if (servletContext.getAttribute(KEY) == null)
            servletContext.setAttribute(KEY, this);
    }

    public synchronized List<PlayerHuman> onlinePlayers() {
        var ret = new ArrayList<PlayerHuman>();
        activeSessions.forEach(session -> {
            if (session != null && session.getAttribute(Constants.SESSION_PLAYER_ID) != null && session.getAttribute(Constants.SESSION_PLAYER_NAME) != null) {
                var player = new PlayerHuman();
                player.setId((int) session.getAttribute(Constants.SESSION_PLAYER_ID));
                player.setName((String) session.getAttribute(Constants.SESSION_PLAYER_NAME));
                ret.add(player);
            }
        });
        return ret;
    }

    public synchronized HttpSession findSessionFromPlayerID(final int playerID) {
        return activeSessions.stream().filter(httpSession ->
                httpSession.getAttribute(Constants.SESSION_PLAYER_ID) != null && (int) httpSession.getAttribute(Constants.SESSION_PLAYER_ID) == playerID)
                .findFirst().orElse(null);
    }
}

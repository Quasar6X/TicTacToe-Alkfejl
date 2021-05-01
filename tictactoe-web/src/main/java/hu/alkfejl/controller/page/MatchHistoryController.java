package hu.alkfejl.controller.page;

import hu.alkfejl.connection.ConnectionManagerServer;
import hu.alkfejl.constants.Constants;
import hu.alkfejl.model.Player;
import hu.alkfejl.model.dao.SimpleMoveDAO;
import hu.alkfejl.model.dao.SimplePartyDAO;
import hu.alkfejl.model.dao.SimplePlayerDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.stream.Collectors;

@WebServlet("/MatchHistoryController")
public final class MatchHistoryController extends HttpServlet {

    private static final SimplePartyDAO partyDAO = SimplePartyDAO.getInstance();
    private static final SimplePlayerDAO playerDAO = SimplePlayerDAO.getInstance();
    private static final SimpleMoveDAO moveDAO = SimpleMoveDAO.getInstance();

    @Override
    public void init() {
        var manager = new ConnectionManagerServer();
        if (!moveDAO.isValid()) moveDAO.setConnectionManager(manager);
        if (!playerDAO.isValid()) playerDAO.setConnectionManager(manager);
        if (!partyDAO.isValid()) partyDAO.setConnectionManager(manager);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        var parties = partyDAO.findAll();
        var players = playerDAO.findAll();
        var moves = moveDAO.findAll();


        var winners = new ArrayList<Player>();
        var p1s = new ArrayList<Player>();
        var p2s = new ArrayList<Player>();

        parties.forEach(party -> winners.add(players.stream().filter(player -> player.getId() == party.getWinnerID()).findAny().orElse(null)));
        parties.forEach(party -> {
            var playerIDcache = new ArrayList<Integer>();
            moves.stream().filter(move -> move.getPartyID() == party.getId()).collect(Collectors.toList()).forEach(move -> {
                if (!playerIDcache.contains(move.getPlayerID()))
                    playerIDcache.add(move.getPlayerID());
            });
            players.stream().filter(player -> player.getId() == playerIDcache.get(0)).findAny().ifPresent(p1s::add);
            players.stream().filter(player -> player.getId() == playerIDcache.get(playerIDcache.size() - 1)).findAny().ifPresent(p2s::add);
        });

        int upperBound = p1s.size();
        int sessionplayerID = (int) req.getSession().getAttribute(Constants.SESSION_PLAYER_ID);
        for (int i = 0; i < upperBound; i++) {
            if (p1s.get(i).getId() != sessionplayerID && p2s.get(i).getId() != sessionplayerID) {
                p1s.remove(i);
                p2s.remove(i);
                parties.remove(i);
                winners.remove(i);
                --upperBound;
                --i;
            }
        }

        if (parties.isEmpty())
            req.setAttribute(Constants.ATTRIBUTE_IS_EMPTY, true);
        else {
            req.setAttribute(Constants.ATTRIBUTE_IS_EMPTY, false);
            req.setAttribute(Constants.ATTRIBUTE_PARTY_LIST, parties);
            req.setAttribute(Constants.ATTRIBUTE_PLAYER_1_LIST, p1s);
            req.setAttribute(Constants.ATTRIBUTE_PLAYER_2_LIST, p2s);
            req.setAttribute(Constants.ATTRIBUTE_WINNER_LIST, winners);
        }
    }
}

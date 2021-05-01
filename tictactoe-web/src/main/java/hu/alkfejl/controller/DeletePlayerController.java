package hu.alkfejl.controller;

import hu.alkfejl.connection.ConnectionManagerServer;
import hu.alkfejl.constants.Constants;
import hu.alkfejl.model.dao.SimpleMoveDAO;
import hu.alkfejl.model.dao.SimplePartyDAO;
import hu.alkfejl.model.dao.SimplePlayerDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@WebServlet("/DeletePlayerController")
public final class DeletePlayerController extends HttpServlet {

    private static final SimpleMoveDAO moveDAO = SimpleMoveDAO.getInstance();
    private static final SimplePlayerDAO playerDAO = SimplePlayerDAO.getInstance();
    private static final SimplePartyDAO partyDAO = SimplePartyDAO.getInstance();

    @Override
    public void init() {
        var manager = new ConnectionManagerServer();
        if (!moveDAO.isValid()) moveDAO.setConnectionManager(manager);
        if (!playerDAO.isValid()) playerDAO.setConnectionManager(manager);
        if (!partyDAO.isValid()) partyDAO.setConnectionManager(manager);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getParameter(Constants.FORM_INPUT_NAME_PLAYER_ID) != null) {
            var moves = moveDAO.findAll();
            var movesOfPlayer = moves.stream().filter(move -> move.getPlayerID() == Integer.parseInt(req.getParameter(Constants.FORM_INPUT_NAME_PLAYER_ID))).collect(Collectors.toList());
            var idCache = new ArrayList<Integer>();
            movesOfPlayer.forEach(move -> {
                if (!idCache.contains(move.getPartyID())) {
                    idCache.add(move.getPartyID());
                }
            });
            idCache.forEach(partyID -> {
                moves.stream().filter(move -> move.getPartyID() == partyID).collect(Collectors.toList()).forEach(moveDAO::delete);
                partyDAO.findAll().stream().filter(party -> party.getId() == partyID).findAny().ifPresent(partyDAO::delete);
            });

            playerDAO.findAll().stream().filter(p -> p.getId() == Integer.parseInt(req.getParameter(Constants.FORM_INPUT_NAME_PLAYER_ID))).findAny().ifPresent(playerDAO::delete);
        }
        resp.sendRedirect("index.jsp");
    }
}

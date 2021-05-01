package hu.alkfejl.controller;

import hu.alkfejl.constants.Constants;
import hu.alkfejl.model.dao.SimpleMoveDAO;
import hu.alkfejl.model.dao.SimplePartyDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/DeleteMatchController")
public final class DeleteMatchController extends HttpServlet {

    private static final SimpleMoveDAO moveDAO = SimpleMoveDAO.getInstance();
    private static final SimplePartyDAO partyDAO = SimplePartyDAO.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var partyID = Integer.parseInt(req.getParameter(Constants.FORM_INPUT_NAME_PARTY_ID));
        moveDAO.findAll().stream().filter(move -> move.getPartyID() == partyID).forEach(moveDAO::delete);
        partyDAO.findAll().stream().filter(p -> p.getId() == partyID).findFirst().ifPresent(partyDAO::delete);
        resp.sendRedirect("pages/match_history.jsp");
    }
}

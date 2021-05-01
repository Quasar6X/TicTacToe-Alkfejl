package hu.alkfejl.controller.page;

import hu.alkfejl.connection.ConnectionManagerServer;
import hu.alkfejl.model.dao.SimpleMoveDAO;
import hu.alkfejl.model.dao.SimplePartyDAO;
import hu.alkfejl.model.dao.SimplePlayerDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet("/IndexController")
public final class IndexController extends HttpServlet {

    @Override
    public void init() {
        var manager = new ConnectionManagerServer();
        if (!SimpleMoveDAO.getInstance().isValid()) SimpleMoveDAO.getInstance().setConnectionManager(manager);
        if (!SimplePlayerDAO.getInstance().isValid()) SimplePlayerDAO.getInstance().setConnectionManager(manager);
        if (!SimplePartyDAO.getInstance().isValid()) SimplePartyDAO.getInstance().setConnectionManager(manager);
    }
}

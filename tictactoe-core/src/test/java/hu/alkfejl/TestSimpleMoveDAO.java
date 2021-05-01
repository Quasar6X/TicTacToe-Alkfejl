package hu.alkfejl;

import hu.alkfejl.connection.ConnectionManagerClient;
import hu.alkfejl.model.Move;
import hu.alkfejl.model.Party;
import hu.alkfejl.model.PlayerHuman;
import hu.alkfejl.model.dao.SimpleMoveDAO;
import hu.alkfejl.model.dao.SimplePartyDAO;
import hu.alkfejl.model.dao.SimplePlayerDAO;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class TestSimpleMoveDAO {

    private static SimpleMoveDAO moveDAO;
    private static SimplePlayerDAO playerDAO;
    private static SimplePartyDAO partyDAO;

    private static PlayerHuman controlPlayer;
    private static Party controlParty;
    private static Move control;

    @BeforeAll
    public static void setup() {
        moveDAO = SimpleMoveDAO.getInstance();
        playerDAO = SimplePlayerDAO.getInstance();
        partyDAO = SimplePartyDAO.getInstance();
        var manager = new ConnectionManagerClient();

        moveDAO.setConnectionManager(manager);
        playerDAO.setConnectionManager(manager);
        partyDAO.setConnectionManager(manager);

        var dummyPlayer = new PlayerHuman();
        dummyPlayer.setName("Quasar6");
        controlPlayer = (PlayerHuman) playerDAO.save(dummyPlayer);

        var dummyParty = new Party();
        dummyParty.setBoardSize(16);
        controlParty = partyDAO.save(dummyParty);

        control = new Move();
    }

    @Test
    @Order(1)
    public void testInsertMove() {
        control.setTile("A3");
        control.setPartyID(controlParty.getId());
        control.setPlayerID(controlPlayer.getId());
        var move = moveDAO.save(control);
        assertNotNull(move);
        assertEquals(control.getTile(), move.getTile());
        control = move;
    }

    @Test
    @Order(2)
    public void testFindAllMoves() {
        var controList = new ArrayList<Move>();
        controList.add(control);

        var moves = moveDAO.findAll();

        int i = moves.size() - 1;

        assertEquals(controList.get(0).getId(), moves.get(i).getId());
        assertEquals(controList.get(0).getTile(), moves.get(i).getTile());
        assertEquals(controList.get(0).getPartyID(), moves.get(i).getPartyID());
        assertEquals(controList.get(0).getPlayerID(), moves.get(i).getPlayerID());
    }

    @Test
    @Order(3)
    public void testDeleteMove() {
        int i = moveDAO.findAll().size();
        moveDAO.findAll().stream().filter(move -> move.getId() == control.getId()).findFirst().ifPresent(moveDAO::delete);
        assertEquals(i - 1, moveDAO.findAll().size());
    }

    @AfterAll
    public static void cleanUp() {
        playerDAO.delete(controlPlayer);
        partyDAO.delete(controlParty);
    }
}

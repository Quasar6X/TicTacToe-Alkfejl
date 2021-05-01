package hu.alkfejl;

import hu.alkfejl.connection.ConnectionManagerClient;
import hu.alkfejl.model.Party;
import hu.alkfejl.model.PlayerAI;
import hu.alkfejl.model.dao.SimplePartyDAO;
import org.junit.jupiter.api.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class TestSimplePartyDAO {

    private static SimplePartyDAO partyDAO;
    private static Party control;

    @BeforeAll
    public static void setup() {
        partyDAO = SimplePartyDAO.getInstance();
        var manager = new ConnectionManagerClient();
        partyDAO.setConnectionManager(manager);
        control = new Party();
    }

    @Test
    @Order(1)
    public void testInsertParty() {
        control.setBoardSize(9);
        var ts = Timestamp.valueOf(LocalDateTime.now());
        control.setTimeOfParty(ts);
        control.setWinnerID(new PlayerAI().getId());
        var party = partyDAO.save(control);
        assertNotNull(party);
        assertEquals(control.getBoardSize(), party.getBoardSize());
        assertEquals(ts.toString(), party.getTimeOfParty().toString());
        assertEquals(control.getWinnerID(), party.getWinnerID());
        control = party;
    }

    @Test
    @Order(2)
    public void testFindAllParties() {
        var controlList = new ArrayList<Party>();
        controlList.add(control);

        var parties = partyDAO.findAll();

        int i = parties.size() - 1;

        assertEquals(controlList.get(0).getId(), parties.get(i).getId());
        assertEquals(controlList.get(0).getBoardSize(), parties.get(i).getBoardSize());
        assertEquals(controlList.get(0).getWinnerID(), parties.get(i).getWinnerID());

    }

    @Test
    @Order(3)
    public void testDeleteParty() {
        int i = partyDAO.findAll().size();
        partyDAO.findAll().stream().filter(party -> party.getId() == control.getId()).findFirst().ifPresent(partyDAO::delete);
        assertEquals(i - 1, partyDAO.findAll().size());
    }
}

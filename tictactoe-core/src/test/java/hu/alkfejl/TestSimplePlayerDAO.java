package hu.alkfejl;

import hu.alkfejl.connection.ConnectionManagerClient;
import hu.alkfejl.model.Player;
import hu.alkfejl.model.PlayerAI;
import hu.alkfejl.model.PlayerHuman;
import hu.alkfejl.model.dao.SimplePlayerDAO;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class TestSimplePlayerDAO {

    private static SimplePlayerDAO playerDAO;
    private static PlayerAI ai;
    private static PlayerHuman control;

    @BeforeAll
    public static void setup() {
        playerDAO = SimplePlayerDAO.getInstance();
        var manager = new ConnectionManagerClient();
        playerDAO.setConnectionManager(manager);

        ai = new PlayerAI();
        control = new PlayerHuman();
    }

    @Test
    @Order(1)
    public void testInsertPlayer() {
        var control = new PlayerHuman();
        control.setName("Quasar6");

        var playerDAO = SimplePlayerDAO.getInstance();
        var testPlayer = playerDAO.save(control);

        assertNotNull(testPlayer);
        assertEquals(control.getName(), testPlayer.getName());
        TestSimplePlayerDAO.control = (PlayerHuman) testPlayer;
    }

    @Test
    @Order(2)
    public void testFindAllPlayers() {
        var controlList = new ArrayList<Player>();
        controlList.add(ai);
        controlList.add(control);

        var players = playerDAO.findAll();

        int i = players.size() - 1;

        if (i > 1) {
            assertNotEquals(controlList.get(0).getId(), players.get(i - 1).getId());
            assertNotEquals(controlList.get(0).getName(), players.get(i - 1).getName());
        }
        assertEquals(controlList.get(1).getId(), players.get(i).getId());
        assertEquals(controlList.get(1).getName(), players.get(i).getName());
    }

    @Test
    @Order(3)
    public void testUpdatePlayer() {
        var player = playerDAO.findAll().stream().filter(player1 -> "Quasar6".equals(player1.getName())).findFirst().orElse(null);

        if (player != null) {
            ((PlayerHuman) player).setName("Quasar7");
            playerDAO.save(player);
            assertEquals("Quasar7", playerDAO.findAll().stream().filter(p1 -> "Quasar7".equals(p1.getName())).findFirst().orElse(new PlayerHuman()).getName());
        } else {
            fail();
        }
    }

    @Test
    @Order(4)
    public void testUpdateAIPlayer() {
        assertNull(playerDAO.save(ai));
    }

    @Test
    @Order(5)
    public void testDeleteAIPlayer() {
        int i = playerDAO.findAll().size();
        playerDAO.findAll().stream().filter(player -> player.getId() == ai.getId()).findFirst().ifPresent(playerDAO::delete);
        assertEquals(i, playerDAO.findAll().size());
    }

    @Test
    @Order(6)
    public void testDeletePlayer() {
        int i = playerDAO.findAll().size();
        playerDAO.findAll().stream().filter(player -> player.getId() == control.getId()).findFirst().ifPresent(playerDAO::delete);
        assertEquals(i - 1, playerDAO.findAll().size());
    }
}

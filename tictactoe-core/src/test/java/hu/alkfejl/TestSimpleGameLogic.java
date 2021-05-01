package hu.alkfejl;

import hu.alkfejl.model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class TestSimpleGameLogic {

    private static final PlayerHuman controlHuman = new PlayerHuman();
    private static final PlayerAI controlAI = new PlayerAI();
    private static final Party controlParty = new Party();
    private static SimpleGameLogic gameLogic = new SimpleGameLogic();

    @BeforeAll
    public static void setup() {
        controlHuman.setSymbol(Player.Symbol.X);
        controlParty.setBoardSize(8);
        gameLogic.setParty(controlParty);
    }

    @AfterEach
    public void cleanup() {
        gameLogic.printBoard();
        gameLogic = new SimpleGameLogic();
        gameLogic.setParty(controlParty);
    }

    @Test
    @Order(1)
    public void testTurn() {
        var move = gameLogic.move(controlHuman, 0, 0);
        assertEquals(GameLogic.Tile.X, gameLogic.getBoard()[0][0].getValue());
        assertNotNull(move);
        assertEquals("0_0", move.getTile());
        assertEquals(controlHuman.getId(), move.getPlayerID());
        assertEquals(controlParty.getId(), move.getPartyID());
        var move2 = gameLogic.move(controlAI, 0, 0);
        assertNull(move2);
        System.out.println("AFTER TEST 1:");
    }

    @Test
    @Order(2)
    public void testCheckStatePrimaryDiagonal() {
        gameLogic.move(controlHuman, 2, 2);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 1);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 3, 3);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 2);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 4, 4);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 3);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 5, 5);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 4);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 6, 6);
        assertEquals(GameLogic.State.XWin, gameLogic.checkState());
        System.out.println("AFTER TEST 2:");
    }

    @Test
    @Order(3)
    public void testCheckStateSecondaryDiagonal() {
        gameLogic.move(controlHuman, 0, 7);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 1);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 1, 6);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 2);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 2, 5);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 3);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 3, 4);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 4);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 4, 3);
        assertEquals(GameLogic.State.XWin, gameLogic.checkState());
        System.out.println("AFTER TEST 3:");
    }

    @Test
    @Order(4)
    public void testCheckStateVertical() {
        gameLogic.move(controlHuman, 0, 0);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 1);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 1, 0);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 2);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 2, 0);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 3);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 3, 0);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 4);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 4, 0);
        assertEquals(GameLogic.State.XWin, gameLogic.checkState());
        System.out.println("AFTER TEST 4:");
    }

    @Test
    @Order(5)
    public void testCheckStateHorizontal() {
        gameLogic.move(controlHuman, 1, 0);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 1);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 1, 1);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 2);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 1, 2);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 3);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 1, 3);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 4);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 1, 4);
        assertEquals(GameLogic.State.XWin, gameLogic.checkState());
        System.out.println("AFTER TEST 5:");
    }

    @Test
    @Order(6)
    public void testCheckStateDraw() {
        var controlParty2 = new Party();
        controlParty2.setBoardSize(3);
        gameLogic = new SimpleGameLogic();
        gameLogic.setParty(controlParty2);
        gameLogic.move(controlHuman, 0, 0);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 1, 1);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 0, 2);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 0, 1);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 2, 1);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 1, 0);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 1, 2);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlAI, 2, 2);
        assertEquals(GameLogic.State.NONE, gameLogic.checkState());
        gameLogic.move(controlHuman, 2, 0);
        assertEquals(GameLogic.State.DRAW, gameLogic.checkState());
        System.out.println("AFTER TEST 6:");
    }

    @Test
    @Order(7)
    public void testMoveException() {
        gameLogic.move(controlHuman, 0, 0);
        assertThrows(IllegalArgumentException.class, () -> gameLogic.move(controlHuman, 1, 0));
        System.out.println("AFTER TEST 7:");
    }
}

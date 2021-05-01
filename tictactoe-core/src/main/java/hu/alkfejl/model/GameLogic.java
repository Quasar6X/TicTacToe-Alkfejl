package hu.alkfejl.model;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

public interface GameLogic {

    /**
     * Possible states that the board can be at.
     */
    enum State {DRAW, XWin, OWin, NONE}

    /**
     * Simple bean to store the Tiles of the board and their state
     */
    final class Tile implements Serializable {

        public static final String NONE = "";
        public static final String X = "X";
        public static final String O = "O";

        private final StringProperty value = new SimpleStringProperty(this, "value", NONE);

        public String getValue() {
            return value.get();
        }

        public void setValue(String value) {
            this.value.set(value);
        }

        public StringProperty valueProperty() {
            return value;
        }
    }

    /**
     * This method places the {@link Player.Symbol} on the given field, meaning it sets
     * the Tile object's value equivalent to the aforementioned symbol. This method also
     * assumes responsibilty in flipping the turn.
     *
     * @param player the player who made the move
     * @param x      the x coordinate of a tile
     * @param y      the y coordinate of a tile
     * @return a {@link Move} bean
     */
    Move move(Player player, int x, int y);

    /**
     * Returns the current state of the board. Should be called after every {@link #move(Player, int, int)} call.
     *
     * @return the {@link State} of the board
     */
    State checkState();

    /**
     * This method flips the turn. May be called explicitly if a player's time is up.
     */
    void flipTurn();

    /**
     * Prints the current state of the gameboard to standard IO.
     */
    @SuppressWarnings("unused")
    void printBoard(); // Debugging purposes

    /*----------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------Public getters and setters-------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    Party getParty();

    ReadOnlyObjectProperty<Party> partyProperty();

    void setParty(final Party party);

    Player.Symbol getTurn();

    ReadOnlyObjectProperty<Player.Symbol> turnProperty();

    Tile[][] getBoard();

    ReadOnlyObjectProperty<Tile[][]> boardProperty();
}

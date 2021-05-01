package hu.alkfejl.model;

import javafx.beans.property.*;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.Random;

public final class PlayerAI implements Player, Serializable {

    /**
     * Random number generator for moves.
     */
    private static final Random random = new Random();

    /**
     * ID of the AI. Always equal to 1.
     */
    private final ReadOnlyIntegerWrapper id = new ReadOnlyIntegerWrapper(this, "id", 1);

    /**
     * Name of the AI. Always equal to 'Computer'.
     */
    private final ReadOnlyStringWrapper name = new ReadOnlyStringWrapper(this, "name", "Computer");

    /**
     * Symbol which the AI places. Always equal to 'O'.
     */
    private final ReadOnlyObjectWrapper<Symbol> symbol = new ReadOnlyObjectWrapper<>(this, "symbol", Symbol.O);

    /**
     * AI makes a random move.
     *
     * @param boardSize the upper bound of the board
     * @return a pair of x, y coordinates
     */
    public Pair<Integer, Integer> randomMove(int boardSize) {
        int x = random.nextInt(boardSize);
        int y = random.nextInt(boardSize);
        return new Pair<>(x, y);
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------------Public getters-------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public int getId() {
        return id.get();
    }

    public ReadOnlyIntegerProperty idProperty() {
        return id.getReadOnlyProperty();
    }

    @Override
    public String getName() {
        return name.get();
    }

    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    @Override
    public Symbol getSymbol() {
        return symbol.get();
    }

    public ReadOnlyObjectProperty<Symbol> symbolProperty() {
        return symbol.getReadOnlyProperty();
    }
}

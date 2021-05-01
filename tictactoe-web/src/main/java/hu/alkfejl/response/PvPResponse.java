package hu.alkfejl.response;

import hu.alkfejl.model.GameLogic;
import hu.alkfejl.model.SimpleMove;

import java.io.Serializable;

public final class PvPResponse implements Serializable {
    private SimpleMove playerMove;
    private int state = GameLogic.State.NONE.ordinal();
    private boolean valid;
    private int symbol;

    public int getSymbol() {
        return symbol;
    }

    public void setSymbol(int symbol) {
        this.symbol = symbol;
    }

    public SimpleMove getPlayerMove() {
        return playerMove;
    }

    public void setPlayerMove(SimpleMove playerMove) {
        this.playerMove = playerMove;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}

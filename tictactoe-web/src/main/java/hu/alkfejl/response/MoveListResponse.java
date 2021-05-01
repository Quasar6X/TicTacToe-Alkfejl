package hu.alkfejl.response;

import hu.alkfejl.model.SimpleMove;

import java.io.Serializable;
import java.util.ArrayList;

public final class MoveListResponse implements Serializable {

    private final ArrayList<SimpleMove> moves = new ArrayList<>();
    private final ArrayList<String> symbols = new ArrayList<>();
    private String turn;
    private int state;
    private boolean otherPlayerLeft;

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public boolean isOtherPlayerLeft() {
        return otherPlayerLeft;
    }

    public void setOtherPlayerLeft(boolean otherPlayerLeft) {
        this.otherPlayerLeft = otherPlayerLeft;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ArrayList<SimpleMove> getMoves() {
        return moves;
    }

    public ArrayList<String> getSymbols() {
        return symbols;
    }
}

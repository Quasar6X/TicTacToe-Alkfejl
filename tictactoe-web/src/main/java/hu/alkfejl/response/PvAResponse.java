package hu.alkfejl.response;

import hu.alkfejl.model.GameLogic;
import hu.alkfejl.model.SimpleMove;

import java.io.Serializable;

public final class PvAResponse implements Serializable {
    private SimpleMove playerMove;
    private int state = GameLogic.State.NONE.ordinal();
    private SimpleMove aiMove;

    public SimpleMove getPlayerMove() {
        return playerMove;
    }

    public int getState() {
        return state;
    }

    public SimpleMove getAiMove() {
        return aiMove;
    }

    public void setPlayerMove(SimpleMove playerMove) {
        this.playerMove = playerMove;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setAiMove(SimpleMove aiMove) {
        this.aiMove = aiMove;
    }
}

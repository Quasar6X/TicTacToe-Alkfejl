package hu.alkfejl.controller;

import hu.alkfejl.model.GameLogic;

public interface StateFinder {
    default GameLogic.State getStateFromOrdinal(final int ordinal) {
        switch (ordinal) {
            case 0: return GameLogic.State.DRAW;
            case 1: return GameLogic.State.XWin;
            case 2: return GameLogic.State.OWin;
            default: return GameLogic.State.NONE;
        }
    }
}

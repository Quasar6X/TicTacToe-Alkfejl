package hu.alkfejl.model;

import java.io.Serializable;


/**
 * Necessary class to send a move object to the client.
 * The default implementation of Move uses javafx properties,
 * which contain cicular references, so GSON can't serialize them
 */
public final class SimpleMove implements Serializable {
    public int id;
    public String tile;
    public int playerID;
    public int partyID;

    public static SimpleMove fromMove(final Move move) {
        if (move == null)
            return null;
        var ret = new SimpleMove();
        ret.id = move.getId();
        ret.tile = move.getTile();
        ret.playerID = move.getPlayerID();
        ret.partyID = move.getPartyID();
        return ret;
    }
}

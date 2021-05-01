package hu.alkfejl.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

public final class Move implements Serializable {

    /**
     * ID of the move.
     */
    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");

    /**
     * Tile where the player moved.
     */
    private final StringProperty tile = new SimpleStringProperty(this, "tile");

    /**
     * Player who made the move.
     */
    private final IntegerProperty playerID = new SimpleIntegerProperty(this, "playerID");


    /**
     * Party during which this move was played.
     */
    private final IntegerProperty partyID = new SimpleIntegerProperty(this, "partyID");

    /*----------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------Public getters and setters-------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getTile() {
        return tile.get();
    }

    public StringProperty tileProperty() {
        return tile;
    }

    public void setTile(String tile) {
        this.tile.set(tile);
    }

    public int getPlayerID() {
        return playerID.get();
    }

    public IntegerProperty playerIDProperty() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID.set(playerID);
    }

    public int getPartyID() {
        return partyID.get();
    }

    public IntegerProperty partyIDProperty() {
        return partyID;
    }

    public void setPartyID(int partyID) {
        this.partyID.set(partyID);
    }
}

package hu.alkfejl.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public final class Party implements Serializable {

    /**
     * ID of the party played.
     */
    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");

    /**
     * Board size on which the party was played.
     * Will always be in the range from 3 (inclusive) to 15 (inclusive).
     */
    private final IntegerProperty boardSize = new SimpleIntegerProperty(this, "boardSize");

    /**
     * When was this party played.
     */
    private final ObjectProperty<Timestamp> timeOfParty = new SimpleObjectProperty<>(this, "timeOfParty", Timestamp.valueOf(LocalDateTime.now()));

    /**
     * The winner of this party
     */
    private final IntegerProperty winnerID = new SimpleIntegerProperty(this, "winnerID");

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

    public int getBoardSize() {
        return boardSize.get();
    }

    public IntegerProperty boardSizeProperty() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize.set(boardSize);
    }

    public Timestamp getTimeOfParty() {
        return timeOfParty.get();
    }

    public ObjectProperty<Timestamp> timeOfPartyProperty() {
        return timeOfParty;
    }

    public void setTimeOfParty(Timestamp timeOfParty) {
        this.timeOfParty.set(timeOfParty);
    }

    public int getWinnerID() {
        return winnerID.get();
    }

    public IntegerProperty winnerIDProperty() {
        return winnerID;
    }

    public void setWinnerID(int winnerID) {
        this.winnerID.set(winnerID);
    }
}

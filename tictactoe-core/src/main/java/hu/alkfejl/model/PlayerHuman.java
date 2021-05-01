package hu.alkfejl.model;

import javafx.beans.property.*;

import java.io.Serializable;

public final class PlayerHuman implements Player, Serializable {

    /**
     * ID of the player in database. Always 1 for AI.
     */
    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");

    /**
     * Name of the player ind database or 'Computer' if AI.
     */
    private final StringProperty name = new SimpleStringProperty(this, "name");

    /**
     * Symbol of the player, which they place on the field. Can be 'X' or 'O'.
     */
    private final ObjectProperty<Symbol> symbol = new SimpleObjectProperty<>(this, "symbol");

    /*----------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------Public getters and setters-------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    @Override
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @Override
    public Symbol getSymbol() {
        return symbol.get();
    }

    public ObjectProperty<Symbol> symbolProperty() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol.set(symbol);
    }
}

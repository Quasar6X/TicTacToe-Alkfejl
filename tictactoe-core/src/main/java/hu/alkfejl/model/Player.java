package hu.alkfejl.model;


/**
 * Provides a simple interface for handling AI and user controlled Players.
 */
public interface Player {
    enum Symbol {X, O}

    int getId();

    String getName();

    Symbol getSymbol();
}

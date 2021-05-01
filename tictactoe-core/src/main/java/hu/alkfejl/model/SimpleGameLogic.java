package hu.alkfejl.model;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.io.Serializable;

public final class SimpleGameLogic implements GameLogic, Serializable {

    /**
     * Board of the game.
     */
    private final ReadOnlyObjectWrapper<Tile[][]> board = new ReadOnlyObjectWrapper<>(this, "board");

    /**
     * Party which game this belongs to.
     */
    private final ReadOnlyObjectWrapper<Party> party = new ReadOnlyObjectWrapper<>(this, "party");

    /**
     * Whose turn it is.
     */
    private final ReadOnlyObjectWrapper<Player.Symbol> turn = new ReadOnlyObjectWrapper<>(this, "lastSymbol", Player.Symbol.X);

    /**
     * This method places a charachter on the board if possible,
     * and then returns a {@link hu.alkfejl.model.Move} for the client.
     * The {@link hu.alkfejl.model.Player} bean should be initialised
     * with the appropriate 'charachter' property.
     *
     * @param player the player who made the move
     * @param x      row index of the move
     * @param y      col index of the move
     * @return a Move bean for later use
     * @throws IllegalArgumentException if the player was the one who moved last round
     */
    public Move move(final Player player, int x, int y) {
        if (!board.get()[x][y].getValue().equals(Tile.NONE))
            return null;

        if (player.getSymbol() != turn.get())
            throw new IllegalArgumentException("player can't move again.");

        if (player.getSymbol() == Player.Symbol.X)
            board.get()[x][y].setValue(Tile.X);
        else
            board.get()[x][y].setValue(Tile.O);
        flipTurn();

        var move = new Move();
        move.setTile(x + "_" + y);
        move.setPlayerID(player.getId());
        return move;
    }

    /**
     * Calls {@link #checkState(int)} with the appropriate symbolCount,
     * to determine the state of the board.
     *
     * @return the state which the board is at
     */
    public State checkState() {
        int size = party.get().getBoardSize();
        switch (size) {
            case 3:
                return checkState(3);
            case 4:
                return checkState(4);
            default:
                return checkState(5);
        }
    }

    /**
     * Algorithm to check wether or not the current board is in a winning state.
     *
     * @param symbolCount the number of continuous symbols needed for a win
     * @return the state which the board is at
     */
    @SuppressWarnings("DuplicatedCode") // PLS idea I'm not gonna extract the core of the algorithm...
    private State checkState(final int symbolCount) {
        for (int top = 0; top <= board.get().length - symbolCount; top++) {
            int bottom = top + symbolCount - 1;

            for (int left = 0; left <= board.get().length - symbolCount; left++) {
                int right = left + symbolCount - 1;

                nextRow:
                for (int row = top; row <= bottom; row++) {
                    if (board.get()[row][left].getValue().equals(Tile.NONE))
                        continue;
                    for (int col = left; col <= right; col++)
                        if (!board.get()[row][col].getValue().equals(board.get()[row][left].getValue()))
                            continue nextRow;

                    return winner(board.get()[row][left]);
                }

                nextCol:
                for (int col = left; col <= right; col++) {
                    if (board.get()[top][col].getValue().equals(Tile.NONE))
                        continue;
                    for (int row = top; row <= bottom; row++)
                        if (!board.get()[row][col].getValue().equals(board.get()[top][col].getValue()))
                            continue nextCol;

                    return winner(board.get()[top][col]);
                }

                diagPrimary:
                if (!board.get()[top][left].getValue().equals(Tile.NONE)) {
                    for (int i = 1; i < symbolCount; i++)
                        if (!board.get()[top + i][left + i].getValue().equals(board.get()[top][left].getValue()))
                            break diagPrimary;

                    return winner(board.get()[top][left]);
                }

                diagSecondary:
                if (!board.get()[top][right].getValue().equals(Tile.NONE)) {
                    for (int i = 1; i < symbolCount; i++)
                        if (!board.get()[top + i][right - i].getValue().equals(board.get()[top][right].getValue()))
                            break diagSecondary;

                    return winner(board.get()[top][right]);
                }
            }
        }

        boolean isFull = true;

        full:
        for (var chars : board.get())
            for (var c : chars)
                if (c.getValue().equals(Tile.NONE)) {
                    isFull = false;
                    break full;
                }

        return isFull ? State.DRAW : State.NONE;
    }

    /**
     * Never ever call this with anything other than 'X' or 'O',
     * you will possibly get a false positive.
     *
     * @param tile 'X' or 'O'
     * @return the state corresponding to the param
     */
    public State winner(final Tile tile) {
        return tile.getValue().equals(Tile.X) ? State.XWin : State.OWin;
    }

    /**
     * Sets the board values to none.
     */
    private void initializeBoard() {
        for (int i = 0; i < board.get().length; i++)
            for (int j = 0; j < board.get()[i].length; j++) {
                board.get()[i][j] = new Tile();
                board.get()[i][j].setValue(Tile.NONE);
            }
    }

    @Override
    public void printBoard() {
        for (int i = 0; i < board.get().length * 2 - 1; i++)
            System.out.print("-");
        System.out.println();
        for (var row : board.get()) {
            for (var tile : row) {
                switch (tile.getValue()) {
                    case Tile.O:
                        System.out.print("O ");
                        break;
                    case Tile.X:
                        System.out.print("X ");
                        break;
                    case Tile.NONE:
                        System.out.print("- ");
                }
            }
            System.out.println();
        }
        for (int i = 0; i < board.get().length * 2 - 1; i++)
            System.out.print("-");
        System.out.println();
    }

    @Override
    public void flipTurn() {
        this.turn.set(this.turn.get() == Player.Symbol.X ? Player.Symbol.O : Player.Symbol.X);
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /*-------------------------------------------Public getters and setters-------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public Player.Symbol getTurn() {
        return turn.get();
    }

    @Override
    public ReadOnlyObjectProperty<Player.Symbol> turnProperty() {
        return turn.getReadOnlyProperty();
    }

    @Override
    public Tile[][] getBoard() {
        return board.get();
    }

    @Override
    public ReadOnlyObjectProperty<Tile[][]> boardProperty() {
        return board.getReadOnlyProperty();
    }

    @Override
    public Party getParty() {
        return party.get();
    }

    @Override
    public ReadOnlyObjectProperty<Party> partyProperty() {
        return party.getReadOnlyProperty();
    }

    @Override
    public void setParty(final Party party) {
        this.party.set(party);
        board.set(new Tile[party.getBoardSize()][party.getBoardSize()]);
        initializeBoard();
    }
}

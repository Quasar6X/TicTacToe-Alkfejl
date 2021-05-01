package hu.alkfejl.model.dao;

import hu.alkfejl.connection.ConnectionManager;
import hu.alkfejl.model.Move;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class SimpleMoveDAO implements MoveDAO {

    private static final String SELECT_ALL_MOVES = "SELECT * FROM MOVE";
    private static final String INSERT_MOVE = "INSERT INTO MOVE (tile, player_id, party_id) VALUES (?, ?, ?)";
    private static final String DELETE_MOVE = "DELETE FROM MOVE WHERE id = ?";

    private static boolean hasInstance = false;
    private ConnectionManager manager;

    public static SimpleMoveDAO getInstance() {
        return InstanceHolder.instance;
    }

    public synchronized void setConnectionManager(final ConnectionManager manager) {
        this.manager = manager;
    }

    @Override
    public synchronized boolean isValid() {
        return manager != null;
    }

    private static final class InstanceHolder {
        public static final SimpleMoveDAO instance;

        static {
            try {
                instance = new SimpleMoveDAO();
                hasInstance = true;
            } catch (final InstantiationException e) {
                LOG.error("Failed to create instance");
                throw new NullPointerException("SimpleMoveDAO did not initialize correctly instance is null!");
            }
        }
    }

    private SimpleMoveDAO() throws InstantiationException {
        if (hasInstance)
            throw new InstantiationException("Singleton class do not instantiate!");
    }

    @Override
    public List<Move> findAll() {
        List<Move> moves = new ArrayList<>();
        var c = manager.getConnection();
        try (var stmt = c.createStatement();
             var rs = stmt.executeQuery(SELECT_ALL_MOVES)) {

            while (rs.next()) {
                var move = new Move();
                move.setId(rs.getInt("id"));
                move.setTile(rs.getString("tile"));
                move.setPlayerID(rs.getInt("player_id"));
                move.setPartyID(rs.getInt("party_id"));
                moves.add(move);
            }
        } catch (final Exception e) {
            LOG.warn("Could not list Moves.", e);
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
        } finally {
            close(c);
        }
        return moves;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Move save(final Move toSave) {
        if (toSave.getId() > 0) { // ONLY INSERT NO UPDATE
            LOG.warn("Tried to update Move. Only pass new Moves with id <= 0 to this method.");
            return null;
        }
        var c = manager.getConnection();
        if (c != null) {
            try (var stmt = c.prepareStatement(INSERT_MOVE, Statement.RETURN_GENERATED_KEYS)) {
                c.setAutoCommit(false);

                stmt.setString(1, toSave.getTile());
                stmt.setInt(2, toSave.getPlayerID());
                stmt.setInt(3, toSave.getPartyID());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    LOG.warn("No records have been inserted.");
                    return null;
                }
                c.commit();

                var genKeys = stmt.getGeneratedKeys();
                if (genKeys.next())
                    toSave.setId(genKeys.getInt(1));
            } catch (final Exception e) {
                LOG.warn("Could not insert Move into database. Rolling back...", e);
                rollback(c);
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
            } finally {
                close(c);
            }
        }
        return toSave;
    }

    @Override
    public void delete(final Move toDelete) {
        var c = manager.getConnection();
        if (c != null) {
            try (var stmt = c.prepareStatement(DELETE_MOVE)) {
                c.setAutoCommit(false);
                stmt.setInt(1, toDelete.getId());
                stmt.executeUpdate();
                c.commit();
            } catch (final Exception e) {
                LOG.warn("Could not delete Move from database.", e);
                rollback(c);
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
            } finally {
                close(c);
            }
        }
    }
}

package hu.alkfejl.model.dao;

import hu.alkfejl.connection.ConnectionManager;
import hu.alkfejl.model.Player;
import hu.alkfejl.model.PlayerAI;
import hu.alkfejl.model.PlayerHuman;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class SimplePlayerDAO implements PlayerDAO {

    private static final String SELECT_ALL_PLAYERS = "SELECT * FROM PLAYER";
    private static final String INSERT_PLAYER = "INSERT INTO PLAYER (name) VALUES (?)";
    private static final String UPDATE_PLAYER = "UPDATE PLAYER SET name = ? WHERE id = ?";
    private static final String DELETE_PLAYER = "DELETE FROM PLAYER WHERE id = ?";

    private static boolean hasInstance = false;
    private ConnectionManager manager;

    public static SimplePlayerDAO getInstance() {
        return InstanceHolder.instance;
    }
    public synchronized void setConnectionManager(final ConnectionManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean isValid() {
        return manager != null;
    }

    private static final class InstanceHolder {
        private static final SimplePlayerDAO instance;

        static {
            try {
                instance = new SimplePlayerDAO();
                hasInstance = true;
            } catch (final InstantiationException e) {
                LOG.error("Failed to create instance");
                throw new NullPointerException("SimplePlayerDAO did not initialize correctly instance is null!");
            }
        }
    }

    private SimplePlayerDAO() throws InstantiationException {
        if (hasInstance)
            throw new InstantiationException("Singleton class do not instantiate!");
    }

    @Override
    public List<Player> findAll() {
        List<Player> players = new ArrayList<>();
        var c = manager.getConnection();
        try (var stmt = c.createStatement();
             var rs = stmt.executeQuery(SELECT_ALL_PLAYERS)) {

            while (rs.next()) {
                int id = rs.getInt(1);
                if (id == 1) // If this is the AI
                    players.add(new PlayerAI());
                else {
                    var player = new PlayerHuman();
                    player.setId(id);
                    player.setName(rs.getString(2));
                    players.add(player);
                }
            }
        } catch (final Exception e) {
            LOG.warn("Could not list Players.", e);
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
        } finally {
            close(c);
        }
        return players;
    }

    @Override
    public Player save(final Player toSave) {
        if (toSave.getId() == 1) { // NEVER OVERWRITE AI
            LOG.warn("Player parameter should never be an AI, meaning it's id cannot be 1.");
            return null;
        }

        var ret = (PlayerHuman) toSave;
        var c = manager.getConnection();
        if (c != null) {
            try (var stmt = ret.getId() <= 0 ? c.prepareStatement(INSERT_PLAYER, Statement.RETURN_GENERATED_KEYS) : c.prepareStatement(UPDATE_PLAYER)) {
                c.setAutoCommit(false);
                if (ret.getId() > 0) { // UPDATE
                    stmt.setInt(2, ret.getId());
                }
                stmt.setString(1, ret.getName());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    LOG.warn("No records have been updated/inserted.");
                    return null;
                }
                c.commit();

                if (ret.getId() <= 0) { // INSERT
                    var genKeys = stmt.getGeneratedKeys();
                    if (genKeys.next())
                        ret.setId(genKeys.getInt(1));
                }
            } catch (final Exception e) {
                LOG.warn("Could not insert/update Player.", e);
                rollback(c);
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
            } finally {
                close(c);
            }
        }
        return ret;
    }

    @Override
    public void delete(final Player toDelete) {
        if (toDelete.getId() == 1) { // NEVER DELETE AI
            LOG.warn("Player parameter should never be an AI, meaning it's id cannot be 1.");
            return;
        }
        var c = manager.getConnection();
        if (c != null) {
            try (var stmt = c.prepareStatement(DELETE_PLAYER)) {
                c.setAutoCommit(false);
                stmt.setInt(1, toDelete.getId());
                stmt.executeUpdate();
                c.commit();
            } catch (final Exception e) {
                LOG.warn("Could not delete Player", e);
                rollback(c);
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
            } finally {
                close(c);
            }
        }
    }
}

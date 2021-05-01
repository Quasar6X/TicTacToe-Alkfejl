package hu.alkfejl.model.dao;

import hu.alkfejl.connection.ConnectionManager;
import hu.alkfejl.model.Party;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public final class SimplePartyDAO implements PartyDAO {

    private static final String SELECT_ALL_PARTIES = "SELECT * FROM PARTY";
    private static final String INSERT_PARTY = "INSERT INTO PARTY (board_size, time_of_party) VALUES (?,?)";
    private static final String INSERT_PARTY_WITH_WINNER = "INSERT INTO PARTY (board_size, time_of_party, winner_id) VALUES (?, ?, ?)";
    private static final String DELETE_PARTY = "DELETE FROM PARTY WHERE id = ?";

    private static boolean hasInstance = false;
    private ConnectionManager manager;

    public static SimplePartyDAO getInstance() {
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
        private static final SimplePartyDAO instance;

        static {
            try {
                instance = new SimplePartyDAO();
                hasInstance = true;
            } catch (final InstantiationException e) {
                LOG.error("Failed to create instance");
                throw new NullPointerException("SimplePartyDao did not initialize correctly instance is null!");
            }
        }
    }

    private SimplePartyDAO() throws InstantiationException {
        if (hasInstance)
            throw new InstantiationException("Singleton class do not instantiate!");
    }

    @Override
    public List<Party> findAll() {
        List<Party> parties = new ArrayList<>();
        var c = manager.getConnection();
        try (var stmt = c.createStatement();
             var rs = stmt.executeQuery(SELECT_ALL_PARTIES)) {

            while (rs.next()) {
                var party = new Party();
                party.setId(rs.getInt(1));
                party.setBoardSize(rs.getInt(2));
                party.setTimeOfParty(Timestamp.valueOf(rs.getString(3)));
                int winnerID = rs.getInt(4);
                if (winnerID != 0)
                    party.setWinnerID(winnerID);
                parties.add(party);
            }

        } catch (final Exception e) {
            LOG.warn("Could not list Parties.", e);
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
        } finally {
            close(c);
        }
        return parties;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public Party save(final Party toSave) {
        if (toSave.getId() > 0) { // ONLY INSERT NO UPDATE
            LOG.warn("Tried to update Party. Only pass new Parties with id <= 0 to this method.");
            return null;
        }
        var c = manager.getConnection();
        if (c != null) {
            try (var stmt = toSave.getWinnerID() == 0 ?
                    c.prepareStatement(INSERT_PARTY, Statement.RETURN_GENERATED_KEYS) : c.prepareStatement(INSERT_PARTY_WITH_WINNER, Statement.RETURN_GENERATED_KEYS)) {
                c.setAutoCommit(false);

                stmt.setInt(1, toSave.getBoardSize());
                stmt.setString(2, toSave.getTimeOfParty().toString());

                if (toSave.getWinnerID() != 0)
                    stmt.setInt(3, toSave.getWinnerID());

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
                LOG.warn("Could not insert Party.", e);
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
    public void delete(final Party toDelete) {
        var c = manager.getConnection();
        if (c != null) {
            try (var stmt = c.prepareStatement(DELETE_PARTY)) {
                c.setAutoCommit(false);
                stmt.setInt(1, toDelete.getId());
                stmt.executeUpdate();
                c.commit();
            } catch (final Exception e) {
                LOG.warn("Could not delete Party.", e);
                rollback(c);
                if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
            } finally {
                close(c);
            }
        }
    }
}

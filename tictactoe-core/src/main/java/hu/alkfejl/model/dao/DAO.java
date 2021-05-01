package hu.alkfejl.model.dao;

import hu.alkfejl.connection.ConnectionManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {

    /**
     * Querries the databse and makes a list from the objects it querried.
     *
     * @return a list of objects in the databse
     */
    List<T> findAll();

    /**
     * Saves the given object to the databse. This means if the parameter object has an ID and
     * exists in the database, then it's fields will be updated. If it has no ID, then it will
     * be inserted into the datbase.
     *
     * @param toSave the object to save
     * @return the saved object instance
     */
    T save(T toSave);

    /**
     * Delete the given object from the database.
     * The object to be deleted will be determined by it's ID property.
     *
     * @param toDelete the object to delete
     */
    void delete(T toDelete);

    /**
     * Set the connection manager to get connections from
     *
     * @param manager the manager {@link hu.alkfejl.connection.ConnectionManagerClient} for client side and {@link hu.alkfejl.connection.ConnectionManagerServer} for server side
     */
    void setConnectionManager(final ConnectionManager manager);

    /**
     * @return true if this DAO has a connection manager
     */
    boolean isValid();

    /**
     * Rolls back the given connection. Called in the catch block of SQL transactions.
     *
     * @param connection the connection to roll back
     */
    default void rollback(final Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (final SQLException e) {
                Logger log = Logger.getLogger(DAO.class);
                log.error("Could not rollback! Database may be corrupt!");
            }
        }
    }

    /**
     * Closes the given connection. Called when the Connection object cannot be enclosed
     * in a try-with-resources block.
     *
     * @param connection the connection to close
     */
    default void close(final Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (final SQLException e) {
                Logger log = Logger.getLogger(DAO.class);
                log.error("Could not close connection!");
            }
        }
    }
}

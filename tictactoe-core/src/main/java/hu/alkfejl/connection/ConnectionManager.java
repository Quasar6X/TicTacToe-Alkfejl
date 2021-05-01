package hu.alkfejl.connection;

import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

public abstract class ConnectionManager {

    public static final Logger LOG = Logger.getLogger(ConnectionManager.class);

    protected final ConnectionManagerProperties properties;
    protected final DataSource DATA_SOURCE;

    public ConnectionManager(final DataSource dataSource) {
        properties = loadProperties();
        DATA_SOURCE = dataSource;
    }

    public Connection getConnection() {
        Connection c = null;
        try {
            c = DATA_SOURCE.getConnection();
        } catch (final Exception e) {
            LOG.error("Could not get connection from pool", e);
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
        }
        return c;
    }

    public abstract void closeConnections();

    private ConnectionManagerProperties loadProperties() {
        LOG.info("Loading application.properties file");
        String CONNECTION_URL;
        int retryCloseConnection;
        try {
            var PROPS = new Properties();
            PROPS.load(ConnectionManagerClient.class.getResourceAsStream("/application.properties"));
            CONNECTION_URL = PROPS.getProperty("db.url");
            retryCloseConnection = Integer.parseInt(PROPS.getProperty("db.max.close"));
        } catch (final Exception e) {
            LOG.error("Cannot load application.properties", e);
            throw new RuntimeException("Cannot load application.properties");
        }
        return new ConnectionManagerProperties(CONNECTION_URL, retryCloseConnection);
    }

    protected static final class ConnectionManagerProperties {
        public final String CONNECTION_URL;
        public final int RETRY_CLOSING;

        public ConnectionManagerProperties(final String CONNECTION_URL, final int retryClosing) {
            this.CONNECTION_URL = CONNECTION_URL;
            this.RETRY_CLOSING = retryClosing;
        }
    }
}

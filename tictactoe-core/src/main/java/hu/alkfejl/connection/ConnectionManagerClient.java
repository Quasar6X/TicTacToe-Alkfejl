package hu.alkfejl.connection;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;

public final class ConnectionManagerClient extends ConnectionManager {

    private int retryClosing;

    public ConnectionManagerClient() {
        super(new BasicDataSource());
        retryClosing = properties.RETRY_CLOSING;
        var source = (BasicDataSource) DATA_SOURCE;
        source.setUrl(properties.CONNECTION_URL);
        source.setTestWhileIdle(false);
        source.setTestOnBorrow(true);
        source.setValidationQuery("SELECT 1");
        source.setTestOnReturn(false);
        source.setTimeBetweenEvictionRunsMillis(30000);
        source.setMaxTotal(100);
        source.setInitialSize(10);
        source.setMaxWaitMillis(10000);
        source.setRemoveAbandonedTimeout(60);
        source.setMinEvictableIdleTimeMillis(30000);
        source.setMinIdle(10);
        source.setRemoveAbandonedOnMaintenance(true);
    }

    @Override
    public void closeConnections() {
        --retryClosing;
        try {
            LOG.info("Trying to close connection pool...");
            ((BasicDataSource) DATA_SOURCE).close();
        } catch (final SQLException e) {
            LOG.warn("Cannot close DATA_SOURCE connection pool! Retrying: " + retryClosing + " more times.", e);
            if (retryClosing == 0) {
                LOG.error("Could not close DATA_SOURCE connection pool.", e);
            } else
                closeConnections();
        } finally {
            LOG.info("Successfully closed the connection pool.");
        }
    }
}

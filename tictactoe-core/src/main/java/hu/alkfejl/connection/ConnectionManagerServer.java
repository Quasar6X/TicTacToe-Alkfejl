package hu.alkfejl.connection;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public final class ConnectionManagerServer extends ConnectionManager {

    public ConnectionManagerServer() {
        super(new DataSource());
        var pp = new PoolProperties();
        pp.setUrl(properties.CONNECTION_URL);
        pp.setDriverClassName("org.sqlite.JDBC");
        pp.setTestWhileIdle(false);
        pp.setTestOnBorrow(true);
        pp.setValidationQuery("SELECT 1");
        pp.setTestOnReturn(false);
        pp.setValidationInterval(30000);
        pp.setTimeBetweenEvictionRunsMillis(30000);
        pp.setMaxActive(100);
        pp.setInitialSize(10);
        pp.setMaxWait(10000);
        pp.setRemoveAbandonedTimeout(60);
        pp.setMinEvictableIdleTimeMillis(30000);
        pp.setMinIdle(10);
        pp.setRemoveAbandoned(true);
        ((DataSource) DATA_SOURCE).setPoolProperties(pp);
    }

    @Override
    public void closeConnections() {
        LOG.info("Trying to close connection pool...");
        ((DataSource) DATA_SOURCE).close();
        ((DataSource) DATA_SOURCE).postDeregister();
    }
}

package by.bsuir.fitness.pool;


import by.bsuir.fitness.util.PropertyLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The type Connection pool.
 */
public class ConnectionPool {
    private static final Logger LOGGER = LogManager.getLogger(ConnectionPool.class);

    private static final int DEFAULT_NUMBER_OF_CONNECTION = 5;
    private static final String PROPERTY_PATH = "db/mysql.properties";
    private static final String URL_PROPERTY_KEY = "url";
    private static final String NUMBER_OF_CONNECTIONS_KEY = "number.of.connections";
    private static final AtomicBoolean IS_CREATED = new AtomicBoolean(false);
    private static final ReentrantLock LOCK = new ReentrantLock();

    private static ConnectionPool instance;

    private final BlockingQueue<ProxyConnection> awaitingConnections;
    private final List<ProxyConnection> occupiedConnections = new ArrayList<>();
    private final Driver driver;

    private int numberOfConnections;

    private ConnectionPool() {
        try {
            var property = PropertyLoader.loadProperty(PROPERTY_PATH);
            try {
                numberOfConnections = Integer.parseInt(property.getProperty(NUMBER_OF_CONNECTIONS_KEY));
            } catch (final NumberFormatException exception) {
                numberOfConnections = DEFAULT_NUMBER_OF_CONNECTION;
                LOGGER.warn("Incorrect number of connections, set default = {}", numberOfConnections);
            }

            this.awaitingConnections = new ArrayBlockingQueue<>(numberOfConnections);

            driver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(driver);
            for (int i = 0; i < numberOfConnections; i++) {
                var connection = new ProxyConnection(DriverManager.getConnection(property.getProperty(URL_PROPERTY_KEY), property));
                awaitingConnections.offer(connection);
            }
        } catch (final SQLException exception) {
            LOGGER.error("Missing or incorrect db configuration file.", exception);
            throw new RuntimeException(exception);
        }

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ConnectionPool getInstance() {
        if (!IS_CREATED.get()) {
            initPool();
            IS_CREATED.set(true);
            LOGGER.debug("Connection pool created, number of connections - {}", instance.numberOfConnections);
        }
        return instance;
    }

    /**
     * Init pool.
     */
    public static void initPool() {
        if (!IS_CREATED.get()) {
            LOCK.lock();
            try {
                if (instance == null) {
                    instance = new ConnectionPool();
                    IS_CREATED.set(true);
                }
            } finally {
                LOCK.unlock();
            }
        }
    }

    /**
     * Take connection connection.
     *
     * @return the connection
     */
    public Connection takeConnection() {
        ProxyConnection connection = null;
        try {
            connection = awaitingConnections.take();
            occupiedConnections.add(connection);
        } catch (final InterruptedException exception) {
            LOGGER.error("In ConnectionPoll takeConnection interrupted.");
        }
        return connection;
    }

    /**
     * Size int.
     *
     * @return the int
     */
    public int size() {
        return instance.numberOfConnections;
    }

    /**
     * Release connection.
     *
     * @param connection the connection
     */
    public void releaseConnection(final ProxyConnection connection) {
        awaitingConnections.offer(connection);
    }

    /**
     * Close all connections.
     */
    public void closeAllConnections() {
        for (int i = 0; i < numberOfConnections; i++) {
            try {
                var connection = awaitingConnections.take();
                connection.reallyClose();
            } catch (final InterruptedException exception) {
                LOGGER.error(exception.getMessage());
            }
        }

        try {
            DriverManager.deregisterDriver(driver);
        } catch (final SQLException exception) {
            LOGGER.error(exception.getMessage());
        }
    }
}


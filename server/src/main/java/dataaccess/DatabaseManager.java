package dataaccess;

import java.sql.*;
import java.util.Properties;

import static utils.Catcher.*;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;
    private static boolean databaseExists;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        if (databaseExists) return;
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        tryCatchResources(() -> DriverManager.getConnection(connectionUrl, dbUsername, dbPassword),
                conn -> conn.prepareStatement(statement),
                PreparedStatement::executeUpdate,
                SQLException.class, DataAccessException.class, e -> "failed to create database");
        databaseExists = true;
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        return tryCatchRethrow(() -> {
            //do not wrap the following line with a try-with-resources
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        }, SQLException.class, DataAccessException.class, e -> "failed to get connection");
    }

    private static void loadPropertiesFromResources() {
        tryCatchResources(() -> Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"), propStream -> {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
            return null;
        }, Exception.class, RuntimeException.class, e -> "unable to process db.properties");
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}

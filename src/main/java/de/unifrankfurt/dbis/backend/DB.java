package de.unifrankfurt.dbis.backend;

import dbfit.MySqlTest;
import de.unifrankfurt.dbis.config.XConfig;
import de.unifrankfurt.dbis.Submission.Count;
import fit.Parse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class is a wrapper for the database connection used within DBFit which suppressed the error messages
 */
public class DB extends MySqlTest implements AutoCloseable {

    private static Logger logger = LogManager.getLogger(DB.class);

    /**
     * The connection parameters for the database from the configuration file
     */
    private XConfig XConfig;

    /**
     * a stream to collect the error messages of DBFit so that they can be analysed
     */
    private ByteArrayOutputStream byteOutputStream;

    /**
     * A reference to the PrintStream of the ByteArrayOutputStream to substitute the standard System.err PrintStream
     */
    private PrintStream err;

    /**
     * The reference of the System.err PrintStream to restore this after DBFit was executed
     */
    private PrintStream systemErr;


    /*old
    /**
     * This method creates a DB object by using the properties of a MySQL Configuration
     *
     * @param XConfig a MySQL connection configuration
     *
    public DB(MySQLConfig XConfig) {
        super();

        byteOutputStream = new ByteArrayOutputStream();
        this.err = new PrintStream(byteOutputStream);
        this.systemErr = System.err;
        this.XConfig = XConfig;
    }
    */

    public DB(XConfig XConfig) {
        super();
        byteOutputStream = new ByteArrayOutputStream();
        this.err = new PrintStream(byteOutputStream);
        this.systemErr = System.err;
        this.XConfig = XConfig;
    }

    /**
     * Establish a connection to the database so that this connection can be used by DBFit
     *
     * @throws SQLException a sql error
     */
    public void connect() throws SQLException {
        String host = XConfig.getHostname();
        String db = XConfig.getDatabase();
        String port = XConfig.getPort();
        String user = XConfig.getUsername();
        String password = XConfig.getPassword();
        logger.info(String.format("Try to connect to database '%s' on server %s:%s with user %s with password '%s'", db,
                host, port, user, password));
        super.connect(host, user, password, db);
    }

    /**
     * If successful this returns a connection to the database
     *
     * @return a connection to the database
     * @throws SQLException a SQLException can occur
     */
    public Connection getConnection() throws SQLException {
        return this.environment.getConnection();
    }


    /**
     * This method tests whether a connection to the database can be establish with the given configuration. If not an
     * SQL Exception will be thrown.
     *
     * @param XConfig a MySQL Connection Object
     * @return true if no error occur
     * @throws SQLException if a error occur
     */
    public static boolean testConnection(XConfig XConfig) throws SQLException {
        DB db = new DB(XConfig);
        db.connect();
        db.close();
        return true;
    }

    /**
     * This method tests whether a connection to the database can be establish with the given configuration.
     *
     * @param XConfig a MySQL Connection Object
     * @return true if the connection was establish or false if not.
     */
    public static boolean connectible(XConfig XConfig) {
        try {
            return DB.testConnection(XConfig);
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * this logs the error message which was thrown by the DBFit tests
     */
    public void logErrMessage() {
        String msg = byteOutputStream.toString();
        String[] msgLine = msg.split("\n");
        for (String line : msgLine) {
            if (!line.startsWith("\t")) {
                int idx = line.indexOf(": ");
                if (idx >= 0) {
                    logger.error("DBFIT Error: " + line.substring(idx + 2));
                }
            }
        }
        byteOutputStream.reset();
    }


    /**
     * returns the count which was evaluated by DBFit
     *
     * @return a Count object
     */
    public Count getCount() {
        return new Count(
                this.counts.right,
                this.counts.wrong,
                this.counts.ignores,
                this.counts.exceptions
        );
    }

    /**
     * this evaluates the DBFit solution of a given parser
     *
     * @param fitParser a Parser object
     */
    public void doTables(Parse fitParser) {
        System.setErr(err);

        super.doTables(fitParser);

        System.err.flush();
        System.setErr(systemErr);
    }
}
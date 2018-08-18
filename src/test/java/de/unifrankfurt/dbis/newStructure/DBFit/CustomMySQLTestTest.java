package de.unifrankfurt.dbis.newStructure.DBFit;

import de.unifrankfurt.dbis.DBFit.CustomMySQLTest;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class CustomMySQLTestTest {

    @Test
    public void connect() throws SQLException {
        CustomMySQLTest test = new CustomMySQLTest();
        test.connect("localhost","test","test","simple");
        assertTrue(test.isConnected());
        test.close();
        assertFalse(test.isConnected());
    }

}
package de.unifrankfurt.dbis.newStructure.DBFit;

import de.unifrankfurt.dbis.TestResources;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class MySQLTestWrapperTest {

    @Test
    public void connect() throws IOException, SQLException {
        MySQLTestWrapper test = new MySQLTestWrapper();
        test.connect(TestResources.Simple.getInstance().getConfig());
        assertTrue(test.isConnected());
        test.close();
        assertFalse(test.isConnected());
        assertTrue(test.reconnect());
        test.close();
    }

    @Test
    public void doTables() {
    }

    @Test
    public void runSubmission() {
    }
}

package de.unifrankfurt.dbis.DBFit;

import dbfit.MySqlTest;
import dbfit.api.DBEnvironment;
import de.unifrankfurt.dbis.Submission.Count;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomMySQLTest extends MySqlTest {

    public boolean isConnected() {
        DBEnvironment a = super.environment;

        Connection c;
        try {
            c = a.getConnection();
        } catch (SQLException | IllegalArgumentException e) {
            return false;
        }
        if (c == null) return false;
        try {
            if (c.isClosed()) return false;
        } catch (SQLException e) {
            return false;
        }
        return true;
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
}
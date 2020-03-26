package de.unifrankfurt.dbis.Inner;

import de.unifrankfurt.dbis.config.DataSource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

class BaseTest {

    @Test
    void test1() throws IOException {
        DataSource source = new DataSource("localhost", "3306", "airportuser", "airportuser", "airport", false, "+01:00");
        System.err.println("a");
        Base base = Base.fromPath(Paths.get("/home/xyntek/Dropbox/SQLChecker/NEU_TESTS/solution.sql"));
        System.err.println("b");
        SQLScript script = SQLScript.fromPath(Paths.get("/home/xyntek/Dropbox/SQLChecker/NEU_TESTS/test_uebung_reset.sql"));
        System.err.println("c");
        Solution sol = base.generateSolution(source, script);
        System.err.println(sol);
    }
}
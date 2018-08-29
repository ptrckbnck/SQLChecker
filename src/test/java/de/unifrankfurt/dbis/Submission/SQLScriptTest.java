package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.TestResources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;


public class SQLScriptTest {

    @Test
    public void getQueryList() throws IOException {
        TestResources.Simple simple = TestResources.Simple.getInstance();
        Path path = simple.getResetPath();
        System.out.println(path);
        SQLScript s = SQLScript.fromPath(path);
        System.err.println(s.getQueryList());
        assertTrue(true);
    }

    @Test
    public void fromPath() {
    }

    @Test
    public void storeInPath() {
    }

    @Test
    public void execute() {
    }
}
package de.unifrankfurt.dbis.integration;

import de.unifrankfurt.dbis.Runner;
import de.unifrankfurt.dbis.Submission.Submission;
import de.unifrankfurt.dbis.Submission.SubmissionParseException;
import de.unifrankfurt.dbis.Submission.Task;
import de.unifrankfurt.dbis.config.EvalConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Evaluation {

    @Disabled
    @Test
    public void test() {
        Runner.main("-e", "-c", "/home/xyntek/simple/config.txt");
        assertTrue(true);
    }

    @Disabled
    @Test
    public void test2() {
        Runner.main("-e","-csv","/home/xyntek/TestAufgaben/Blatt1/a.csv", "-c", "/home/xyntek/TestAufgaben/Blatt1/config.txt");
        assertTrue(true);
    }

    @Disabled
    @Test
    public void test3() throws IOException, SubmissionParseException {
        Submission<Task> e = Submission.fromPath(Paths.get("/home/xyntek/TestAufgaben/Blatt1/ub1/raw.sql"));
        //System.err.println(e);
        assertTrue(true);
    }

    @Test
    public void test4() throws IOException {
        EvalConfig config = EvalConfig.fromPath(Paths.get("/home/xyntek/simple/config.txt"));
        config.storeInPath(Paths.get("/home/xyntek/simple/config.ini"));
        config = EvalConfig.fromPath(Paths.get("/home/xyntek/simple/config.ini"));
        System.out.println(config.toString());

    }
}

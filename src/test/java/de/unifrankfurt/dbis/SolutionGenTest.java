package de.unifrankfurt.dbis;

import de.unifrankfurt.dbis.config.XConfig;
import de.unifrankfurt.dbis.config.XConfigBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

public class SolutionGenTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream sout;
    /**
     * pipes stdout
     */
    @Before
    public void setUpStreams() {
        sout = System.out;
        System.setOut(new PrintStream(outContent));
    }

    /**
     * removes pipe
     */
    @After
    public void cleanUpStreams() {
        System.setOut(sout);
    }


    private String getRes(String path) {
        return this.getClass().getResource(path).getPath();
    }

    @Test
    public void mainBuildConfig() throws IOException {
        assumeTrue(false);
        XConfig simple = TestResources.Simple.getInstance().getConfig();
        XConfigBuilder XConfigBuilder = new XConfigBuilder(simple);
        Path out = Files.createTempDirectory("dbFitHtml.txt");
        Path sample = Files.createTempFile(out, "sample.txt", "");
        String in = getRes("/test/simple/raw_simple.txt");
        Path config = Files.createTempFile(out, "config.ini", "");
        XConfigBuilder.setSolutionOutPath(out.toString())
                .setSolutionSamplePath(sample.toString())
                .setSolutionInPath(in);
        XConfigBuilder.createConfig().storeInPath(config);
        String[] args = {"-c", config.toString(), "-s"};
        int result = Runner.main(args);
        assertEquals(0, result);
        System.setIn(null);
    }
}

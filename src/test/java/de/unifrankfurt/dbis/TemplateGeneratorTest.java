package de.unifrankfurt.dbis;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

class TemplateGeneratorTest {

    @Test
    void run() throws IOException {

        //  public TemplateGenerator(Path path, Path out, Boolean useStdout){
        TemplateGenerator tg = new TemplateGenerator(Paths.get("/home/xyntek/IdeaProjects/SQLChecker/examples/solution.sql"),
                Paths.get("/home/xyntek/IdeaProjects/SQLChecker/examples/t"), false);
        tg.run();
    }
}
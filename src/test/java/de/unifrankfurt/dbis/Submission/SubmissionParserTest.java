package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.TestResources;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class SubmissionParserTest {

    @Test
    public void tokenizer() {
    }

    @Test
    public void parseAuthor() {
    }

    @Test
    public void parse() throws IOException, SubmissionParseException {
        TestResources.DBFitSubmissionData s = TestResources.getSubmissionWAuthor();
        List<String> a = Files.readAllLines(s.getPath());
        Submission<Task> b = SubmissionParser.parse(a);
        System.out.println(b);
    }

    @Test
    public void fromToken() {
    }

    @Test
    public void splitBody() {
    }
}
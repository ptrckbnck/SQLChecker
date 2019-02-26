package de.unifrankfurt.dbis.Inner;

import de.unifrankfurt.dbis.config.DataSource;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {
    private DataSource datasource = new DataSource("localhost", "3306", "airportuser", "airportuser", "airport", false, "+01:00");

    private SQLScript resetScript = new SQLScript(List.of("DROP DATABASE IF EXISTS airport;", "CREATE DATABASE airport;"));





    @Test
    void isSublistWithGaps() {
        List<String> l = List.of("A", "B", "C", "D");
        assertTrue(Solution.isSublistWithGaps(l, List.of()));
        assertTrue(Solution.isSublistWithGaps(l, List.of("A", "B", "C")));
        assertTrue(Solution.isSublistWithGaps(l, List.of("A", "B", "D")));
        assertTrue(Solution.isSublistWithGaps(l, l));
        assertFalse(Solution.isSublistWithGaps(l, List.of("A", "B", "C", "D", "E")));
        assertFalse(Solution.isSublistWithGaps(l, List.of("A", "C", "B")));
    }



    @Test
    void fixedSubmission() throws SubmissionParseException {
        String solString = "/*submission_name*/\n" +
                "name\n" +
                "\n" +
                "/*1a*/\n" +
                "/* Kommentar zu Aufgabe 1a\n" +
                "ueber mehrere Zeilen */\n" +
                "sql1;\n" +
                "/*static*/\n" +
                "static;\n" +
                "\n" +
                "/*1b*/\n" +
                "/* Kommentar zu Aufgabe 1b */\n" +
                "sql2\n";
        String subString = "/*authors*/\n" +
                "a,b,c\n" +
                "\n" +
                "/*1b*/\n" +
                "/* Kommentar zu Aufgabe 1b */\n" +
                "sql2\n";
        Solution sol = null; //TODO
        Submission sub = SubmissionParser.parse(subString, StandardCharsets.UTF_8);
        Submission newSub = sol.tryToFixTagsFor(sub);
        assertEquals(sub.getAuthors(), newSub.getAuthors());
        assertEquals(sub.getPath(), newSub.getPath());
        assertEquals(sub.getName(), newSub.getName());
        assertEquals(sol.getNonStaticTags(), newSub.getTags());
    }

    @Test
    void evaluate() {
        List<Task> tasks = List.of(
                new TaskSQL(new Tag("1"), "SELECT '1';"),
                new TaskSQL(new Tag("2"), "CREATE TABLE TEST_TABLE( ID int);"),
                new TaskSQL(new Tag("3"), "INSERT INTO TEST_TABLE (ID)\nVALUES (1);"),
                new TaskSQL(new Tag("4"), "SELECT * FROM TEST_TABLE;")
        );
        Submission submission = new Submission(tasks, "test", StandardCharsets.UTF_8);
        Solution solution = submission.generateSolution(datasource, resetScript);
        ResultStorage resultStorage = new ResultStorage();
        solution.evaluate(resultStorage, datasource, resetScript, submission, true);

        System.err.println(resultStorage);
    }
}
package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.DBFit.ResultStorage;
import de.unifrankfurt.dbis.config.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {
    private static Submission testsubmission = null;
    private static String DBFitHtml = null;
    private static DataSource source = null;
    private static SQLScript resetScript;
    private static String surveyHtml = null;

    /**
     * setUp Data used in Tests.
     */
    @BeforeAll
    static void setUp() {
        List<Student> students = List.of(new Student("foo", "bar", "foo@bar.de"));

        Task task1 = new TaskNonCallable(new Tag("Aufgabe1"), "/* comment1 */\nSELECT '1';");
        Task task2 = new TaskNonCallable(new Tag("Aufgabe2"), "/* comment2 */\nSELECT '2';");

        List<Task> tasks = List.of(task1, task2);
        testsubmission = new Submission(students, tasks, "test_submission");
        testsubmission.setPath(Paths.get("/home/test/submission.txt"));

        DBFitHtml = "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>dbfit.MySqlTest</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Connect</td>\n" +
                "\t\t<td>localhost</td>\n" +
                "\t\t<td>test</td>\n" +
                "\t\t<td>test</td>\n" +
                "\t\t<td>test</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>/*Aufgabe1*/</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>1</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>1</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>/*Aufgabe2*/</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>2</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>2</td>\n" +
                "\t</tr>\n" +
                "</table>";

        source = new DataSource("localhost", "3306", "test", "test", "test", false, "+01:00");

        resetScript = new SQLScript(List.of("CREATE DATABASE IF NOT EXISTS test;"));

        surveyHtml = "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>dbfit.MySqlTest</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Connect</td>\n" +
                "\t\t<td>localhost</td>\n" +
                "\t\t<td>test</td>\n" +
                "\t\t<td>test</td>\n" +
                "\t\t<td>test</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>/* comment1 */\n" +
                "SELECT '1';</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>1</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>1</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>/* comment2 */\n" +
                "SELECT '2';</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>2</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>2</td>\n" +
                "\t</tr>\n" +
                "</table>";
    }


    /**
     * tests getter method
     */
    @Test
    void getSubmission() {
        Solution solution = new Solution(testsubmission, DBFitHtml);
        assertEquals(testsubmission, solution.getSubmission());
    }

    /**
     * tests getter method
     */
    @Test
    void getDBFitHtml() {
        Solution solution = new Solution(testsubmission, DBFitHtml);
        assertEquals(DBFitHtml, solution.getDBFitHtml());
    }

    /**
     * tests if DBFitTags get extracted correctly.
     */
    @Test
    void getDBFitTags() {
        Solution solution = new Solution(testsubmission, DBFitHtml);
        assertEquals(List.of("Aufgabe1", "Aufgabe2"), solution.getDBFitTags());
    }

    /**
     * tests if correct DBFIT-html is created for given submission.
     */
    @Test
    void generateSurveyHTML() {
        Solution solution = new Solution(testsubmission, DBFitHtml);
        String html = solution.generateSurveyHTML(testsubmission);
        assertEquals(surveyHtml, html.trim());
    }


    /**
     * tests getter Solution.getName()
     */
    @Test
    void getName() {
        Solution solution = new Solution(testsubmission, DBFitHtml);
        assertEquals("test_submission", solution.getName());
    }


    /**
     * tests Solution.evaluate() with an fully correct submission.
     * The submission is the same that was used to create the Solution in the first place.
     * Execution of evaluate creates a resultStorage whose createCSV method is used to compare the return value.
     *
     */

    void evaluate() throws fit.exception.FitParseException, SQLException {
        Solution solution = new Solution(testsubmission, DBFitHtml);
        ResultStorage resultStorage = solution.evaluate(Paths.get("/home/test/"), source, resetScript, testsubmission, false);
        String csv = resultStorage.csv(solution.csvCreator());
        String expectedCsv = "\"test/submission.txt\"," +
                " \"[foo foo@bar.de bar]\"," +
                " \"test_submission\"," +
                " \"pass\"," +
                " \"pass\"," +
                " \"2\"," +
                " \"\"";
        assertEquals(
                expectedCsv,
                csv);
    }

    /**
     * tests Solution.evaluate() with an partially incorrect submission.
     *
     */

    void evaluateFail() throws fit.exception.FitParseException, SQLException {
        Solution solution = new Solution(testsubmission, DBFitHtml);
        Submission submissionFail = new Submission(List.of(new Student("foo", "bar", "foo@bar.de")),
                List.of(
                        new TaskNonCallable(Tags.get("Aufgabe1"), "-- comment1\nSELECT '1';"),
                        new TaskNonCallable(Tags.get("Aufgabe2"), "-- comment2\nSELECT 'FAIL' as '2';")),
                "test_submission");
        submissionFail.setPath(Paths.get("/home/test/submission.txt"));

        ResultStorage resultStorage = solution.evaluate(Paths.get("/home/test/"), source, resetScript, submissionFail, false);
        String csv = resultStorage.csv(solution.csvCreator());
        String expectedCsv = "\"test/submission.txt\"," +
                " \"[foo foo@bar.de bar]\"," +
                " \"test_submission\"," +
                " \"pass\"," +
                " \"fail\"," +
                " \"1\"," +
                " \"\"";
        assertEquals(
                expectedCsv,
                csv);
    }


    /**
     * tests if csv header is correctly created.
     */
    @Test
    void generateCSVHeader() {
        Solution solution = new Solution(testsubmission, DBFitHtml);
        String csv = solution.generateCSVHeader();
        String expectedCsv = "\"Path\", \"Authors\", \"Solution\", \"Aufgabe1\", \"Aufgabe2\", \"#Success\", \"ErrorMsg\"";
        assertEquals(expectedCsv, csv);
    }


    /**
     * tests whether solution.getParseResult() creates correct HTML for given fit.Parse.
     *
     */
    @Test
    void getParseResult() throws SQLException, fit.exception.FitParseException {
        fit.Parse parse = new fit.Parse(surveyHtml);
        Solution solution = new Solution(testsubmission, DBFitHtml);
        solution.runDBFitTest(source, resetScript, parse);
        String parseResult = solution.getParseResult(parse);
        String expectedParseResult = "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>dbfit.MySqlTest</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Connect</td>\n" +
                "\t\t<td>localhost</td>\n" +
                "\t\t<td>test</td>\n" +
                "\t\t<td>test</td>\n" +
                "\t\t<td>test</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>/* comment1 */\n" +
                "SELECT '1';</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>1</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td class=\"pass\">1</td>\n" +
                "\t</tr>\n" +
                "</table>\n" +
                "\n" +
                "<table>\n" +
                "\t<tr>\n" +
                "\t\t<td>Query</td>\n" +
                "\t\t<td>/* comment2 */\n" +
                "SELECT '2';</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>2</td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td class=\"pass\">2</td>\n" +
                "\t</tr>\n" +
                "</table>";

        assertEquals(expectedParseResult, parseResult);
    }

    /**
     * tests method Solution.runDBFitTest.
     *
     */
    @Test
    void runDBFitTest() throws fit.exception.FitParseException, SQLException {
        fit.Parse parse = new fit.Parse(surveyHtml);
        Solution solution = new Solution(testsubmission, DBFitHtml);
        Count count = solution.runDBFitTest(source, resetScript, parse);
        assertEquals(new Count(2, 0, 0, 0),count);
    }

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
    void fixedTaskList() {
        Submission sub = new Submission(List.of(new Student("a", "b", "c")),
                List.of(new TaskNonCallable(new Tag("tag1"), "-- comment1\nsql1"),
                        new TaskNonCallable(new Tag("tag3"), "-- comment3\nsql3")), "name");
        List<Task> newList = Solution.fixedTaskList(sub,
                List.of(new Tag("tag1"), new Tag("tag2"), new Tag("tag3")),
                List.of(new Tag("tag1"), new Tag("tag3")));
        List<Task> expectedList = List.of(new TaskNonCallable(new Tag("tag1"), "-- comment1\nsql1"),
                new TaskNonCallable(new Tag("tag2"), "tag missing"),
                new TaskNonCallable(new Tag("tag3"), "-- comment3\nsql3"));
        assertEquals(expectedList, newList);
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
        Solution sol = new Solution(SubmissionParser.parse(solString, StandardCharsets.UTF_8), "");
        Submission sub = SubmissionParser.parse(subString, StandardCharsets.UTF_8);
        Submission newSub = sol.tryToFixTagsFor(sub);
        assertEquals(sub.getAuthors(), newSub.getAuthors());
        assertEquals(sub.getPath(), newSub.getPath());
        assertEquals(sub.getName(), newSub.getName());
        assertEquals(sol.getSubmission().getNonStaticTags(), newSub.getTags());
    }

}
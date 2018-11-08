package de.unifrankfurt.dbis.Submission;

import de.unifrankfurt.dbis.DBFit.ResultStorage;
import de.unifrankfurt.dbis.config.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {
    private static Submission<TaskSQL> testsubmission = null;
    private static String DBFitHtml = null;
    private static DataSource source = null;
    private static SQLScript resetScript;
    private static String surveyHtml = null;

    /**
     * setUp Data used in Tests.
     */
    @BeforeAll
    static void setUp() {
        testsubmission =
                new Submission<>(List.of(new Student("foo", "bar", "foo@bar.de")),
                        List.of(
                                new TaskSQLNonCallable(new Tag("Aufgabe1"), "comment1", "SELECT '1';"),
                                new TaskSQLNonCallable(new Tag("Aufgabe2"), "comment2", "SELECT '2';")),
                        "test_submission");
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
     * @throws fit.exception.FitParseException
     * @throws SQLException
     */
    @Test
    void evaluate() throws fit.exception.FitParseException, SQLException {
        Solution solution = new Solution(testsubmission, DBFitHtml);
        ResultStorage resultStorage = solution.evaluate(Paths.get("/home/test/"), source, resetScript, testsubmission, false);
        String csv = resultStorage.createCSV();
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
     * @throws fit.exception.FitParseException
     * @throws SQLException
     */
    @Test
    void evaluateFail() throws fit.exception.FitParseException, SQLException {
        Solution solution = new Solution(testsubmission, DBFitHtml);
        Submission<TaskSQL> submissionFail = new Submission<>(List.of(new Student("foo", "bar", "foo@bar.de")),
                List.of(
                        new TaskSQLNonCallable(new Tag("Aufgabe1"), "comment1", "SELECT '1';"),
                        new TaskSQLNonCallable(new Tag("Aufgabe2"), "comment2", "SELECT 'FAIL' as '2';")),
                "test_submission");
        submissionFail.setPath(Paths.get("/home/test/submission.txt"));

        ResultStorage resultStorage = solution.evaluate(Paths.get("/home/test/"), source, resetScript, submissionFail, false);
        String csv = resultStorage.createCSV();
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
     * @throws SQLException
     * @throws fit.exception.FitParseException
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
     * @throws fit.exception.FitParseException
     * @throws SQLException
     */
    @Test
    void runDBFitTest() throws fit.exception.FitParseException, SQLException {
        fit.Parse parse = new fit.Parse(surveyHtml);
        Solution solution = new Solution(testsubmission, DBFitHtml);
        Count count = solution.runDBFitTest(source, resetScript, parse);
        assertEquals(new Count(2, 0, 0, 0),count);
    }
}
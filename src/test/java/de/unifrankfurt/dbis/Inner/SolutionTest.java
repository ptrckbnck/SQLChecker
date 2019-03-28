package de.unifrankfurt.dbis.Inner;

import de.unifrankfurt.dbis.Inner.Parser.BaseParser;
import de.unifrankfurt.dbis.config.DataSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void fixedSubmission() {
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
        Base sub = BaseParser.parseDefault(subString).build();
        /*List<String> tags = getNonStaticTags();
        List<String> faultyTags = sub.getNonStaticTags();
        if (faultyTags.isEmpty()) return null; //no tags at all
        if (new HashSet<>(faultyTags).size() != faultyTags.size()) return null; //duplicate keys
        if (!isSublistWithGaps(tags, faultyTags)) return null;
        List<Task> tasks = fixedTaskList(sub, tags, faultyTags);
        Base newSub = new Base(sub.getAuthors(), tasks, sub.getName(), sub.getCharset(), baseType);
        newSub.setPath(sub.getPath());*/ //TODO
        Base newSub = sub;
        assertEquals(sub.getAuthors(), newSub.getAuthors());
        assertEquals(sub.getPath(), newSub.getPath());
        assertEquals(sub.getName(), newSub.getName());
        assertEquals(sol.getNonStaticTags(), newSub.getTags());
    }

   /* @Test
    void evaluate() throws SQLException {
        List<TaskInterface> tasks = List.of(
                new TaskSQL("1", null,null,"SELECT '1';"),
                new TaskSQL("2", null,null, "CREATE TABLE TEST_TABLE( ID int);"),
                new TaskSQL("3", null,null,"INSERT INTO TEST_TABLE (ID)\nVALUES (1);"),
                new TaskSQL("4", null,null, "SELECT * FROM TEST_TABLE;")
        );
        Base base = new Base(tasks, "test", StandardCharsets.UTF_8, BaseType.solution);
        Solution solution = base.generateSolution(datasource, resetScript);
        ResultStorage resultStorage = null;
        solution.evaluate(resultStorage, datasource, resetScript, base, true);
        Report report = new Report();
        report.setRootPath(Paths.get("root"));
        report.setSolutionMetadata(solution.getMetaData());
        report.add(resultStorage);
        List<String> csv = report.getCSV();
        csv.forEach(System.out::println);

    }*///TODO

    /*@Test
    void evaluate2() throws SQLException {
        List<Task> tasks = List.of(
                new TaskSQL(new Tag("1"), "SELECT '1';"),
                new TaskSQL(new Tag("2"), "CREATE TABLE TEST_TABLE( ID int);"),
                new TaskSQL(new Tag("3"), "INSERT INTO TEST_TABLE (ID)\nVALUES (1);"),
                new TaskSQL(new Tag("4"), "SELECT * FROM TEST_TABLE;")
        );
        Base base = new Base(tasks, "test", StandardCharsets.UTF_8, baseType);
        Solution solution = base.generateSolution(datasource, resetScript);

        List<Task> tasks2 = List.of(
                new TaskSQL(new Tag("1"), "SELECT '1';"),
                new TaskSQL(new Tag("2"), "CREATE TABLE TEST_TABLE( ID int);"),
                new TaskSQL(new Tag("3"), "INSERT INTO TEST_TABLE (ID)\nVALUES (1);"),
                new TaskSQL(new Tag("4"), "SELECT '1';")
        );
        Base base2 = new Base(tasks2, "test", StandardCharsets.UTF_8, baseType);


        ResultStorage resultStorage = null;
        solution.evaluate(resultStorage, datasource, resetScript, base2, true);
        Report report = new Report();
        report.setRootPath(Paths.get("root"));
        report.setSolutionMetadata(solution.getMetaData());
        report.add(resultStorage);
        List<String> csv = report.getCSV();
        csv.forEach(System.out::println);

    }*///TODO
}
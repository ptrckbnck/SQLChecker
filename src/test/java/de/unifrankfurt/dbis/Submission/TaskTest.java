package de.unifrankfurt.dbis.Submission;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void sqlWOComment() {
        TaskNonCallable task = new TaskNonCallable(Tags.get("tag"), "-- test\n" +
                "#test\n" +
                "/*test\n" +
                "test2*/\n" +
                "sql");
        assertEquals("sql", task.sqlWOComment());
    }

    @Test
    void sqlWOComment2() {
        TaskNonCallable task = new TaskNonCallable(Tags.get("tag"), "-- test\n" +
                "sql -- test");
        assertEquals("sql", task.sqlWOComment());
    }

    @Test
    void sqlWOComment3() {
        TaskNonCallable task = new TaskNonCallable(Tags.get("tag"), "-- test\n" +
                "sql #test");
        assertEquals("sql", task.sqlWOComment());
    }

    @Test
    void sqlWOComment4() {
        TaskNonCallable task = new TaskNonCallable(Tags.get("tag"), "-- test\n" +
                "sql /*test */");
        assertEquals("sql", task.sqlWOComment());
    }

    @Test
    void sqlWOComment5() {
        TaskNonCallable task = new TaskNonCallable(Tags.get("tag"), "-- test\n" +
                "sql \n" +
                "sql2 # test\n" +
                "sql3 -- test\n" +
                "sql4 /*test */");
        assertEquals("sql \n" +
                "sql2 \n" +
                "sql3 \n" +
                "sql4", task.sqlWOComment());
    }
}
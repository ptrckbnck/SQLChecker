package de.unifrankfurt.dbis.Submission;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void sqlWOComment() {
        TaskSQL task = new TaskSQL(Tags.get("tag"), null, "-- test\n" +
                "#test\n" +
                "/*test\n" +
                "test2*/\n" +
                "sql");
        assertEquals("sql", task.sqlWOComment());
    }

    @Test
    void sqlWOComment2() {
        TaskSQL task = new TaskSQL(Tags.get("tag"), null, "-- test\n" +
                "sql -- test");
        assertEquals("sql", task.sqlWOComment());
    }

    @Test
    void sqlWOComment3() {
        TaskSQL task = new TaskSQL(Tags.get("tag"), null, "-- test\n" +
                "sql #test");
        assertEquals("sql", task.sqlWOComment());
    }

    @Test
    void sqlWOComment4() {
        TaskSQL task = new TaskSQL(Tags.get("tag"), null, "-- test\n" +
                "sql /*test */");
        assertEquals("sql", task.sqlWOComment());
    }

    @Test
    void sqlWOComment5() {
        TaskSQL task = new TaskSQL(Tags.get("tag"), null, "-- test\n" +
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
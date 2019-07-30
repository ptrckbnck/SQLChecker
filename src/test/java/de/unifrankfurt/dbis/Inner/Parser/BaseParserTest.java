package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.Base;
import de.unifrankfurt.dbis.Inner.BaseBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BaseParserTest {



    @Test
    void test_head() {
        String h =
                "/*%%%%head%%\n" +
                        "{\"type\": \"submission\",\n" +
                        "\"name\": \"test\",\n" +
                        "\"authors\": [[\"Yusuf Baran\",\"yusufbaran66@yahoo.de\",\"6364384\"]" +
                        ",[\"Angelos Ioannou\",\"angelos.ioannou@gmx.de\",\"6081379\"]]\n" +
                        "}\n" +
                        "%%*/\n";
        BaseParser spn = new BaseParser();
        spn.registerSubTokenCreator(ParseTokenHead::fromRawToken);
        List<ParseToken> a = spn.analyzeTokens(BaseParser.tokenizer(h));
        assertEquals(1, a.size());
        final ParseTokenHead parseTokenHead = (ParseTokenHead) a.get(0);
        assertEquals("test", parseTokenHead.getBaseName());
        assertEquals(2, parseTokenHead.getStudents().size());
        assertEquals(BaseType.submission, parseTokenHead.getType());
    }

    @Test
    void test2() {
        String h =
                "/*%%%%head%%\n" +
                        "{\"type\": \"submission\",\n" +
                        "\"name\": \"test\",\n" +
                        "\"authors\": [[\"Yusuf Baran\",\"yusufbaran66@yahoo.de\",\"6364384\"]" +
                        ",[\"Angelos Ioannou\",\"angelos.ioannou@gmx.de\",\"6081379\"]]\n" +
                        "}\n" +
                        "%%*/\n" +
                        "\n" +
                        "/*%%1a%%task%%" +
                        "[\"a\",\"b\",\"c\"].[1,2,3] %%*/\n" +
                        "Select * from test;\n" +
                        "\n" +
                        "/*%%1b%%" +
                        "task%%" +
                        "[\"a\",\"b\",\"c\"]" +
                        ".[1,2,3]" +
                        "%%%%task%%*/\n" +
                        "Select * from test2;";
        BaseParser spn = new BaseParser();
        spn.registerSubTokenCreator(ParseTokenHead::fromRawToken);
        spn.registerSubTokenCreator(ParseTokenTask::fromRawToken);
        List<RawToken> tokenized = BaseParser.tokenizer(h);
        List<ParseToken> a = spn.analyzeTokens(tokenized);
        assertEquals(3, a.size());
    }

    @Test
    void test3() {
        String h =
                "/*%%%%head%%\n" +
                        "{\"type\": \"solution\",\n" +
                        "\"name\": \"test\",\n" +
                        "\"authors\": [[\"Yusuf Baran\",\"yusufbaran66@yahoo.de\",\"6364384\"]" +
                        ",[\"Angelos Ioannou\",\"angelos.ioannou@gmx.de\",\"6081379\"]]\n" +
                        "}\n" +
                        "%%*/\n" +
                        "\n" +
                        "/*%%1a%%task%%" +
                        "[\"a\",\"b\",\"c\"].[1,2,3].2 %%*/\n" +
                        "Select * from test;\n" +
                        "\n" +
                        "/*%%1b%%task%%" +
                        "[\"a\",\"b\",\"c\"]" +
                        ".[1,2,3]" +
                        "%%*/\n" +
                        "Select * from test2;" +
                        "\n" +
                        "/*%%s%%static%%" +
                        "[1,2,3]" +
                        "%%*/\n" +
                        "Select * from test3;";
        BaseBuilder a = BaseParser.parseDefault(h);
        Base base = a.build();
        assertEquals(BaseType.solution, base.getType());
        assertEquals("test", base.getName());
        assertEquals("Yusuf Baran", base.getAuthors().get(0).getName());
        List<TaskInterface> tasks = base.getTasks();
        TaskInterface task1 = tasks.get(0);
        TaskInterface task2 = tasks.get(1);
        TaskInterface task3 = tasks.get(2);

        assertEquals("1a", task1.getName());
        assertEquals("1b", task2.getName());
        assertEquals("s", task3.getName());
    }

    @Test
    void tokenizerTask() {
        String h =
                "/*%%1a%%\n" +
                        "task%%\n" +
                        "2.eins.[\"a\",\"b\",\"c\"].[1,2,3]%%*/\n" +
                        "Select * from test1;\n" +
                        "\n" +
                        "/*%%%%type%%*/\n" +
                        "Select * from test2;" +
                        "\n" +
                        "/*%%task3%%" +
                        "type%%" +
                        "addition%%*/\n" +
                        "Select * from test3;" +
                        "\n" +
                        "/*%%task4%%%%*/\n" +
                        "Select * from test4";
        List<RawToken> tokenized = BaseParser.tokenizer(h);
        assertEquals("task", tokenized.get(0).getType());
        assertEquals("1a", tokenized.get(0).getName());
        assertTrue(tokenized.get(0).getBody().contains("test1"));
        assertTrue(tokenized.get(0).getAddition().contains("[1,2,3]"));

        assertEquals("type", tokenized.get(1).getType());
        assertNull(tokenized.get(1).getName());
        assertTrue(tokenized.get(1).getBody().contains("test2"));

        assertEquals("type", tokenized.get(2).getType());
        assertEquals("addition", tokenized.get(2).getAddition());
        assertEquals("task3", tokenized.get(2).getName());
        assertTrue(tokenized.get(2).getBody().contains("test3"));

        assertNull(tokenized.get(3).getType());
        assertEquals("task4", tokenized.get(3).getName());
        assertTrue(tokenized.get(3).getBody().contains("test4"));
    }


}
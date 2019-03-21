package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.Base;
import de.unifrankfurt.dbis.Inner.BaseBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseParserTest {



    @Test
    void test() {
        String h =
                "/*%%head%%\n" +
                        "{\"type\": \"submission\",\n" +
                        "\"name\": \"test\",\n" +
                        "\"authors\": [[\"Yusuf Baran\",\"yusufbaran66@yahoo.de\",\"6364384\"]" +
                        ",[\"Angelos Ioannou\",\"angelos.ioannou@gmx.de\",\"6081379\"]]\n" +
                        "}\n" +
                        "%%*/\n";
        BaseParser spn = new BaseParser();
        spn.registerSubTokenCreator(ParseTokenHead::fromRawToken);
        List<ParseToken> a = spn.analyzeTokens(BaseParser.tokenizer(h));
        assertEquals(3, a.size());
    }

    @Test
    void test2() {
        String h =
                "/*%%head%%\n" +
                        "{\"type\": \"submission\",\n" +
                        "\"name\": \"test\",\n" +
                        "\"authors\": [[\"Yusuf Baran\",\"yusufbaran66@yahoo.de\",\"6364384\"]" +
                        ",[\"Angelos Ioannou\",\"angelos.ioannou@gmx.de\",\"6081379\"]]\n" +
                        "}\n" +
                        "%%*/\n" +
                        "\n" +
                        "/*%%1a%%" +
                        "[\"a\",\"b\",\"c\"].[1,2,3] %%*/\n" +
                        "Select * from test;\n" +
                        "\n" +
                        "/*%%task%%" +
                        "1b" +
                        ".[\"a\",\"b\",\"c\"]" +
                        ".[1,2,3]" +
                        "%%*/\n" +
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
                "/*%%head%%\n" +
                        "{\"type\": \"solution\",\n" +
                        "\"name\": \"test\",\n" +
                        "\"authors\": [[\"Yusuf Baran\",\"yusufbaran66@yahoo.de\",\"6364384\"]" +
                        ",[\"Angelos Ioannou\",\"angelos.ioannou@gmx.de\",\"6081379\"]]\n" +
                        "}\n" +
                        "%%*/\n" +
                        "\n" +
                        "/*%%1a%%" +
                        "[\"a\",\"b\",\"c\"].[1,2,3].2 %%*/\n" +
                        "Select * from test;\n" +
                        "\n" +
                        "/*%%task%%" +
                        "1b" +
                        ".[\"a\",\"b\",\"c\"]" +
                        ".[1,2,3]" +
                        "%%*/\n" +
                        "Select * from test2;" +
                        "\n" +
                        "/*%%static%%" +
                        "s" +
                        ".[1,2,3]" +
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
                        "Select * from test;\n" +
                        "\n" +
                        "/*%%task2%%%%*/\n" +
                        "Select * from test2;" +
                        "\n" +
                        "/*%%task3%%" +
                        "type%%" +
                        "addition%%*/\n" +
                        "Select * from test3;" +
                        "\n" +
                        "/*%%task4%%*/\n" +
                        "Select * from test4";
        List<RawToken> tokenized = BaseParser.tokenizer(h);
        System.err.println(tokenized);
    }

    @Test
    void tokenizerStatic() {
        String h =
                "/*%%1a%%\n" +
                        "static%%\n" +
                        "2.eins.[1,2,3]%%*/\n" +
                        "Select * from test;\n" +
                        "\n" +
                        "/*%%static2%%static%%*/\n" +
                        "Select * from test2;" +
                        "\n" +
                        "/*%%static3%%" +
                        "static%%" +
                        "addition%%*/\n" +
                        "Select * from test3;";
        List<RawToken> tokenized = BaseParser.tokenizer(h);
        System.err.println(tokenized);
    }
}
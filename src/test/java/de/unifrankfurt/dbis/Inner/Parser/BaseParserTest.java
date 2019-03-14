package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.BaseBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

class BaseParserTest {


    void tokenizer() {
        List<RawToken> a = BaseParser.tokenizer(
                "/*%%head%%\n" +
                        "{\"type\": \"submission\",\n" +
                        "\"name\": \"test\"\t\n" +
                        "\"authors\": [\"Yusuf Baran\",\"yusufbaran66@yahoo.de\",\"6364384\"],[\"Angelos Ioannou\",\"angelos.ioannou@gmx.de\",\"6081379\"],\n" +
                        "}\n" +
                        "%%*/\n" +
                        "\n" +
                        "/*%%1a%%-[\"a\",\"b\",\"c\"]-[1,2,3] %%*/\n" +
                        "Select * from test;\n" +
                        "\n" +
                        "/*%%1b%%-[\"a\",\"b\",\"c\"]-[1,2,3] %%*/\n" +
                        "Select * from test2;");
        System.err.println(a);
    }

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
        spn.registerSubTokenCreator(SubTokenHead::fromRawToken);
        List<SubToken> a = spn.analyzeTokens(BaseParser.tokenizer(h));
        System.err.println(a);
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
        spn.registerSubTokenCreator(SubTokenHead::fromRawToken);
        spn.registerSubTokenCreator(SubTokenTask::fromRawToken);
        List<RawToken> tokenized = BaseParser.tokenizer(h);
        List<SubToken> a = spn.analyzeTokens(tokenized);
        System.err.println(a);
    }

    @Test
    void test3() {
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
        BaseBuilder a = BaseParser.parseDefault(h);
        System.err.println(a);
        System.err.println(a.build());
    }
}
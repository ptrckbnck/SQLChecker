package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.BaseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BaseParser {
    private List<Function<RawToken, ParseToken>> createSubTokenList = new ArrayList<>();

    public static BaseBuilder parseDefault(String text) {
        return new BaseParser()
                .registerSubTokenCreator(ParseTokenHead::fromRawToken)
                .registerSubTokenCreator(ParseTokenStatic::fromRawToken)
                .registerSubTokenCreator(ParseTokenTask::fromRawToken)
                .parse(text);
    }

    public static List<RawToken> tokenizer(String toParse) {
        List<RawToken> rawTokenList = new ArrayList<>();
        List<String> stringList = splitTags(toParse);
        for (String string : stringList) {
            final int endTag = string.indexOf("%%*/");
            final String tag = string.substring(4, endTag);
            final int split = tag.indexOf("%%");
            String name;
            String addition;
            String body;
            if (split != -1) {
                name = tag.substring(0, split).trim();
                addition = tag.substring(split + 2).trim();
            } else {
                name = tag.trim();
                addition = "";

            }
            body = string.substring(endTag + 4).trim();
            rawTokenList.add(new RawToken(name, addition, body));
        }
        return rawTokenList;
    }

    private static List<String> splitTags(String toParse) {
        List<String> stringList = new ArrayList<>();

        int startTag = toParse.indexOf("/*%%");
        if (startTag == -1) {
            return stringList;
        }

        toParse = toParse.substring(startTag);
        while (!toParse.isEmpty()) {
            int endTag = toParse.indexOf("%%*/");
            startTag = toParse.indexOf("/*%%", endTag);
            if (startTag == -1) {
                stringList.add(toParse.trim());
                toParse = "";
            } else {
                stringList.add(toParse.substring(0, startTag).trim());
                toParse = toParse.substring(startTag);
            }
        }
        return stringList;
    }

    public BaseBuilder parse(List<String> lines) {
        return parse(String.join("\n", lines));
    }

    public BaseBuilder parse(String text) {
        List<RawToken> tokenized = tokenizer(text);
        List<ParseToken> analyzed = analyzeTokens(tokenized);
        BaseBuilder bb = new BaseBuilder();
        analyzed.forEach(x -> x.build(bb));
        return bb;
    }

    public BaseParser registerSubTokenCreator(Function<RawToken, ParseToken> func) {
        this.createSubTokenList.add(func);
        return this;
    }

    public ParseToken analyzeToken(RawToken rawToken) {
        for (Function<RawToken, ParseToken> func : createSubTokenList) {
            ParseToken parseToken = func.apply(rawToken);
            if (!Objects.isNull(parseToken)) return parseToken;
        }
        return null;
    }

    public List<ParseToken> analyzeTokens(List<RawToken> tokens) {
        return tokens.stream().map(this::analyzeToken).collect(Collectors.toList());
    }


}

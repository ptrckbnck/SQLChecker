package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.BaseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * All tokens have one of the following style:
 * \/*%%<Name>%%<Type>%%<Addition>%%*\/
 * <Body>
 * \/*%%<Name>%%<Addition>%%*\/
 * <Body>
 * \/*%%<Name>%%*\/
 * <Body>
 */
public class BaseParser {
    private List<Function<RawToken, ParseToken>> createSubTokenList = new ArrayList<>();

    public static BaseBuilder parseDefault(String text) {
        return getDefaultBaseParser()
                .parse(text);
    }

    public static BaseParser getDefaultBaseParser() {
        return new BaseParser()
                .registerSubTokenCreator(ParseTokenHead::fromRawToken)
                .registerSubTokenCreator(ParseTokenStatic::fromRawToken)
                .registerSubTokenCreator(ParseTokenTask::fromRawToken)
                .registerSubTokenCreator(ParseTokenUnknown::fromRawToken);
    }

    public static List<RawToken> tokenizer(String toParse) {
        List<RawToken> rawTokenList = new ArrayList<>();
        List<String> stringList = splitTags(toParse);
        for (String string : stringList) {
            final int endTag = string.indexOf("%%*/");
            final String head = string.substring(4, endTag);
            final List<String> split = List.of(head.split("%%"));
            final int size = split.size();
            final String name = split.get(0).isBlank() ? null : split.get(0).trim();
            final String type = (size > 1 && !split.get(1).isBlank()) ? split.get(1).trim() : null;
            final String addition = (size > 2 && !split.get(2).isBlank()) ? split.get(2).trim() : null;
            final String body = string.substring(endTag + 4).trim();
            rawTokenList.add(new RawToken(name, type, addition, body));
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
        analyzed.forEach(x -> {
            if (Objects.nonNull(x)) {
                x.build(bb);
            }
        });
        return bb;
    }

    public BaseParser registerSubTokenCreator(Function<RawToken, ParseToken> func) {
        this.createSubTokenList.add(func);
        return this;
    }

    public ParseToken analyzeToken(RawToken rawToken) {
        return createSubTokenList.stream()
                .map(x -> x.apply(rawToken))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public List<ParseToken> analyzeTokens(List<RawToken> tokens) {
        return tokens.stream()
                .map(this::analyzeToken)
                .collect(Collectors.toList());
    }


}

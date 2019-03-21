package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.BaseBuilder;

public class ParseTokenUnknown implements ParseToken {
    private final String name;
    private final String type;
    private final String addition;
    private final String body;

    public ParseTokenUnknown(String name, String type, String addition, String body) {
        this.name = name;
        this.type = type;
        this.addition = addition;
        this.body = body;
    }

    public static ParseTokenUnknown fromRawToken(RawToken rawToken) {
        String name = rawToken.getName();
        String type = rawToken.getType();
        String addition = rawToken.getAddition();
        String body = rawToken.getBody();
        return new ParseTokenUnknown(name, type, addition, body);
    }

    @Override
    public void build(BaseBuilder bb) {
        //ignored
    }
}

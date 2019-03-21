package de.unifrankfurt.dbis.Inner.Parser;

import java.util.StringJoiner;

public class RawToken {
    private final String name;
    private final String type;
    private final String addition;
    private final String body;

    public RawToken(String name, String type, String addition, String body) {
        this.name = name;
        this.type = type;
        this.addition = addition;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAddition() {
        return addition;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RawToken.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("type='" + type + "'")
                .add("addition='" + addition + "'")
                .add("body='" + body + "'")
                .toString();
    }
}

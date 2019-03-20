package de.unifrankfurt.dbis.Inner.Parser;

import java.util.StringJoiner;

public class RawToken {
    private final String type;
    private final String addition;
    private final String body;

    public RawToken(String type, String addition, String body) {
        this.type = type;
        this.addition = addition;
        this.body = body;
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
                .add("type='" + type + "'")
                .add("addition='" + addition + "'")
                .add("body='" + body + "'")
                .toString();
    }
}

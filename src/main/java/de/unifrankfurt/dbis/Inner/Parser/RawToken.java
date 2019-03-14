package de.unifrankfurt.dbis.Inner.Parser;

public class RawToken {
    private final String name;
    private final String addition;
    private final String body;

    public RawToken(String name, String addition, String body) {
        this.name = name;
        this.addition = addition;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public String getAddition() {
        return addition;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Token{" + "name='" + name + '\'' +
                ", addition='" + addition + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}

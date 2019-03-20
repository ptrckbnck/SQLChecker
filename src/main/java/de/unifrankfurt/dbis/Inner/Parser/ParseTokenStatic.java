package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.BaseBuilder;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static de.unifrankfurt.dbis.Inner.Parser.ParseTokenTask.parseOrder;


public class ParseTokenStatic implements ParseToken {
    public static String id = "static";
    public static String delimiter = ".";
    private final String name;
    private final List<Integer> ordering;
    private final String body;

    public ParseTokenStatic(String name, List<Integer> ordering, String body) {
        this.name = name;
        this.ordering = ordering;
        this.body = body;
    }

    public static ParseToken fromRawToken(RawToken rawToken) {
        if (!rawToken.getName().equals(id)) return null;
        List<String> splitted = List.of(rawToken.getAddition().split("\\" + (delimiter)));
        String name = null;
        List<Integer> order = null;
        if (splitted.size() > 0) {
            name = splitted.get(0);
        }
        if (splitted.size() > 1) {
            order = parseOrder(splitted.get(1));
        }

        return new ParseTokenStatic(name, order, rawToken.getBody());
    }

    private String serializedOrder() {
        if (Objects.isNull(ordering)) return "";
        return "[" + this.ordering.stream().map(String::valueOf).collect(Collectors.joining(",")) + "]";
    }

    public String getName() {
        return name;
    }

    public List<Integer> getOrdering() {
        return ordering;
    }

    public String getBody() {
        return body;
    }

    public String serialize() {
        return "/*%%" + id + "%%" + serializedHead() + "%%*/\n" + this.body;
    }

    private String serializedHead() {
        return this.name + delimiter + this.serializedOrder();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParseTokenStatic.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("ordering=" + ordering)
                .add("body='" + body + "'")
                .toString();
    }

    @Override
    public void build(BaseBuilder bb) {
        bb.addTask(new TaskStatic(name, ordering, body));
    }
}

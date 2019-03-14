package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.BaseBuilder;

import java.util.List;
import java.util.StringJoiner;

import static de.unifrankfurt.dbis.Inner.Parser.SubTokenTask.parseOrder;


public class SubTokenStatic implements SubToken {
    public static String id = "static";
    public static String delimiter = ".";
    private final String name;
    private final List<Integer> ordering;
    private final String body;

    public SubTokenStatic(String name, List<Integer> ordering, String body) {
        this.name = name;
        this.ordering = ordering;
        this.body = body;
    }


    public static SubToken fromRawToken(RawToken rawToken) {
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

        return new SubTokenStatic(name, order, rawToken.getBody());
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

    @Override
    public String toString() {
        return new StringJoiner(", ", SubTokenStatic.class.getSimpleName() + "[", "]")
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

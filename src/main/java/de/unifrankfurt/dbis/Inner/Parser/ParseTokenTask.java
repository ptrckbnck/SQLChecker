package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.BaseBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;


/**
 * \/*%%task%%<Name>.<Punkte>.<Aufgabengruppe>.<Schema>.<Order>%%*\/
 * <Body>
 * <p>
 * \/*%%<Name>%%<Punkte>.<Aufgabengruppe>.<Schema>.<Order>%%*\/
 * <Body>
 */
public class ParseTokenTask implements ParseToken {
    public static String id = "task";
    public static String delimiter = ".";
    private final String name;
    private final Integer score;
    private final String group;
    private final List<String> schema;
    private final List<Integer> order;
    private final String body;

    public ParseTokenTask(String name, Integer score, String group, List<String> schema, List<Integer> order, String body) {
        this.name = name;
        this.score = score;
        this.group = group;
        this.schema = schema;
        this.order = order;
        this.body = body;
    }

    public static ParseTokenTask fromRawToken(RawToken rawToken) {

        final String type = rawToken.getType();
        if (Objects.isNull(type)) {
            return null;
        }
        String addition = rawToken.getAddition();
        if (Objects.isNull(addition)) {
            addition = "";
        }
        String body = rawToken.getBody();
        if (Objects.isNull(rawToken.getBody())) {
            body = "";
        }

        List<String> splitted = Arrays.asList(rawToken.getAddition().split("\\" + (delimiter)));
        if (!Objects.equals(type, id)) {
            splitted.add(0, type);
        }
        String name = null;
        Integer score = null;
        String group = null;
        List<String> head = null;
        List<Integer> order = null;

        final int size = splitted.size();
        if (size > 0) {
            name = splitted.get(0);
        }
        if (size > 1) {
            score = Integer.valueOf(splitted.get(1));
        }
        if (size > 2) {
            group = splitted.get(2);
        }
        if (size > 3) {
            parseSchema(splitted.get(3));
        }
        if (size > 4) {
            order = parseOrder(splitted.get(2));
        }
        return new ParseTokenTask(name, score, group, head, order, body);
    }

    public static List<Integer> parseOrder(String s) {
        List<String> splitted = split(s);
        if (Objects.isNull(splitted)) return null;
        List<Integer> i = splitted.stream().map(x -> {
                    try {
                        return Integer.valueOf(x);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
        ).collect(Collectors.toList());
        if (i.contains(null)) {
            return null;
        } else {
            return i;
        }
    }

    private static List<String> split(String s) {
        s = s.trim();
        if (!s.startsWith("[") && !s.endsWith("]")) return null;
        s = s.substring(1, s.length() - 1);
        return Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toList());

    }

    private static List<String> parseSchema(String s) {
        List<String> splitted = split(s);
        if (Objects.isNull(splitted)) return null;
        return splitted.stream().map(x -> {
            if (x.length() > 1 && x.startsWith("\"") && x.endsWith("\"")) {
                return x.substring(1, x.length() - 1);
            }
            return x;
        }).collect(Collectors.toList());
    }

    private String serializedHead() {
        return this.name + "." + this.serializedSchema() + "." + this.serializedOrder();
    }

    private String serializedSchema() {
        if (Objects.isNull(this.schema)) return "";
        return "[\"" + String.join("\", \"", this.schema) + "\"]";
    }

    private String serializedOrder() {
        if (Objects.isNull(order)) return "";
        return "[" + this.order.stream().map(String::valueOf).collect(Collectors.joining(",")) + "]";
    }

    public String getName() {
        return name;
    }

    public List<String> getSchema() {
        return schema;
    }

    public List<Integer> getOrder() {
        return order;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParseTokenTask.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("score=" + score)
                .add("group='" + group + "'")
                .add("schema=" + schema)
                .add("order=" + order)
                .add("body='" + body + "'")
                .toString();
    }

    @Override
    public void build(BaseBuilder bb) {
        bb.addTask(new TaskSQL(name, schema, order, body));
    }

    public String serialize() {
        return "/*%%" + id + "%%" + this.serializedHead() + "%%*/\n" + this.body;
    }
}

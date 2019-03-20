package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.BaseBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ParseTokenTask implements ParseToken {
    public static String id = "task";
    public static String delimiter = ".";
    private final String name;
    private final List<String> head;
    private final List<Integer> order;
    private final String body;

    public ParseTokenTask(String name, List<String> head, List<Integer> order, String body) {
        this.name = name;
        this.head = head;
        this.order = order;
        this.body = body;
    }

    public static ParseToken fromRawToken(RawToken rawToken) {
        List<String> splitted = List.of(rawToken.getAddition().split("\\" + (delimiter)));
        String name = null;
        List<String> head = null;
        List<Integer> order = null;
        if (rawToken.getName().equals(id)) {
            if (splitted.size() > 0) {
                name = splitted.get(0);
            }
            if (splitted.size() > 1) {
                head = parseHead(splitted.get(1));
            }
            if (splitted.size() > 2) {
                order = parseOrder(splitted.get(2));
            }

        } else {
            //catch all other ids
            name = rawToken.getName();
            if (splitted.size() > 0) {
                head = parseHead(splitted.get(0));
            }
            if (splitted.size() > 1) {
                order = parseOrder(splitted.get(1));
            }
        }
        return new ParseTokenTask(name, head, order, rawToken.getBody());
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

    private static List<String> parseHead(String s) {
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
        if (Objects.isNull(this.head)) return "";
        return "[\"" + String.join("\", \"", this.head) + "\"]";
    }

    private String serializedOrder() {
        if (Objects.isNull(order)) return "";
        return "[" + this.order.stream().map(String::valueOf).collect(Collectors.joining(",")) + "]";
    }

    public String getName() {
        return name;
    }

    public List<String> getHead() {
        return head;
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
                .add("head=" + head)
                .add("order=" + order)
                .add("body='" + body + "'")
                .toString();
    }

    @Override
    public void build(BaseBuilder bb) {
        bb.addTask(new TaskSQL(name, head, order, body));
    }

    public String serialize() {
        return "/*%%" + id + "%%" + this.serializedHead() + "%%*/\n" + this.body;
    }
}

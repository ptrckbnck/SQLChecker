package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.BaseBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;


/**
 * \/*%%Name%%<Type>.<Punkte>.<Aufgabengruppe>.<Schema>.<Order>%%*\/
 * <Body>
 * <p>
 * \/*%%<Name>%%<Punkte>.<Aufgabengruppe>.<Schema>.<Order>%%*\/
 * <Body>
 */
public class ParseTokenTask implements ParseToken {
    public static final String id = "task";
    public static final String delimiter = ".";
    protected final String name;
    protected final Integer score;
    protected final String group;
    protected final List<String> schema;
    protected final List<Integer> order;
    protected final String body;

    public ParseTokenTask(String name, Integer score, String group, List<String> schema, List<Integer> order, String body) {
        this.name = name;
        this.score = score;
        this.group = group;
        this.schema = schema;
        this.order = order;
        this.body = body;
    }

    /**
     * this the default Token. Any not known id will be interpreted as task.
     *
     * @param rawToken
     * @return
     */
    public static ParseTokenTask fromRawToken(RawToken rawToken) {
        final String name = rawToken.getName(); //should not be null
        if (Objects.isNull(name)) {
            return null;
        }
        final String type = rawToken.getType();
        if (!Objects.isNull(type) && !type.equals(id)){
            return null;
        }
        final String addition = Objects.requireNonNullElse(rawToken.getAddition(), "");
        final String body = Objects.requireNonNullElse(rawToken.getBody(), "");

        final List<String> splitted = Arrays.asList(addition.split("\\" + (delimiter)));
        final int size = splitted.size();

        Integer score;
        try {
            score = Integer.valueOf(splitted.get(0));
        } catch (NumberFormatException ignored) {
            score = null;
        }
        String group = (size > 1) ? splitted.get(1) : null;
        List<String> schema = (size > 2) ? parseSchema(splitted.get(2)) : null;
        List<Integer> order = (size > 3) ? parseOrder(splitted.get(3)) : null;

        return new ParseTokenTask(name, score, group, schema, order, body);
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
        if (!s.startsWith("[") && !s.endsWith("]")) {
            return null;
        }
        s = s.substring(1, s.length() - 1);
        return Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toList());

    }

    static List<String> parseSchema(String s) {
        List<String> splitted = split(s);
        if (Objects.isNull(splitted)) {
            return null;
        }
        return splitted.stream().map(x -> {
            if (x.length() > 1 && x.startsWith("\"") && x.endsWith("\"")) {
                return x.substring(1, x.length() - 1);
            }
            return x;
        }).collect(Collectors.toList());
    }

    public static String getDelimiter() {
        return ParseTokenTask.delimiter;
    }

    protected String serializedExtra() {
        List<String> t = List.of(getScore().toString(),
                getGroup(),
                serializedSchema(),
                serializedOrder());
        return String.join(delimiter, t);
    }

    protected String serializedSchema() {
        return "[\"" + String.join("\", \"", this.schema) + "\"]";
    }

    protected String serializedOrder() {
        return "[" + this.order
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + "]";
    }


    public String getName() {
        return name;
    }

    public Integer getScore() {
        return score;
    }

    public String getGroup() {
        return group;
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
        bb.addTask(new TaskSQL(name, score, group, schema, order, body));
    }

    public String serialize() {
        List<String> tagList = List.of(getName(), getID(), serializedExtra())
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return "/*%%" + String.join("%%", tagList) + "%%*/";
    }

    protected String getID() {
        return ParseTokenTask.id;
    }
}

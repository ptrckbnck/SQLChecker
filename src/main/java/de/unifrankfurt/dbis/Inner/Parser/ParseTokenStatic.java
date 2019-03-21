package de.unifrankfurt.dbis.Inner.Parser;

import de.unifrankfurt.dbis.Inner.BaseBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;


public class ParseTokenStatic extends ParseTokenTask implements ParseToken {
    public static String id = "static";
    public static String delimiter = ".";


    public ParseTokenStatic(String name, Integer score, String group, List<Integer> order, String body) {
        super(name, score, group, null, order, body);
    }

    /**
     * this the default Token. Any not known id will be interpreted as task.
     *
     * @param rawToken
     * @return
     */
    public static ParseTokenStatic fromRawToken(RawToken rawToken) {
        final String name = rawToken.getName(); //should not be null
        if (Objects.isNull(name)) {
            return null;
        }

        final String type = rawToken.getType();
        if (!Objects.equals(type, id)) {
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
        List<Integer> order = (size > 2) ? parseOrder(splitted.get(2)) : null;

        return new ParseTokenStatic(name, score, group, order, body);
    }

    @Override
    protected String serializedHead() {
        return getScore()
                + getDelimiter()
                + getGroup()
                + getDelimiter()
                + serializedOrder();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParseTokenStatic.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("score=" + score)
                .add("group='" + group + "'")
                .add("schema=" + schema)
                .add("order=" + order)
                .add("body='" + body + "'")
                .toString();
    }

    @Override
    protected String getID() {
        return ParseTokenStatic.id;
    }

    @Override
    public void build(BaseBuilder bb) {
        bb.addTask(new TaskStatic(getName(), getScore(), getGroup(), getOrder(), getBody()));
    }
}

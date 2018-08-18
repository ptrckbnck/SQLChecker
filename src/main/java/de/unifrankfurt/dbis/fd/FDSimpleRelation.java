package de.unifrankfurt.dbis.fd;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * A representation for a one line functional relation
 * Has the function to parse a String.
 *
 * @author Patrick Bonack
 * @version 1.0
 * @since 18.11.2017
 */
public class FDSimpleRelation {


    /**
     * the "left side" of ->
     */
    protected FDKey key;

    /**
     * values the "right side" of ->
     */
    protected HashSet<String> values;

    public FDSimpleRelation() {
        this.key = new FDKey();
        this.values = new HashSet<>();
    }

    public FDSimpleRelation(Collection<String> key, Collection<String> values) {
        this.key = new FDKey(key);
        this.values = new HashSet<>(values);
    }


    /**
     * creates a FDSimpleRelation from String
     *
     * @param rel       the String to be parsed
     * @param delimiter the delimiter of attributes
     * @return current updated object if rel has right syntax else null
     */
    public static FDSimpleRelation parse(String rel, String delimiter) {
        if (rel.isEmpty()) return new FDSimpleRelation();
        if (!rel.contains("->")) return null;
        List<String> left;
        List<String> right;
        if (delimiter.equals("")) {
            String[] a = rel.split("->");
            if (a.length != 2) return null;
            if (a[0].length() == 0) return null;
            left = Arrays.asList(a[0].split(""));
            right = Arrays.asList(a[1].split(""));
        } else {
            List<String> list = Arrays.asList(rel.split(delimiter));
            int i = list.indexOf("->");
            if (i != list.lastIndexOf("->")) return null;
            left = list.subList(0, i);
            right = list.subList(i + 1, list.size());
        }
        if (left.size() == 0 || right.size() == 0) return null;
        return new FDSimpleRelation(left, right);
    }

    /**
     * creates a FDSimpleRelation from String
     *
     * @param rel the String to be parsed. "" as default delimiter
     * @return current updated object if rel has right syntax else null
     */
    public static FDSimpleRelation parse(String rel) {
        return FDSimpleRelation.parse(rel, "");
    }

    /**
     * the "left side" of ->
     *
     * @return FDKey
     */
    public FDKey getKey() {
        return this.key;
    }

    /**
     * the "right side" of ->
     *
     * @return HashSet<String> of attributes
     */
    public HashSet<String> getValues() {
        return this.values;
    }

    /**
     * @return HashSet<String> of each attribute either contained in key or values.
     */
    public HashSet<String> getAttributes() {
        HashSet<String> set = new HashSet<>();
        set.addAll(this.key);
        set.addAll(this.values);
        return set;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String attribute : this.key) sb.append(attribute).append(" ");
        sb.append("->");
        for (String attribute : this.values) sb.append(" ").append(attribute);
        return sb.toString();
    }
}

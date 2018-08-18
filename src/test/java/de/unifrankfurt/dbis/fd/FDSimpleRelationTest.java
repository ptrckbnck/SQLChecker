package de.unifrankfurt.dbis.fd;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;


public class FDSimpleRelationTest {

    /**
     * parses "a->b"
     */
    @Test
    public void parse() {
        FDSimpleRelation rel = FDSimpleRelation.parse("a->b");
        FDKey key = new FDKey("a");
        HashSet<String> values = new HashSet<>();
        values.add("b");
        HashSet<String> attributes  = new HashSet<>();
        attributes.addAll(key);
        attributes.addAll(values);
        assertEquals(attributes,rel.getAttributes());
        assertEquals(key,rel.getKey());
        assertEquals(values,rel.getValues());
    }


    /**
     * parses ""
     */
    @Test
    public void parse2() {
        FDSimpleRelation rel = FDSimpleRelation.parse("");
        FDKey key = new FDKey();
        HashSet<String> values = new HashSet<>();
        HashSet<String> attributes  = new HashSet<>();
        attributes.addAll(key);
        attributes.addAll(values);
        assertEquals(attributes,rel.getAttributes());
        assertEquals(key,rel.getKey());
        assertEquals(values,rel.getValues());
    }

    /**
     * parses "a b -> a b"
     */
    @Test
    public void parse3() {
        FDSimpleRelation rel = FDSimpleRelation.parse("a b -> a b"," ");
        FDKey key = new FDKey("a","b");
        HashSet<String> values = new HashSet<>();
        values.add("a");
        values.add("b");
        HashSet<String> attributes  = new HashSet<>();
        attributes.addAll(key);
        attributes.addAll(values);
        assertEquals(attributes,rel.getAttributes());
        assertEquals(key,rel.getKey());
        assertEquals(values,rel.getValues());
    }

    /**
     * parses "a"
     */
    @Test
    public void parse4() {
        FDKey key = new FDKey("a");
        HashSet<String> values = new HashSet<>();
        values.add("b");
        HashSet<String> attributes  = new HashSet<>();
        FDSimpleRelation rel = new FDSimpleRelation(key,values);
        attributes.addAll(key);
        attributes.addAll(values);
        assertEquals(attributes,rel.getAttributes());
        assertEquals(key,rel.getKey());
        assertEquals(values,rel.getValues());
    }

    /**
     * parses "ab->c"
     */
    @Test
    public void parse5() {
        FDKey key = new FDKey("a","b");
        HashSet<String> values = new HashSet<>();
        values.add("c");
        HashSet<String> attributes  = new HashSet<>();
        FDSimpleRelation rel = FDSimpleRelation.parse("ab->c");
        attributes.addAll(key);
        attributes.addAll(values);
        assertEquals(attributes,rel.getAttributes());
        assertEquals(key,rel.getKey());
        assertEquals(values,rel.getValues());
    }

    /**
     * parses "->"
     */
    @Test
    public void parseSyntaxFail1() {
        FDSimpleRelation relation = FDSimpleRelation.parse("->");
        assertEquals(null,relation);
    }

    /**
     * parses "a->"
      */
    @Test
    public void parseSyntaxFail2() {
        FDSimpleRelation relation = FDSimpleRelation.parse("a->");
        assertEquals(null,relation);
    }

    /**
     * parses "->b"
     */
    @Test
    public void parseSyntaxFail3() {
        FDSimpleRelation relation = FDSimpleRelation.parse("->b");
        assertEquals(null,relation);
    }

    /**
     * parses "a->b->c"
     */
    @Test
    public void parseSyntaxFail4() {
        FDSimpleRelation relation = FDSimpleRelation.parse("a->b->c");
        assertEquals(null,relation);
    }

    /**
     * parses "a"
     */
    @Test
    public void parseSyntaxFail5() {
        FDSimpleRelation relation = FDSimpleRelation.parse("a");
        assertEquals(null, relation);
    }

}
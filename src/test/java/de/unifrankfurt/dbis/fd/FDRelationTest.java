package de.unifrankfurt.dbis.fd;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

public class FDRelationTest {


    /**
     * checks simple creator
     */
    @Test
    public void newFDRelationTest(){
    FDRelation fdr = new FDRelation();
    HashSet<String> attributes = new HashSet<>();
    assertEquals(attributes,fdr.getAttributes());
    }

    /**
     * checks creator with key and value
     */
    @Test
    public void newFDRelationTest2(){
        HashSet<String> key = new HashSet<>();
        key.add("a");
        key.add("b");
        HashSet<String> values = new HashSet<>();
        values.add("c");
        values.add("d");
        FDRelation fdr = null;
        try {
            fdr = new FDRelation(key,values);
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        HashSet<String> attributes = new HashSet<>();
        attributes.addAll(key);
        attributes.addAll(values);
        assertEquals(attributes,fdr.getAttributes());
        assertEquals(values ,fdr.getDependenciesOf(new FDKey(key)));
    }

    /**
     * checks creator with FDSimpleRelation
     */
    @Test
    public void newFDRelationTest3(){
        HashSet<String> key = new HashSet<>();
        key.add("a");
        key.add("b");
        HashSet<String> values = new HashSet<>();
        values.add("c");
        values.add("d");
        FDRelation fdr = null;
        try {
            fdr = new FDRelation(key,values);
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        FDRelation fdr2 = null;
        try {
            fdr2 = new FDRelation(new FDSimpleRelation(key,values));
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        assertEquals(fdr,fdr2);
    }

    /**
     * checks if parsing of a->b works as expected
     */
    @Test
    public void parse() {
        FDRelation rel = null;
        try {
            rel = new FDRelation().parse("a->b");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        FDKey key = new FDKey("a");
        HashSet<String> values = new HashSet<>();
        values.add("b");
        HashSet<String> attributes  = new HashSet<>();
        attributes.addAll(key);
        attributes.addAll(values);
        FDKeySet set = new FDKeySet();
        set.add(key);
        assertEquals(attributes,rel.getAttributes());
        assertEquals(set,rel.getDependenciesTo("b"));
        assertEquals(values,rel.getDependenciesOf(key));
    }

    /**
     * checks if parsing of "" works as expected
     */
    @Test
    public void parse2() {
        FDRelation rel = null;
        try {
            rel = new FDRelation().parse("");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        assertEquals(new HashSet<String>(),rel.getAttributes());
    }

    /**
     * checks if parsing of a b -> a b works as expected
     */
    @Test
    public void parse3() {
        FDRelation rel = null;
        try {
            rel = new FDRelation().parse("a b -> a b"," ");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        FDKey key = new FDKey("a","b");
        HashSet<String> values = new HashSet<>();
        values.add("a");
        values.add("b");
        FDRelation rel2 = null;
        try {
            rel2 = new FDRelation(key,values);
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        assertEquals(rel2, rel);
    }

    /**
     * checks if parsing of a b c -> d with delimiter "o" works as expected
     */
    @Test
    public void parse4() {
        FDRelation rel = null;
        try {
            rel = new FDRelation().parse("aoboco->od","o");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        FDKey key = new FDKey("a","b","c");
        HashSet<String> values = new HashSet<>();
        values.add("d");
        FDRelation rel2 = null;
        try {
            rel2 = new FDRelation(key,values);
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        assertEquals(rel2, rel);

    }

    /**
     *
     * checks if parsing of "->" works as expected
     */
    @Test
    public void parseSyntaxFail1() {
        FDRelation relation = null;
        try {
            relation = new FDRelation().parse("->");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        assertEquals(null,relation);
    }

    /**
     * checks if parsing of "a->" works as expected
     */
    @Test
    public void parseSyntaxFail2() {
        FDRelation relation = null;
        try {
            relation = new FDRelation().parse("a->");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        assertEquals(null,relation);
    }

    /**
     * checks if parsing of "->b" works as expected
     */
    @Test
    public void parseSyntaxFail3() {
        FDSimpleRelation relation = FDSimpleRelation.parse("->b");
        assertEquals(null,relation);
    }


    /**
     * checks if parsing of "a->b->c" works as expected
     */
    @Test
    public void parseSyntaxFail4() {
        FDRelation relation = null;
        try {
            relation = new FDRelation().parse("a->b->c");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        assertEquals(null,relation);
    }


    /**
     * checks if parsing of "a" works as expected
     */
    @Test
    public void parseSyntaxFail5() {
        FDRelation relation = null;
        try {
            relation = new FDRelation().parse("a");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        assertEquals(null, relation);
    }

    /**
     * checks getAttributes works as expected
     */
    @Test
    public void getAttributesTest(){
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("a->b");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        HashSet<String> attributes = new HashSet<>();
        attributes.add("a");
        attributes.add("b");
        assertEquals(attributes,fdr.getAttributes());
        try {
            fdr = fdr.parse("b->c");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        attributes.add("c");
        assertEquals(attributes,fdr.getAttributes());
    }


    /**
     * checks if dependencies are correctly calculated (3relation)
     */
    @Test
    public void getDependenciesOfTest() {
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("a->b")
                    .parse("a->c")
                    .parse("ab->d");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }

        HashSet<String> set = new HashSet<>();
        set.add("b");
        set.add("c");
        assertEquals(set,fdr.getDependenciesOf(new FDKey("a")));
        set.add("d");
        assertEquals(set,fdr.getDependenciesOf(new FDKey("a","b")));
    }

    /**
     * dependency test with one relation
     */
    @Test
    public void getDependenciesOfTest1() {
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("a->b");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        HashSet<String> a = new HashSet<>();
        a.add("b");
        assertEquals(a,fdr.getDependenciesOf("a"));
        assertEquals(new HashSet<String>(),fdr.getDependenciesOf("b"));
    }

    /**
     * dependency test with two relation
     */
    @Test
    public void getDependenciesOfTest2() {
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("a->b")
                    .parse("a->c");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        HashSet<String> a = new HashSet<>();
        a.add("b");
        a.add("c");
        assertEquals(a,fdr.getDependenciesOf("a"));
        assertEquals(new HashSet<String>(),fdr.getDependenciesOf("b"));
        assertEquals(new HashSet<String>(),fdr.getDependenciesOf("c"));
    }

    /**
     * dependency test with reflexive relation
     */
    @Test
    public void getDependenciesOfTest3() {
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("a->a");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        HashSet<String> a = new HashSet<>();
        a.add("a");
        assertEquals(a,fdr.getDependenciesOf("a"));
    }


    /**
     * checks if concat parsing works
     */
    @Test
    public void parseConcat() {
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("a->b").parse("b->c");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        assertTrue(!fdr.getData().containsKey("a"));
        assertTrue(fdr.getData().containsKey("b"));
        assertTrue(fdr.getData().containsKey("c"));
        FDKeySet b = new FDKeySet();
        b.add(new FDKey("a"));
        FDKeySet c = new FDKeySet();
        c.add(new FDKey("b"));
        assertEquals(b,fdr.getData().get("b"));
        assertEquals(c,fdr.getData().get("c"));
    }

    /**
     * checks if the compact representation works as intended.
     */
    @Test
    public void compact() {
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("ab->bc","")
                    .parse("ab->e");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        fdr.compact();
        HashMap<HashSet<String>, HashSet<String>> map = new HashMap<>();
        HashSet<String> key = new HashSet<>();
        key.add("a");
        key.add("b");
        HashSet<String> values = new HashSet<>();
        values.add("b");
        values.add("c");
        values.add("e");
        map.put(key,values);
        assertEquals(map,fdr.compact());
    }

    /**
     * checks if transitive dependencies are found
     */
    @Test
    public void transFinderTest() {
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("a->b")
                    .parse("b->c")
                    .parse("ab->d");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        FDKeySet set = new FDKeySet();
        assertEquals(set,fdr.transFinder("a"));
        set.add(new FDKey("a"));
        assertEquals(set,fdr.transFinder("b"));
        assertEquals(set,fdr.transFinder("d"));
        set.add(new FDKey("b"));
        assertEquals(set,fdr.transFinder("c"));
    }

    /**
     * tests ff transitive close is calculated correctly
     */
    @Test
    public void closureTest() {
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("a->b")
                    .parse("b->c")
                    .parse("ab->d");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        FDRelation fdr2 = null;
        try {
            fdr2 = new FDRelation().parse("a->b")
                    .parse("a->c")
                    .parse("a->d")
                    .parse("b->c");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        fdr = fdr.transitiveClosure();
        assertEquals(fdr2,fdr);
    }

    /**
     * tests reflexive variant of the transitive closure
     */
    @Test
    public void closureReflexiveTest() {
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("a->b")
                    .parse("b->c")
                    .parse("ab->d");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        fdr = fdr.transitiveClosureReflexive();
        HashSet<String> a = new HashSet<>();
        a.add("a");
        a.add("b");
        a.add("c");
        a.add("d");
        HashSet<String> b = new HashSet<>();
        b.add("b");
        b.add("c");
        HashSet<String> c = new HashSet<>();
        c.add("c");
        HashSet<String> d = new HashSet<>();
        d.add("d");
        HashSet<String> bd = new HashSet<>();
        bd.add("b");
        bd.add("c");
        bd.add("d");
        assertEquals(a,fdr.getDependenciesOf(new FDKey("a")));
        assertEquals(b,fdr.getDependenciesOf(new FDKey("b")));
        assertEquals(c,fdr.getDependenciesOf(new FDKey("c")));
        assertEquals(d,fdr.getDependenciesOf(new FDKey("d")));
        assertEquals(a,fdr.getDependenciesOf(new FDKey("a","b")));
        assertEquals(bd,fdr.getDependenciesOf(new FDKey("b","d")));
    }

    /**
     * another closure test
     */
    @Test
    public void closureReflexiveTest2() {
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("a->b")
                    .parse("b->c");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        HashSet<String> a = new HashSet<>();
        a.add("a");
        a.add("b");
        a.add("c");
        fdr = fdr.transitiveClosureReflexive();
        assertEquals(a,fdr.getDependenciesOf("a"));
    }

    /**
     * checks if getDependenciesTo works as intended.
     * if a->c: a has a dependencies to c
     */
    @Test
    public void getDependenciesToTest() {
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("a->c")
                    .parse("b->c")
                    .parse("ab->d");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        FDKeySet set = new FDKeySet();
        set.add(new FDKey("a"));
        set.add(new FDKey("b"));
        assertEquals(set,fdr.getDependenciesTo("c"));
        set.clear();
        set.add(new FDKey("a","b"));
        assertEquals(set,fdr.getDependenciesTo("d"));
    }

    /**
     * checks if the map is correctly created
     */
    @Test
    public void getDictKeyToAttributeTest() {
        FDRelation fdr = null;
        try {
            fdr = new FDRelation().parse("a->b")
                    .parse("a->c")
                    .parse("ab->d");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        HashMap<FDKey,HashSet<String>> map = new HashMap<>();
        HashSet<String> set = new HashSet<>();
        set.add("b");
        set.add("c");
        map.put(new FDKey("a"),set);
        set = new HashSet<>();
        set.add("d");
        map.put(new FDKey("a","b"),set);
        assertEquals(map,fdr.getDictKeyToAttribute());
    }

}
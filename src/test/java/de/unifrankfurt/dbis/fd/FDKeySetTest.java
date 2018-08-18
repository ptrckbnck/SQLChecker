package de.unifrankfurt.dbis.fd;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class FDKeySetTest {


    /**
     * tests if size works correctly
     */
    @Test
    public void size() {
        FDKeySet set = new FDKeySet();
        assertTrue(set.size==0);
        set.add(new FDKey("a","b"));
        assertTrue(set.size()==1);
        set.remove(new FDKey("a","b"));
        assertTrue(set.size()==0);

    }

    /**
     * tests if isEmpty works correctly
     */
    @Test
    public void isEmpty() {
        FDKeySet set = new FDKeySet();
        assertTrue(set.size==0);
        assertTrue(set.isEmpty());
        set.add(new FDKey("a","b"));
        assertTrue(!set.isEmpty());
        set.remove(new FDKey("a","b"));
        assertTrue(set.isEmpty());
    }


    /**
     * checks of if NullPointerException is raised correctly if you try to add null
     */
    @Test(expected = NullPointerException.class)
    public void containsNull(){
        FDKeySet set = new FDKeySet();
        set.contains(null);
    }

    /**
     * checks if contains works correctly
     */
    @Test
    public void contains() {
        FDKeySet set = new FDKeySet();
        assertTrue("empty Key",!set.contains(new FDKey()));
        FDKey key = new FDKey("a", "b");
        assertTrue("pre add",!set.contains(key));
        set.add(key);
        assertTrue("after add",set.contains(key));
        set.remove(key);
        assertTrue("after remove",!set.contains(key));
    }

    /**
     * isRedundant should raise Exception if key is null
     */
    @Test(expected = NullPointerException.class)
    public void isRedundantNull(){
        FDKeySet set = new FDKeySet();
        set.isRedundant(null);
    }


    /**
     * checks if isRedundant works correctly
     */
    @Test
    public void isRedundant() {
        FDKeySet set = new FDKeySet();
        FDKey a = new FDKey("a");
        FDKey b = new FDKey("b");
        FDKey ab = new FDKey("a","b");
        FDKey cde = new FDKey("c","d","e");
        assertTrue(set.isRedundant(new FDKey()));
        assertTrue(!set.isRedundant(a));
        assertTrue(!set.isRedundant(ab));
        assertTrue(!set.isRedundant(b));
        assertTrue(!set.isRedundant(cde));
        set.add(a);
        assertTrue(set.isRedundant(a));
        assertTrue(set.isRedundant(ab));
        assertTrue(!set.isRedundant(b));
        assertTrue(!set.isRedundant(cde));
        set.add(b);
        assertTrue(set.isRedundant(a));
        assertTrue(set.isRedundant(ab));
        assertTrue(set.isRedundant(b));
        assertTrue(!set.isRedundant(cde));
        set.add(ab);
        assertTrue(set.isRedundant(a));
        assertTrue(set.isRedundant(ab));
        assertTrue(set.isRedundant(b));
        assertTrue(!set.isRedundant(cde));
    }

    /**
     * checks if created Iterator works as expected
     */
    @Test
    public void iterator() {
        FDKeySet set = new FDKeySet();
        Iterator<FDKey> iter = set.iterator();
        assertTrue(!iter.hasNext());

        FDKey a = new FDKey("a");
        set.add(a);
        FDKey b = new FDKey("b");
        set.add(b);
        FDKey cd = new FDKey("c","d");
        set.add(cd);
        FDKey efg = new FDKey("e","f","g");
        set.add(efg);
        iter = set.iterator();
        assertTrue(iter.hasNext());
        FDKey next = iter.next();
        assertTrue((next.equals(a))||(next.equals(b)));
        next = iter.next();
        assertTrue((next.equals(a))||(next.equals(b)));
        next = iter.next();
        assertEquals(next, cd);
        next = iter.next();
        assertEquals(next, efg);
        assertTrue(!iter.hasNext());
    }

    /**
     * checks if toArray works correctly
     */
    @Test
    public void toArray() {
        FDKeySet set = new FDKeySet();
        FDKey a = new FDKey("a");
        set.add(a);
        FDKey b = new FDKey("b");
        set.add(b);
        FDKey cd = new FDKey("c","d");
        set.add(cd);
        FDKey efg = new FDKey("e","f","g");
        set.add(efg);
        HashSet<FDKey> hashset2 = new HashSet<>();
        HashSet<Object> hashset = new HashSet<>(Arrays.asList(set.toArray()));
        hashset2.add(a);
        hashset2.add(b);
        hashset2.add(cd);
        hashset2.add(efg);
        assertEquals(hashset, hashset2);
    }

    /**
     * add(null) should raise exception
     */
    @Test(expected = NullPointerException.class)
    public void add_null() {
        FDKeySet set = new FDKeySet();
        set.add(null);
    }

    /**
     * checks if simple adds with no redundancy works correctly
     */
    @Test
    public void add() {
        FDKeySet set = new FDKeySet();
        assertTrue(!set.add(new FDKey()));
        //should be empty
        assertEquals(0, set.size);
        FDKey a = new FDKey("a");
        //increase by one
        assertTrue(set.add(a));
        assertEquals(1, set.size);
        assertTrue(!set.add(a));
        //no increase
        assertEquals(1, set.size);
        FDKey b = new FDKey("b");
        assertTrue(set.add(b));
        assertEquals(2, set.size);
        assertTrue(!set.add(b));
        assertEquals(2, set.size);
        FDKey cd = new FDKey("c","d");
        assertTrue(set.add(cd));
        assertEquals(3, set.size);
        FDKey ab = new FDKey("a","b");
        assertTrue(!set.add(ab));
        assertEquals(3, set.size);
        FDKey abc = new FDKey("a","b","c");
        assertTrue(!set.add(abc));
        assertEquals(3, set.size);
    }

    /**
     * checks if add with redundancy works correctly
     */
    @Test
    public void add2() {
        FDKeySet set = new FDKeySet();
        assertTrue(!set.add(new FDKey()));
        assertTrue(set.size==0);
        FDKey a = new FDKey("a");
        FDKey ac = new FDKey("a","c");
        FDKey ab = new FDKey("a","b");
        assertTrue(set.add(ab));
        assertEquals(1,set.size);
        assertTrue(set.add(ac));
        assertEquals(2,set.size);
        assertTrue(set.add(a));
        //set size decreases because ac and ab are redundant
        assertEquals(1,set.size);
    }

    /**
     * checks if remove works correctly
     */
    @Test
    public void remove() {
        FDKeySet set = new FDKeySet();
        FDKey a = new FDKey("a");
        set.add(a);
        FDKey b = new FDKey("b");
        set.add(b);
        FDKey cd = new FDKey("c","d");
        set.add(cd);
        assertTrue(set.size()==3);
        assertTrue(!set.remove(new FDKey("c")));
        assertTrue(set.size()==3);
        assertTrue(!set.remove(new FDKey("a","b")));
        assertTrue(set.size()==3);
        assertTrue(set.remove(a));
        assertTrue(set.size()==2);
        assertTrue(set.remove(b));
        assertTrue(set.size()==1);
        assertTrue(set.remove(cd));
        assertTrue(set.size()==0);
        assertTrue(!set.remove(new FDKey()));
        assertTrue(set.size()==0);
    }

    /**
     * remove(null) should raise exception
     */
    @Test(expected = NullPointerException.class)
    public void remove_null() {
        FDKeySet set = new FDKeySet();
        set.remove(null);
    }

    /**
     * checks addAll
     */
    @Test
    public void addAll() {
        FDKeySet set = new FDKeySet();
        HashSet<FDKey> hashset = new HashSet<>();
        hashset.add(new FDKey("a"));
        hashset.add(new FDKey("b"));
        hashset.add(new FDKey("c","d"));
        assertTrue(set.addAll(hashset));
        assertEquals(3,set.size);
    }


    /**
     * checks if everything will be removed
     */
    @Test
    public void clear() {
        FDKeySet set = new FDKeySet();
        FDKey a = new FDKey("a");
        set.add(a);
        set.add(new FDKey("b"));
        set.add(new FDKey("c","d"));
        set.clear();
        assertEquals(0,set.size());
        assertFalse(set.contains(a));

    }

    /**
     *  checks if removeAll works correctly
     */
    @Test
    public void removeAll() {
        FDKeySet set = new FDKeySet();
        FDKey a = new FDKey("a");
        set.add(a);
        FDKey b = new FDKey("b");
        set.add(b);
        FDKey cd = new FDKey("c", "d");
        set.add(cd);
        HashSet<FDKey> hashset = new HashSet<>();
        hashset.add(a);
        hashset.add(b);
        assertTrue(set.removeAll(hashset));
        assertFalse(set.contains(a));
        assertFalse(set.contains(b));
        assertTrue(set.contains(cd));

    }

    /**
     * checks retainAll
     */
    @Test
    public void retainAll() {
        FDKeySet set = new FDKeySet();
        FDKey a = new FDKey("a");
        FDKey b = new FDKey("b");
        FDKey cd = new FDKey("c", "d");
        FDKey efg = new FDKey("e","f","g");
        set.add(cd);
        set.add(a);
        set.add(efg);
        HashSet<FDKey> hashset = new HashSet<>();
        hashset.add(a);
        hashset.add(b);
        hashset.add(efg);
        assertTrue(set.retainAll(hashset));
        assertTrue(set.contains(a));
        assertFalse(set.contains(b));
        assertFalse(set.contains(cd));
        assertTrue(set.contains(efg));
    }

    /**
     * checks containsAll
     */
    @Test
    public void containsAll() {
        FDKeySet set = new FDKeySet();
        FDKey a = new FDKey("a");
        FDKey b = new FDKey("b");
        FDKey cd = new FDKey("c", "d");
        FDKey efg = new FDKey("e","f","g");
        set.add(a);
        set.add(b);
        set.add(cd);
        HashSet<FDKey> hashset = new HashSet<>();
        hashset.add(a);
        hashset.add(cd);
        assertTrue(set.containsAll(hashset));
        hashset.add(efg);
        assertFalse(set.containsAll(hashset));
    }

    /**
     * checks if internal list size is reduced if highest set is empty.
     */
    @Test
    public void shrinkTest(){
        FDKeySet keySet = new FDKeySet();
        FDKey a = new FDKey("a");
        keySet.add(a);
        FDKey cd = new FDKey("c", "d");
        keySet.add(cd);
        keySet.remove(a);
        assertEquals(2,keySet.data.size());
        keySet.remove(cd);
        assertEquals(0,keySet.data.size());
    }

}
package de.unifrankfurt.dbis.fd;


import java.util.*;
import java.util.function.Predicate;

/**
 * FDKey extends HashSet<String>. It is basically just that.
 * It is easier to work with FDKey instead of HashSet<String>.
 * Has the ability to create a Set with all true subsets with size>0 of itself.
 * FDKey is immutable
 *
 * @author Patrick Bonack
 * @version 1.0
 * @since 18.11.2017
 */
public class FDKey implements Set<String> {
    private HashSet<String> set;

    /**
     * attributes should be unique
     *
     * @param attributes collection of String attributes
     */
    public FDKey(Collection<String> attributes) {
        set = new HashSet<>(attributes);
        if (set.contains(null)) throw new NullPointerException();
    }

    /**
     * attributes should be unique
     *
     * @param attributes String[]
     */
    public FDKey(String... attributes) {
        this.set = new HashSet<>(Arrays.asList(attributes));
        if (this.contains(null)) throw new NullPointerException();
    }

    public FDKey() {
        this.set = new HashSet<>();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.set);
    }

    @Override
    public boolean equals(Object o) {
        return Objects.equals(this.set, o);
    }

    @Override
    public int size() {
        return this.set.size();
    }

    @Override
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.set.contains(o);
    }

    @Override
    public Iterator<String> iterator() {
        return this.set.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return this.set.toArray(ts);
    }

    @Override
    public boolean add(String s) {
        //throw new UnsupportedOperationException();
        return this.set.add(s);//needed for gson.load()
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.set.containsAll(collection);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends String> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(Predicate<? super String> predicate) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return null if this is empty.
     */
    public HashSet<FDKey> powerSetWoSelfAndEmptySet() {
        if (this.size() == 0) return null;
        HashSet<FDKey> set = new HashSet<>();
        if (this.size() == 1) return set;
        set.add(new FDKey());
        for (int i = 1; i < this.size(); i++) {
            HashSet<FDKey> newSet = new HashSet<>();
            for (String s : this) {
                for (FDKey key : set) {
                    Collection<String> col = new HashSet<>(key);
                    col.add(s);
                    FDKey newKey = new FDKey(col);
                    newSet.add(newKey);
                }
            }
            set = newSet;
        }
        return set;
    }

    /**
     * @return a string representation of this collection.
     */
    @Override
    public String toString() {
        return Arrays.asList(this.toArray()).toString();
    }

    /**
     * exception if empty FDKey are not excepted
     */
    static class EmptyException extends Exception {

    }
}

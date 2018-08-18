package de.unifrankfurt.dbis.fd;


import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.iterators.IteratorChain;

import java.util.*;

/**
 * A special Set<FDKey>.
 * It is guaranteed, that if this contains FDKey key,
 * this does not also contain any superset of key.
 * If key is added to this, any superset of key will be removed.
 * If this contains key, trying to add a superset of key will fail.
 * Does not allow null or empty FDKey.
 *
 * @author Patrick Bonack
 * @version 1.0
 * @since 18.11.2017
 */
public class FDKeySet implements Set<FDKey> {

    /**
     * size the cardinality of this
     * data ArrayList with HashSets of FDKeys. The set at index i contains only FDKey with size i.
     */
    protected ArrayList<HashSet<FDKey>> data;

    /**
     * count of all contained FDKey
     */
    protected int size;

    public FDKeySet() {
        this.data = new ArrayList<>();
        this.size = 0;
    }

    /**
     * @return cardinality of this
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * @return true if this contains no elements
     */
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * @return True if this contains o
     * @throws NullPointerException if o == null
     */
    @Override
    public boolean contains(Object o) {
        if (o == null) throw new NullPointerException();
        FDKey key = (FDKey) o;
        int s = key.size();
        if (s == 0) return false;
        if (s > this.data.size()) return false;
        HashSet<FDKey> lookupSet = data.get(s - 1);
        return lookupSet.contains(key);
    }

    /**
     * @return returns the max size of FDKey that can be stored without resizing data.
     */
    public int maxInitSet() {
        return this.data.size();
    }

    /**
     * Looks if any set at data[i], i < |key|, contains subset of key.
     *
     * @param key look up
     * @return true if this contains any subset of key or key is empty.
     * @throws NullPointerException if key == null
     */
    public boolean isRedundant(FDKey key) {
        if (key == null) throw new NullPointerException();
        int s = key.size();
        if (s == 0) return true;
        HashSet<FDKey> lookupSet;
        for (int i = 0; i < s && i < this.maxInitSet(); i = i + 1) {
            lookupSet = this.data.get(i);
            for (FDKey lookupKey : lookupSet) {
                if (key.containsAll(lookupKey)) return true;
            }
        }
        return false;
    }

    /**
     * Iterator of this.
     * Is is guaranteed that, FDkey key is yielded only if there is no other key with smaller size.
     *
     * @return Iterator<FDKey>
     */
    @Override
    public Iterator<FDKey> iterator() {
        return new FDKeySetIterator();
    }

    /**
     * Custom iterator for this.
     * Is is guaranteed that, this yields FDkey key only if there is no other key with smaller s
     */
    class FDKeySetIterator implements Iterator<FDKey> {

        /**
         * IteratorChain of HashSet iterators
         */
        IteratorChain<FDKey> iter;

        private FDKeySetIterator() {
            iter = new IteratorChain<>();
            for (HashSet<FDKey> set : data){
                iter.addIterator(set.iterator());
            }
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public FDKey next() {
            return iter.next();
        }
    }

    /**
     * @return an array containing all of the elements in this collection
     */
    @Override
    public Object[] toArray() {
        return IteratorUtils.toArray(this.iterator());
    }


    /**
     * Directly adds key to this. Does <b>not</b> check for conflicts.
     *
     * @param key to be added
     */
    private void addUnsafe(FDKey key) {
        int i = key.size() - 1;
        HashSet<FDKey> set = this.data.get(i);
        set.add(key);
        this.data.set(i, set);
        this.size += 1;

    }

    /**
     * Tries to add key to this.
     * Key is not added if:
     * this contains any subset of key
     * key == null
     * key is empty.
     * If key is added, any superset of key will be removed.
     * That means, the size of this can be reduced as result of add.
     *
     * @param key to be added
     * @return true if this is changed as result of add.
     * @throws NullPointerException if key == null
     */
    @Override
    public boolean add(FDKey key) {
        if (key == null) throw new NullPointerException();
        if (key.size() == 0) return false;
        if (this.isRedundant(key)) return false;
        int s = key.size();
        // find and remove obsolete elements.
        HashSet<FDKey> toRemove = new HashSet<>();
        for (FDKey lookUpKey : this) {
            if (lookUpKey.containsAll(key)) {
                toRemove.add(lookUpKey);
            }
        }
        this.removeAll(toRemove);
        //initialize new HashSets if needed
        while (s > this.data.size()) {
            this.data.add(new HashSet<>());
        }
        //finally add key
        this.addUnsafe(key);
        return true;
    }

    /**
     * Removes o from this
     *
     * @param o
     * @return true if this changed as result of remove
     * @throws NullPointerException if o == null
     */
    @Override
    public boolean remove(Object o) {
        if (!this.contains(o)) return false;
        FDKey key = (FDKey) o;
        int s = key.size();
        HashSet<FDKey> set = this.data.get(s - 1);
        set.remove(key);
        this.data.set(s - 1, set);
        this.size -= 1;
        this.shrinkData();
        return true;
    }

    /**
     * removes empty sets at end of this.data.
     */
    private void shrinkData() {
        if (this.data.isEmpty()) return;
        if (!this.data.get(this.data.size() - 1).isEmpty()) return;
        for (int i = this.maxInitSet(); i > 0 && this.data.get(i - 1).isEmpty(); i--)
            this.data.remove(i - 1);
    }

    /**
     * Tries to add all of the elements in the specified collection to this.
     *
     * @param collection of FDKey
     * @return true if this is changed as result of addAll
     */
    @Override
    public boolean addAll(Collection collection) {
        if (collection == null) throw new NullPointerException();
        boolean changed = false;
        for (Object o : collection) {
            changed = changed | this.add((FDKey) o);
        }
        return changed;
    }

    /**
     * Removes every element of this.
     */
    @Override
    public void clear() {
        this.data = new ArrayList<>();
        this.size = 0;
    }


    /**
     * Removes all of the elements in the specified collection from this.
     *
     * @param collection of FDKey
     * @return true if this is changed as result of removeAll
     */
    @Override
    public boolean removeAll(Collection collection) {
        if (collection == null) throw new NullPointerException();
        boolean changed = false;
        for (Object o : collection) {
            changed = changed | this.remove(o);
        }
        return changed;
    }


    /**
     * Retains only the elements in this collection that are contained in the specified collection
     *
     * @param collection of FDKey
     * @return true if this is changed as result of removeAll
     */
    @Override
    public boolean retainAll(Collection collection) {
        if (collection == null) throw new NullPointerException();
        HashSet<FDKey> newData = new HashSet<>();
        for (Object o : collection) {
            if (this.contains(o)) newData.add((FDKey) o);
        }
        boolean changed = newData.size() != this.size;
        if (!changed) return false;
        this.clear();
        this.addAll(newData);
        return true;
    }

    /**
     * Returns true if this collection contains all of the elements in the specified collection.
     *
     * @param collection of FDKey
     */
    @Override
    public boolean containsAll(Collection collection) {
        if (collection == null) throw new NullPointerException();
        for (Object o : collection) {
            if (!this.contains(o)) return false;
        }
        return true;
    }

    /**
     * Returns an array containing all of the elements in this collection.
     *
     * @param a should be FDKey[]
     * @return FDKey[]
     */

    public FDKey[] toArray(FDKey[] a) {
        return IteratorUtils.toArray(this.iterator(), FDKey.class);
    }


    /**
     * dummy to satisfy Interface
     * @param a
     * @param <T>
     * @return
     */
    public <T> T[] toArray(T[] a) {
        return a;
    }
    /**
     * Compares the specified object with this for euquality
     *
     * @param o other
     * @return true if this equals o
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof FDKeySet)) {
            return false;
        }
        FDKeySet keySet = (FDKeySet) o;
        return this.size == keySet.size &&
                Objects.equals(this.data, keySet.data);
    }

    /**
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.data, this.size);
    }

    /**
     * @return a string representation of this collection.
     */
    @Override
    public String toString() {
        return Arrays.asList(this.toArray()).toString();
    }
}

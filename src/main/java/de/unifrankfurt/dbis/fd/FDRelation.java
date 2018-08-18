package de.unifrankfurt.dbis.fd;

import java.util.*;

/**
 * An minimized effective storage for a multiline relation.
 * Can create the transitive closure if itself.
 *
 * @author Patrick Bonack
 * @version 1.0
 * @since 18.11.2017
 */
public class FDRelation {

    private final HashSet<String> forcedAttributes;
    /**
     * data stores for each attribute every (minimal) FDKey that a relation maps to it.
     */
    private HashMap<String, FDKeySet> data;
    /**
     * attributes stores every attribute that occurs in the relation.
     */
    private HashSet<String> attributes;

    FDRelation() {
        this.attributes = new HashSet<>();
        this.data = new HashMap<>();
        this.forcedAttributes = null;
    }

    /**
     * @param attributes fixed relation schema
     */
    FDRelation(Collection<String> attributes) {
        if (attributes == null) {
            this.attributes = new HashSet<>();
            this.data = new HashMap<>();
            this.forcedAttributes = null;
        } else {
            this.data = new HashMap<>();
            this.forcedAttributes = new HashSet<>(attributes);
            this.attributes = new HashSet<>(attributes);
        }

    }

    FDRelation(FDSimpleRelation sRel) throws FDKey.EmptyException, UnexpectedAttributeException {
        this();
        this.add(sRel);
    }


    FDRelation(Collection<String> key, Collection<String> values) throws FDKey.EmptyException, UnexpectedAttributeException {
        this();
        this.dataUpdate(key, values);
    }

    public HashSet<String> getForcedAttributes() {
        return forcedAttributes;
    }

    public HashMap<String, FDKeySet> getData() {
        return data;
    }

    public FDRelation parse(String rel, String delimiter) throws FDKey.EmptyException, UnexpectedAttributeException {
        FDSimpleRelation sRel = FDSimpleRelation.parse(rel, delimiter);
        if (sRel == null) return null;
        this.dataUpdate(sRel.key, sRel.values);
        return this;
    }

    /**
     * creates a FDRelation from String
     *
     * @param rel the String to be parsed. "" as default delimiter
     * @return current updated object if rel has right syntax else null
     */
    public FDRelation parse(String rel) throws FDKey.EmptyException, UnexpectedAttributeException {
        return this.parse(rel, "");
    }

    /**
     * adds dependencies from sRel to this
     *
     * @param sRel FDSimpleRelation
     * @return this
     */
    public FDRelation add(FDSimpleRelation sRel) throws FDKey.EmptyException, UnexpectedAttributeException {
        this.dataUpdate(sRel.key, sRel.values);
        return this;
    }

    /**
     * adds every dependency of rel to this
     *
     * @param rel FDRelation
     * @return this
     */
    public FDRelation add(FDRelation rel) throws FDKey.EmptyException, UnexpectedAttributeException {
        for (HashMap.Entry<String, FDKeySet> entry : rel.data.entrySet()) {
            HashSet<String> set = new HashSet<>();
            set.add(entry.getKey());
            for (FDKey key : entry.getValue()) {
                this.dataUpdate(key, set);
            }
        }
        return this;
    }


    /**
     * adds every dependency of every relation in collection to this
     *
     * @param collection FDSimpleRelation collection
     * @return this
     */
    public FDRelation addAll(Collection<FDSimpleRelation> collection) throws FDKey.EmptyException, UnexpectedAttributeException {
        if (collection == null) throw new NullPointerException();
        for (FDSimpleRelation simpleRelation : collection) {
            this.add(simpleRelation);
        }
        return this;
    }


    /**
     * main update function.
     * let key -> values be the relation.
     * For every attribute in value, it adds key to a
     * FDKeySet which stores every FDKey that is dependant to the attribute.
     *
     * @param key FDKey
     */
    private void dataUpdate(Collection<String> key, Collection<String> values) throws FDKey.EmptyException, UnexpectedAttributeException {
        for (String s : values) {
            FDKeySet keySet;
            if (this.data.containsKey(s)) {
                keySet = this.data.get(s);
            } else {
                keySet = new FDKeySet();
            }
            if (key.isEmpty()) throw new FDKey.EmptyException();
            keySet.add(new FDKey(key));
            this.data.put(s, keySet);
        }

        // test for unexpected attribute

        if (this.forcedAttributes != null) {
            HashSet<String> col = new HashSet<>(key);
            col.addAll(values);
            if (!this.forcedAttributes.containsAll(col)) {
                HashSet<String> conflict = new HashSet<>(col);
                conflict.removeAll(this.forcedAttributes);
                throw new UnexpectedAttributeException(new ArrayList<>(conflict).toString());
            }
        }
        // add new attributes
        this.attributes.addAll(key);
        this.attributes.addAll(values);
    }

    class UnexpectedAttributeException extends Exception {

        UnexpectedAttributeException(String s) {
            super(s);
        }
    }

    /**
     * for a mapping attribute to FDKey it creates a line: FDKey -> attribute
     *
     * @return a string representation of this collection.
     */
    public String toStringSimple() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, FDKeySet> entry : this.data.entrySet()) {
            for (FDKey key : entry.getValue()) {
                sb = sb.append(key.toString())
                        .append(" -> ")
                        .append(entry.getKey())
                        .append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * the same as toStringSimple but if the same FDKey gets mapped by to two or more attributes
     * it creates the line:
     * FDKey -> attribute1 attribute2 .. attributeN
     *
     * @return a compact string representation of this collection.
     */
    @Override
    public String toString() {
        HashMap<Set<String>, Set<String>> map = this.compact();
        StringBuilder sb = new StringBuilder();
        for (HashMap.Entry<Set<String>, Set<String>> entry : map.entrySet()) {
            sb = sb.append(entry.getKey().toString())
                    .append(" -> ")
                    .append(entry.getValue().toString())
                    .append("\n");
        }
        return sb.toString();
    }


    /**
     * if data has mapping a -> bc and b -> bc.
     * then this hashmap has bc -> ab.
     *
     * @return HashMap that maps set of attributes to set of attributes.
     */
    protected HashMap<Set<String>, Set<String>> compact() {
        HashMap<Set<String>, Set<String>> map = new HashMap<>();
        for (Map.Entry<String, FDKeySet> entry : this.data.entrySet()) {
            for (Set<String> set : entry.getValue()) {
                Set<String> val;
                if (map.containsKey(set)) {
                    val = map.get(set);
                } else {
                    val = new HashSet<>();
                }
                val.add(entry.getKey());
                map.put(new HashSet<>(set), val);
            }
        }
        return map;
    }

    /**
     * @return HashSet<String> with every attribute
     */
    public HashSet<String> getAttributes() {
        return new HashSet<>(this.attributes);
    }

    /**
     * @param key FDKey to lookup
     * @return Hash<String> with each attribute where dependency key -> attribute exists.
     */
    public HashSet<String> getDependenciesOf(FDKey key) {
        HashSet<String> set = new HashSet<>();
        for (String attribute : this.data.keySet()) {
            FDKeySet keySet = this.data.get(attribute);
            for (FDKey lookUpKey : keySet) {
                if (key.containsAll(lookUpKey)) set.add(attribute);
            }
        }
        return set;
    }

    /**
     * @param key FDKey(key) to lookup
     * @return Hash<String> with each attribute where dependency key -> attribute exists.
     */
    public HashSet<String> getDependenciesOf(String... key) {
        return this.getDependenciesOf(new FDKey(key));
    }

    /**
     * @param attribute to look up
     * @return FDKeySet which each FDKey has dependency to attribute.
     */
    public FDKeySet getDependenciesTo(String attribute) {
        if (!this.attributes.contains(attribute)) return null;
        if (!this.data.containsKey(attribute)) return new FDKeySet();
        FDKeySet newSet = new FDKeySet();
        newSet.addAll(this.data.get(attribute));
        return newSet;
    }


    /**
     * adds every transitive dependency from this to new FDRelation.
     *
     * @return FDRelation
     */
    public FDRelation transitiveClosure() {
        return this.transitiveClosure(this);
    }

    /**
     * adds every transitive dependency from fDR to new FDRelation.
     *
     * @return FDRelation
     */
    private FDRelation transitiveClosure(FDRelation fDR) {
        FDRelation newFDR = new FDRelation(fDR.forcedAttributes);
        newFDR.attributes = new HashSet<>(fDR.attributes);
        HashMap<String, FDKeySet> newMap = new HashMap<>();
        for (String a : newFDR.attributes) {
            FDKeySet f = fDR.transFinder(a);
            if (f.isEmpty()) continue;
            newMap.put(a, fDR.transFinder(a));
        }
        newFDR.data = newMap;
        return newFDR;
    }

    /**
     * adds every transitive and reflexive dependency from this to new FDRelation.
     *
     * @return FDRelation
     */
    public FDRelation transitiveClosureReflexive() {
        FDRelation newFDR = new FDRelation(this.forcedAttributes);
        newFDR.attributes = new HashSet<>(this.attributes);
        for (String a : this.attributes) {
            FDKeySet set = new FDKeySet();
            if (this.data.containsKey(a)) {
                set.addAll(this.data.get(a));
            }
            set.add(new FDKey(a));
            newFDR.data.put(a, set);
        }
        return this.transitiveClosure(newFDR); //transitive
    }

    /**
     * @param attribute to lookup
     * @return FDKeySet with FDKeys that have transitive dependency to attribute
     */
    public FDKeySet transFinder(String attribute) {
        FDKeySet set;
        if (!this.data.containsKey(attribute)) {
            set = new FDKeySet();
        } else {
            set = this.data.get(attribute);
        }
        return transFinder(set);
    }

    /**
     * search for FDKeys that have a dependency to any attribute in keySet.
     * If keys are found that are not in keySet, transFinder recursively searches for new dependencies with the newly found keys.
     *
     * @param keySet to extend transitive
     * @return FDKeySet with FDKeys that have transitive dependency to an FDKey in keySet
     */
    private FDKeySet transFinder(FDKeySet keySet) {
        FDKeySet alternatives = new FDKeySet();
        alternatives.addAll(keySet);
        boolean foundSomething = false;
        for (Collection<String> attSet : keySet) {
            for (String att : attSet) {
                if (this.data.containsKey(att)) {
                    for (Collection<String> possibles : this.data.get(att)) {
                        Collection<String> altSet = new HashSet<>(attSet);
                        altSet.remove(att);
                        altSet.addAll(possibles);
                        boolean added = alternatives.add(new FDKey(altSet));
                        foundSomething = foundSomething || added;
                    }
                }
            }
        }
        if (foundSomething) return transFinder(alternatives);
        else return alternatives;
    }

    /**
     * reverses the intern representation mapping
     *
     * @return Mapping with FDKey to Set of attributes
     */
    public HashMap<FDKey, HashSet<String>> getDictKeyToAttribute() {
        HashMap<FDKey, HashSet<String>> newMap = new HashMap<>();
        for (HashMap.Entry<String, FDKeySet> entry : this.data.entrySet()) {
            String attribute = entry.getKey();
            FDKeySet keySet = entry.getValue();
            for (FDKey key : keySet) {
                HashSet<String> newSet;
                if (newMap.containsKey(key)) {
                    newSet = newMap.get(key);
                } else {
                    newSet = new HashSet<>();
                }
                newSet.add(attribute);
                newMap.put(key, newSet);
            }
        }
        return newMap;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof FDRelation)) {
            return false;
        }
        FDRelation keySet = (FDRelation) o;
        return Objects.equals(this.attributes, keySet.attributes) &&
                Objects.equals(this.data, keySet.data);
    }

    /**
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.attributes, this.data);
    }
}


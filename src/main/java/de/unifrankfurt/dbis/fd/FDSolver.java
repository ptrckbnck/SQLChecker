package de.unifrankfurt.dbis.fd;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * FDSolver determines many normal Form related information for given relationContainer.
 *
 * @author Patrick Bonack
 * @version 1.0
 * @since 18.11.2017
 */
public class FDSolver {

    /**
     * base relation
     */
    private FDRelation relation;

    /**
     * any prim attribute
     */
    private HashSet<String> prim;

    /**
     * any non-prim attribute
     */
    private HashSet<String> notPrim;

    /**
     * the maximum normal form the relation is in.
     */
    private int NF;

    /**
     * any candidate key
     */
    private FDKeySet keyCandidates;

    FDSolver(FDRelation relationContainer) {
        this.relation = relationContainer.transitiveClosureReflexive();
        this.NF = 1;
        this.keyCandidates = this.keyCandidates();
        this.prim = this.prim();
        this.notPrim = this.notPrim();
        this.NF = this.NF();
    }


    public FDRelation getRelation() {
        return relation;
    }

    public HashSet<String> getPrim() {
        return prim;
    }

    public HashSet<String> getNotPrim() {
        return notPrim;
    }

    public int getNF() {
        return NF;
    }

    public FDKeySet getKeyCandidates() {
        return keyCandidates;
    }

    private HashSet<String> getAttributes() {
        return this.relation.getAttributes();
    }

    /**
     * @return HashSet of any prim attribute
     */
    private HashSet<String> prim() {
        HashSet<String> _prim = new HashSet<>();
        for (FDKey key : this.keyCandidates) {
            _prim.addAll(key);
        }
        return _prim;
    }

    /**
     * @return HashSet of any non-prim attribute
     */
    private HashSet<String> notPrim() {
        HashSet<String> _notPrim = new HashSet<>(this.getAttributes());
        _notPrim.removeAll(this.prim);
        return _notPrim;
    }

    /**
     * @return max normal Form (no more than 3)
     */
    private int NF() {
        int nf = 1;
        if (this.is2NF()) nf = 2;
        if (nf == 2 && this.is3NF()) nf = 3;
        return nf;
    }

    /**
     * looks if any non-prime attribute is dependent on any proper subset of any candidate key
     *
     * @return true if in second normal Form
     */
    private boolean is2NF() {
        for (String att : this.notPrim) {
            for (FDKey key : this.keyCandidates) {
                for (FDKey subKey : key.powerSetWoSelfAndEmptySet()) {
                    FDKeySet keySet = this.relation.getData().get(att);
                    if (keySet.contains(subKey)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * looks if all the attributes are dependent only on the candidate keys
     * and not by any non-prime attributes.
     *
     * @return true if in third normal form
     */
    private boolean is3NF() {
        for (HashMap.Entry<String, FDKeySet> entry : relation.getData().entrySet()) {
            FDKeySet keySet = entry.getValue();
            String s = entry.getKey();
            if (this.prim.contains(s)) continue;
            //looks if any key which the attribute is dependent on is not candidate key.
            //that is okay because FDKeySet cannot have supersets of candidate keys.
            if (!this.keyCandidates.containsAll(keySet)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public String toString() {
        return "FDSolver{" +
                "relation=" + relation +
                ", attributes=" + Arrays.asList(getAttributes().toArray()).toString() +
                ", prim=" + Arrays.asList(prim.toArray()).toString() +
                ", notPrim=" + Arrays.asList(notPrim.toArray()).toString() +
                ", NF=" + NF +
                ", keyCandidates=" + keyCandidates +
                '}';
    }

    public String report() {
        String ls = System.lineSeparator();
        return "Report on Relation:" + ls +
                relation + ls +
                "attributes: " + Arrays.asList(getAttributes().toArray()).toString() + ls +
                "prim attributes: " + Arrays.asList(prim.toArray()).toString() + ls +
                "non-prim attributes: " + Arrays.asList(notPrim.toArray()).toString() + ls +
                "key-candidates: " + keyCandidates + ls +
                "Highest normal form: " + NF;
    }

    /**
     * creates the product of every FDKeySet an attribute depends on
     *
     * @return FDKeySet of every candidate key
     */
    private FDKeySet keyCandidates() {
        FDKeySet keyCandidates = new FDKeySet();
        for (HashMap.Entry<String, FDKeySet> entry : relation.getData().entrySet()) {
            FDKeySet keySet = entry.getValue();
            FDKeySet mergeSet = new FDKeySet();
            if (keyCandidates.isEmpty()) {
                mergeSet.addAll(keySet);
            } else {
                for (FDKey key : keyCandidates) {
                    for (FDKey lookUpKey : keySet) {
                        Collection<String> col = new HashSet<>(key);
                        col.addAll(lookUpKey);
                        mergeSet.add(new FDKey(col));
                    }
                }
            }
            keyCandidates = mergeSet;
        }
        return keyCandidates;
    }

}

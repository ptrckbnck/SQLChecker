package de.unifrankfurt.dbis.fd;

import org.junit.Test;


import static org.junit.Assert.*;


public class FDSolverTest {

    /**
     * tests if keyCandidates are correctly calculated
     */
    @Test
    public void keyCandidatesTest(){
        FDRelation container = null;
        try {
            container = new FDRelation().parse("a->b").parse("bc->a");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        FDSolver solver = new FDSolver(container);
        FDKeySet set = new FDKeySet();
        set.add(new FDKey("a","c"));
        set.add(new FDKey("b","c"));
        assertEquals(set,solver.getKeyCandidates());
    }

    /**
     * "a->b","bc->a" should be in Normal Form 3
     */
    @Test
    public void nf3Test(){
        FDRelation container = null;
        try {
            container = new FDRelation().parse("a->b").parse("bc->a");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        FDSolver solver = new FDSolver(container);
        assertEquals(3,solver.getNF());
    }

    /**
     * "ab->c","b->c" should be in NormalForm 1
     */
    @Test
    public void nf1Test(){
        FDRelation container = null;
        try {
            container = new FDRelation().parse("ab->c").parse("b->c");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        FDSolver solver = new FDSolver(container);
        assertEquals(1,solver.getNF());
    }

    /**
     * "a->b","a->c","a->d","c->d" should be in normal Form 2.
     */
    @Test
    public void nf2Test(){
        FDRelation container = null;
        try {
            container = new FDRelation().parse("a->b")
                    .parse("a->c")
                    .parse("a->d")
                    .parse("c->d");
        } catch (FDKey.EmptyException | FDRelation.UnexpectedAttributeException e) {
            assertTrue(false);
        }
        FDSolver solver = new FDSolver(container);
        assertEquals(2,solver.getNF());
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sailw
 */
public class LabelInferenceTest {
    
    public LabelInferenceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }

    /**
     * Test of multiplicative method, of class LabelInference.
     */
    @Test
    public void testMultiplicative() {
        System.out.println("multiplicative");
        Graph G = null;
        Graph expResult = null;
        Graph result = LabelInference.multiplicative(G);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of blockCoordinateDescent method, of class LabelInference.
     */
    @Test
    public void testBlockCoordinateDescent() {
        System.out.println("blockCoordinateDescent");
        Graph G = null;
        Graph expResult = null;
        Graph result = LabelInference.blockCoordinateDescent(G);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of labelPropagation method, of class LabelInference.
     */
    @Test
    public void testLabelPropagation() {
        System.out.println("labelPropagation");
        Graph G = null;
        Graph expResult = null;
        Graph result = LabelInference.labelPropagation(G);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

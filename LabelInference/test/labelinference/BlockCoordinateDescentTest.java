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
public class BlockCoordinateDescentTest {
    
    public BlockCoordinateDescentTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }

    /**
     * Test of getResult method, of class BlockCoordinateDescent.
     */
    @Test
    public void testGetResult() {
        System.out.println("getResult");
        BlockCoordinateDescent instance = null;
        Graph expResult = null;
        Graph result = instance.getResult();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

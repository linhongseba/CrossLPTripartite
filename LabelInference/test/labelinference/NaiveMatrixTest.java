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
public class NaiveMatrixTest {
    
    public NaiveMatrixTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }

    /**
     * Test of times method, of class NaiveMatrix.
     */
    @Test
    public void testTimes() throws Exception {
        System.out.println("times");
        Matrix b = null;
        NaiveMatrix instance = new NaiveMatrix();
        Matrix expResult = null;
        Matrix result = instance.times(b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cron method, of class NaiveMatrix.
     */
    @Test
    public void testCron() throws Exception {
        System.out.println("cron");
        Matrix b = null;
        NaiveMatrix instance = new NaiveMatrix();
        Matrix expResult = null;
        Matrix result = instance.cron(b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of add method, of class NaiveMatrix.
     */
    @Test
    public void testAdd() throws Exception {
        System.out.println("add");
        Matrix b = null;
        NaiveMatrix instance = new NaiveMatrix();
        Matrix expResult = null;
        Matrix result = instance.add(b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of subtract method, of class NaiveMatrix.
     */
    @Test
    public void testSubtract() throws Exception {
        System.out.println("subtract");
        Matrix b = null;
        NaiveMatrix instance = new NaiveMatrix();
        Matrix expResult = null;
        Matrix result = instance.subtract(b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of divide method, of class NaiveMatrix.
     */
    @Test
    public void testDivide() throws Exception {
        System.out.println("divide");
        Matrix b = null;
        NaiveMatrix instance = new NaiveMatrix();
        Matrix expResult = null;
        Matrix result = instance.divide(b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sqrt method, of class NaiveMatrix.
     */
    @Test
    public void testSqrt() throws Exception {
        System.out.println("sqrt");
        NaiveMatrix instance = new NaiveMatrix();
        Matrix expResult = null;
        Matrix result = instance.sqrt();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of get method, of class NaiveMatrix.
     */
    @Test
    public void testGet() throws Exception {
        System.out.println("get");
        int row = 0;
        int col = 0;
        NaiveMatrix instance = new NaiveMatrix();
        double expResult = 0.0;
        double result = instance.get(row, col);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRow method, of class NaiveMatrix.
     */
    @Test
    public void testGetRow() throws Exception {
        System.out.println("getRow");
        int row = 0;
        NaiveMatrix instance = new NaiveMatrix();
        Matrix expResult = null;
        Matrix result = instance.getRow(row);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCol method, of class NaiveMatrix.
     */
    @Test
    public void testGetCol() throws Exception {
        System.out.println("getCol");
        int col = 0;
        NaiveMatrix instance = new NaiveMatrix();
        Matrix expResult = null;
        Matrix result = instance.getCol(col);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of set method, of class NaiveMatrix.
     */
    @Test
    public void testSet() throws Exception {
        System.out.println("set");
        int row = 0;
        int col = 0;
        double x = 0.0;
        NaiveMatrix instance = new NaiveMatrix();
        instance.set(row, col, x);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setRow method, of class NaiveMatrix.
     */
    @Test
    public void testSetRow() throws Exception {
        System.out.println("setRow");
        int row = 0;
        Matrix b = null;
        NaiveMatrix instance = new NaiveMatrix();
        instance.setRow(row, b);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCol method, of class NaiveMatrix.
     */
    @Test
    public void testSetCol() throws Exception {
        System.out.println("setCol");
        int col = 0;
        Matrix b = null;
        NaiveMatrix instance = new NaiveMatrix();
        instance.setCol(col, b);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of subMatrix method, of class NaiveMatrix.
     */
    @Test
    public void testSubMatrix() throws Exception {
        System.out.println("subMatrix");
        int topRow = 0;
        int bottomRow = 0;
        int leftCol = 0;
        int rightCol = 0;
        NaiveMatrix instance = new NaiveMatrix();
        Matrix expResult = null;
        Matrix result = instance.subMatrix(topRow, bottomRow, leftCol, rightCol);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of determinant method, of class NaiveMatrix.
     */
    @Test
    public void testDeterminant() {
        System.out.println("determinant");
        NaiveMatrix instance = new NaiveMatrix();
        double expResult = 0.0;
        double result = instance.determinant();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of inverse method, of class NaiveMatrix.
     */
    @Test
    public void testInverse() throws Exception {
        System.out.println("inverse");
        NaiveMatrix instance = new NaiveMatrix();
        Matrix expResult = null;
        Matrix result = instance.inverse();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of transpose method, of class NaiveMatrix.
     */
    @Test
    public void testTranspose() {
        System.out.println("transpose");
        NaiveMatrix instance = new NaiveMatrix();
        Matrix expResult = null;
        Matrix result = instance.transpose();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of norm method, of class NaiveMatrix.
     */
    @Test
    public void testNorm() {
        System.out.println("norm");
        String normName = "";
        NaiveMatrix instance = new NaiveMatrix();
        double expResult = 0.0;
        double result = instance.norm(normName);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

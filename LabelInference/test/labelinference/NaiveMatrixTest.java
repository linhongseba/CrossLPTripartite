/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.IrreversibleException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sailw
 */
public class NaiveMatrixTest {
    
    double dataA[][]={{1,2},{4,8}};
    double dataB[][]={{5,8,5},{2,3,4}};
    double dataC[][]={{6,4},{3,4},{7,6}};
    double dataD[][]={{2,1,5},{2,4,4},{7,3,1}};
    
    Matrix mA;
    Matrix mB;
    Matrix mC;
    Matrix mD;
    Matrix mE;
    Matrix mF;
    
    public NaiveMatrixTest() throws DimensionNotAgreeException {
        mA=new NaiveMatrix(dataA);
        mB=new NaiveMatrix(dataB);
        mC=new NaiveMatrix(dataC);
        mD=new NaiveMatrix(dataD);
        mE=mB.times(mC);
        mF=mC.times(mB);
    }

    /**
     * Test of times method, of class NaiveMatrix.
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testTimes() throws DimensionNotAgreeException {
        System.out.println("times");
        double expResData1[][]={{9,18},{36,72}};
        Matrix expResult = new NaiveMatrix(expResData1);
        Matrix result = mA.times(mA);
        assertEquals(expResult, result);
        
        double expResData2[][]={{9, 14, 13}, {36, 56, 52}};
        expResult = new NaiveMatrix(expResData2);
        result = mA.times(mB);
        assertEquals(expResult, result);
        
        double expResData3[][]={{41, 21, 19}, {40, 30, 30}, {27, 22, 48}};
        expResult = new NaiveMatrix(expResData3);
        result = mD.times(mD);
        assertEquals(expResult, result);
        
        double expMEData[][]={{89, 82}, {49, 44}};
        Matrix expME = new NaiveMatrix(expMEData);
        assertEquals(expME, mE);
        
        double expMFData[][]={{38, 60, 46}, {23, 36, 31}, {47, 74, 59}};
        Matrix expMF = new NaiveMatrix(expMFData);
        assertEquals(expMF, mF);
    }

    /**
     * Test of cron method, of class NaiveMatrix.
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testCron() throws DimensionNotAgreeException  {
        System.out.println("cron");
        double expResData1[][]={{1,4},{16,64}};
        Matrix expResult = new NaiveMatrix(expResData1);
        Matrix result = mA.cron(mA);
        assertEquals(expResult, result);
        
        double expResData2[][]={{4, 1, 25}, {4, 16, 16}, {49, 9, 1}};
        expResult = new NaiveMatrix(expResData2);
        result = mD.cron(mD);
        assertEquals(expResult, result);
    }

    /**
     * Test of add method, of class NaiveMatrix.
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testAdd() throws DimensionNotAgreeException {
        System.out.println("add");
        double expResData1[][]={{2,4},{8,16}};
        Matrix expResult = new NaiveMatrix(expResData1);
        Matrix result = mA.cron(mA);
        assertEquals(expResult, result);
        
        double expResData2[][]={{4, 2, 10}, {4, 8, 8}, {14, 6, 2}};
        expResult = new NaiveMatrix(expResData2);
        result = mD.cron(mD);
        assertEquals(expResult, result);
    }

    /**
     * Test of subtract method, of class NaiveMatrix.
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testSubtract() throws DimensionNotAgreeException {
        System.out.println("subtract");
        double expResData1[][]={{88,80},{45,36}};
        Matrix expResult = new NaiveMatrix(expResData1);
        Matrix result = mE.subtract(mA);
        assertEquals(expResult, result);
        
        double expResData2[][]={{36, 59, 41}, {21, 32, 27}, {40, 71, 58}};
        expResult = new NaiveMatrix(expResData2);
        result = mF.subtract(mD);
        assertEquals(expResult, result);
    }

    /**
     * Test of divide method, of class NaiveMatrix.
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testDivide() throws DimensionNotAgreeException  {
        System.out.println("divide");
        double expResData1[][]={{89, 41}, {49/4, 11/2}};
        Matrix expResult = new NaiveMatrix(expResData1);
        Matrix result = mE.divide(mA);
        assertEquals(expResult, result);
        
        double expResData2[][]={{19, 60, 46/5}, {23/2, 9, 31/4}, {47/7, 74/3, 59}};
        expResult = new NaiveMatrix(expResData2);
        result = mF.divide(mD);
        assertEquals(expResult, result);
    }

    /**
     * Test of sqrt method, of class NaiveMatrix.
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testSqrt() throws DimensionNotAgreeException {
        System.out.println("sqrt");
        double expResData2[][]={{sqrt(2), 1, sqrt(5)}, {sqrt(2), 2, 2}, {sqrt(7), sqrt(3), 1}};
        Matrix expResult = new NaiveMatrix(expResData2);
        Matrix result = mD.sqrt();
        assertEquals(expResult, result);
    }

    /**
     * Test of determinant method, of class NaiveMatrix.
     */
    @Test
    public void testDeterminant() {
        System.out.println("determinant");
        double result = mA.determinant();
        if(abs(result)>1e-6)fail();
        
        result = mD.determinant();
        if(abs(result+100)>1e-6)fail();
    }

    /**
     * Test of inverse method, of class NaiveMatrix.
     * @throws labelinference.exceptions.IrreversibleException
     */
    @Test
    public void testInverse() throws IrreversibleException {
        System.out.println("inverse");
        double expResData2[][]={{2/25, -7/50, 4/25}, {-13/50, 33/100, -1/50}, {11/50, -1/100, -3/50}};
        Matrix expResult = new NaiveMatrix(expResData2);
        Matrix result = mD.inverse();
        assertEquals(expResult, result);
    }

    /**
     * Test of transpose method, of class NaiveMatrix.
     */
    @Test
    public void testTranspose() {
        System.out.println("transpose");
        double expResData2[][]={{2, 2, 7}, {1, 4, 3}, {5, 4, 1}};
        Matrix expResult = new NaiveMatrix(expResData2);
        Matrix result = mD.transpose();
        assertEquals(expResult, result);
    }

    /**
     * Test of norm method, of class NaiveMatrix.
     */
    @Test
    public void testNorm() {
        System.out.println("norm");
        double result = mA.norm(Matrix.FROBENIUS_NORM);
        if(abs(result-sqrt(85))>1e-6)fail();
        
        result = mD.norm(Matrix.FROBENIUS_NORM);
        if(abs(result-sqrt(125))>1e-6)fail();
    }
    
}

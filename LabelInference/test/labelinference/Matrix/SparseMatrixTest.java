/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.Matrix;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.IrreversibleException;
import labelinference.exceptions.RowOutOfRangeException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sailw
 */
public class SparseMatrixTest {
    double dataA[][]={{1,2},{4,8}};
    double dataB[][]={{5,8,5},{2,3,4}};
    double dataC[][]={{6,4},{3,4},{7,6}};
    double dataD[][]={{2,1,5},{2,4,4},{7,3,1}};
    double dataG[][]={{2,1,5},{2,4,0},{2,0,1}};
    double dataF[][]={{9,0,7}};
    
    Matrix mA;
    Matrix mB;
    Matrix mC;
    Matrix mD;
    Matrix mE;
    Matrix mF;
    Matrix mG;
    Matrix mH;
    
    public SparseMatrixTest() throws DimensionNotAgreeException {
        mA=new SparseMatrix(dataA);
        mB=new SparseMatrix(dataB);
        mC=new SparseMatrix(dataC);
        mD=new SparseMatrix(dataD);
        mE=mB.times(mC);
        mF=mC.times(mB);
        mG=new SparseMatrix(dataG);
        mH=new SparseMatrix(dataF);
    }

    /**
     * Test of set method, of class SparseMatrix.
     */
    @Test
    public void testTimes() throws DimensionNotAgreeException {
        System.out.println("times");
        double expResData1[][]={{9,18},{36,72}};
        Matrix expResult = new SparseMatrix(expResData1);
        Matrix result = mA.times(mA);
        assertEquals(expResult, result);
        
        double expResData2[][]={{9, 14, 13}, {36, 56, 52}};
        expResult = new SparseMatrix(expResData2);
        result = mA.times(mB);
        assertEquals(expResult, result);
        
        double expResData3[][]={{41, 21, 19}, {40, 30, 30}, {27, 22, 48}};
        expResult = new SparseMatrix(expResData3);
        result = mD.times(mD);
        assertEquals(expResult, result);
        
        double expMEData[][]={{89, 82}, {49, 44}};
        Matrix expME = new SparseMatrix(expMEData);
        assertEquals(expME, mE);
        
        double expMFData[][]={{38, 60, 46}, {23, 36, 31}, {47, 74, 59}};
        Matrix expMF = new SparseMatrix(expMFData);
        assertEquals(expMF, mF);
    }

    /**
     * Test of cron method, of class SparseMatrix.
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testCron() throws DimensionNotAgreeException  {
        System.out.println("cron");
        double expResData1[][]={{1,4},{16,64}};
        Matrix expResult = new SparseMatrix(expResData1);
        Matrix result = mA.cron(mA);
        assertEquals(expResult, result);
        
        double expResData2[][]={{4, 1, 25}, {4, 16, 0}, {14, 0, 1}};
        expResult = new SparseMatrix(expResData2);
        result = mG.cron(mD);
        System.out.println(result.toString());
        assertEquals(expResult, result);
    }

    /**
     * Test of add method, of class SparseMatrix.
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testAdd() throws DimensionNotAgreeException {
        System.out.println("add");
        double expResData1[][]={{2,4},{8,16}};
        Matrix expResult = new SparseMatrix(expResData1);
        Matrix result = mA.add(mA);
        assertEquals(expResult, result);
        
        double expResData2[][]={{4, 2, 10}, {4, 8, 8}, {14, 6, 2}};
        expResult = new SparseMatrix(expResData2);
        result = mD.add(mD);
        assertEquals(expResult, result);
    }

    /**
     * Test of subtract method, of class SparseMatrix.
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testSubtract() throws DimensionNotAgreeException {
        System.out.println("subtract");
        double expResData1[][]={{88,80},{45,36}};
        Matrix expResult = new SparseMatrix(expResData1);
        Matrix result = mE.subtract(mA);
        assertEquals(expResult, result);
        
        double expResData2[][]={{36, 59, 41}, {21, 32, 27}, {40, 71, 58}};
        expResult = new SparseMatrix(expResData2);
        result = mF.subtract(mD);
        assertEquals(expResult, result);
    }

    /**
     * Test of divide method, of class SparseMatrix.
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testDivide() throws DimensionNotAgreeException  {
        System.out.println("divide");
        double expResData1[][]={{89, 41}, {49.0/4, 11.0/2}};
        Matrix expResult = new SparseMatrix(expResData1);
        Matrix result = mE.divide(mA);
        assertEquals(expResult, result);
        
        double expResData2[][]={{19, 60, 46.0/5}, {23.0/2, 9, 31.0/4}, {47.0/7, 74.0/3, 59}};
        expResult = new SparseMatrix(expResData2);
        result = mF.divide(mD);
        assertEquals(expResult, result);
        result = mF.divide(mG);
        System.out.println(result);
    }

    /**
     * Test of sqrt method, of class SparseMatrix.
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testSqrt() throws DimensionNotAgreeException {
        System.out.println("sqrt");
        double expResData2[][]={{sqrt(2), 1, sqrt(5)}, {sqrt(2), 2, 2}, {sqrt(7), sqrt(3), 1}};
        Matrix expResult = new SparseMatrix(expResData2);
        Matrix result = mD.sqrt();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRow method, of class SparseMatrix.
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testGetRow() throws DimensionNotAgreeException {
        System.out.println("getRow");
        double expResData2[][]={{2, 1, 5}, {9, 0, 7}, {2, 0, 1}};
        Matrix expResult = new SparseMatrix(expResData2);
        Matrix result = mG;
        try {
			result.setRow(1,mH);
		} catch (RowOutOfRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(result.toString());
        
        assertEquals(expResult, result);
    }

    /**
     * Test of determinant method, of class SparseMatrix.
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
     * Test of inverse method, of class SparseMatrix.
     * @throws labelinference.exceptions.IrreversibleException
     */
    @Test
    public void testInverse() throws IrreversibleException {
        System.out.println("inverse");
        double expResData2[][]={{2.0/25, -7.0/50, 4.0/25}, {-13.0/50, 33.0/100, -1.0/50}, {11.0/50, -1.0/100, -3.0/50}};
        Matrix expResult = new SparseMatrix(expResData2);
        Matrix result = mD.inverse();
        assertEquals(expResult, result);
    }

    /**
     * Test of transpose method, of class SparseMatrix.
     */
    @Test
    public void testTranspose() {
        System.out.println("transpose");
        double expResData2[][]={{2, 2, 7}, {1, 4, 3}, {5, 4, 1}};
        Matrix expResult = new SparseMatrix(expResData2);
        Matrix result = mD.transpose();
        assertEquals(expResult, result);
    }

    /**
     * Test of norm method, of class SparseMatrix.
     */
    @Test
    public void testNorm() throws ColumnOutOfRangeException {
        System.out.println("norm");
        double result = mD.norm(Matrix.FROBENIUS_NORM);
        System.out.println(result);
        if(abs(result-sqrt(125))>1e-6)fail();

        result = mD.getCol(0).norm(Matrix.FIRST_NORM);
        if(abs(result-11)>1e-6)fail();
        
        result = mA.norm(Matrix.FROBENIUS_NORM);
        if(abs(result-sqrt(85))>1e-6)fail();
        
        
    }
    
    /**
     * Test of normalize method, of class SparseMatrix.
     * @throws labelinference.exceptions.ColumnOutOfRangeException
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testNormalize() throws ColumnOutOfRangeException, DimensionNotAgreeException {
        System.out.println("normalize");
        double expResData[][]={{2/11.0}, {2/11.0}, {7/11.0}};
        Matrix expResult = new SparseMatrix(expResData);
        Matrix result = mD.getCol(0).normalize();
        System.out.println(result.toString());
        System.out.println(expResult.toString());
               assertEquals(expResult, result);
    }
    
    /**
     * Test of times method, of class SparseMatrix.
     */
    @Test
    public void testTimesd() {
        System.out.println("times");
        double expResData[][]={{1,0.5,2.5},{1,2,2},{3.5,1.5,0.5}};
        Matrix expResult = new SparseMatrix(expResData);
        Matrix result = mD.times(0.5);
        assertEquals(expResult, result);
    }
}

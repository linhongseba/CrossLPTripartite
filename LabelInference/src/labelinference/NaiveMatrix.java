/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;
import Jama.*; 
import java.util.*;
import java.lang.Math.*;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.IrreversibleException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw
 */
public class NaiveMatrix implements Matrix{
	Jama.Matrix A; 
	public NaiveMatrix(int row,int col)
	{
		A= new Jama.Matrix(row,col);
	}
	public NaiveMatrix(double data[][])
	{
		A= new Jama.Matrix(data);
	}
	public NaiveMatrix(int dim)
	{
		A= new Jama.Matrix(dim,dim);
	}
    public Matrix times(Matrix b) throws DimensionNotAgreeException {
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
		if(M.A.getRowDimension()!=b.A.getRowDimension()||M.A.getColumnDimension()!=b.A.getColumnDimension())
   		{
   			throw new DimensionNotAgreeException();
   		}
   		M.A=A.times(b.A);
   		return M;
    }

    @Override
    public Matrix cron(Matrix b) throws DimensionNotAgreeException {
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
 		if(M.A.getRowDimension()!=b.A.getRowDimension()||M.A.getColumnDimension()!=b.A.getColumnDimension())
   		{
   			throw new DimensionNotAgreeException();
   		}
   		M.A=A.arrayTimes(b.A);
   		return M;
    }

    @Override
    public Matrix add(Matrix b) throws DimensionNotAgreeException {
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
		if(M.A.getRowDimension()!=b.A.getRowDimension()||M.A.getColumnDimension()!=b.A.getColumnDimension())
   		{
   			throw new DimensionNotAgreeException();
   		}
   		M.A=A.plus(b.A);
   		return M;
    }

    @Override
    public Matrix subtract(Matrix b) throws DimensionNotAgreeException {
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
  		if(M.A.getRowDimension()!=b.A.getRowDimension()||M.A.getColumnDimension()!=b.A.getColumnDimension())
   		{
   			throw new DimensionNotAgreeException();
   		}
    	M.A=A.minus(b.A);
    }

    @Override
    public Matrix divide(Matrix b) throws DimensionNotAgreeException {
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
 		if(M.A.getRowDimension()!=b.A.getRowDimension()||M.A.getColumnDimension()!=b.A.getColumnDimension())
   		{
   			throw new DimensionNotAgreeException();
   		}
    	M.A=A.arrayRightDivide(b.A);
    	return M;
    }

    @Override
    public Matrix sqrt(){
    	int n=A.getRowDimension(),m=A.getColumnDimension();
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
   		for(int i=0;i<n;i++)
    		for(int j=0;j<m;j++)
    			M.A.set(i,j,java.lang.Math.sqrt(A.get(i,j)));
    	return M;
    }

    @Override
    public double get(int row, int col) throws ColumnOutOfRangeException, RowOutOfRangeException {
   		if(row>A.getRowDimension())
   		{
   			throw new RowOutOfRangeException();
   		}
   		if(col>A.getColumnDimension())
   		{
   			throw new ColumnOutOfRangeException();
   		}
    	return A.get(row,col);
    }

    @Override
    public Matrix getRow(int row) throws RowOutOfRangeException {
   		if(row>A.getRowDimension())
   		{
   			throw new RowOutOfRangeException();
   		}
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
   		M.A=A.getMatrix(row,row,0,A.getColumnDimension());
        return M;
    }

    @Override
    public Matrix getCol(int col) throws ColumnOutOfRangeException {
   		if(col>A.getColumnDimension())
   		{
   			throw new ColumnOutOfRangeException("Dimension Error!");
   		}
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
   		M.A=A.getMatrix(0,A.getRowDimension(),col,col);
        return M;
    }

    @Override
    public void set(int row, int col, double x) throws ColumnOutOfRangeException, RowOutOfRangeException {
  		if(row>A.getRowDimension())
   		{
   			throw new RowOutOfRangeException("Dimension Error!");
   		}
   		if(col>A.getColumnDimension())
   		{
   			throw new ColumnOutOfRangeException("Dimension Error!");
   		}
        A.set(row, col, x);
    }

    @Override
    public void setRow(int row, Matrix b) throws RowOutOfRangeException, DimensionNotAgreeException {
  		if(row>A.getRowDimension())
   		{
   			throw new RowOutOfRangeException("Dimension Error!");
   		}
        A.setMatrix(row,row,0,A.getColumnDimension(),b.A);
    }

    @Override
    public void setCol(int col, Matrix b) throws ColumnOutOfRangeException, DimensionNotAgreeException {
   		if(col>A.getColumnDimension())
   		{
   			throw new ColumnOutOfRangeException("Dimension Error!");
   		}
        A.setMatrix(0,A.getRowDimension(),col,col,b.A);
    }

    @Override
    public Matrix subMatrix(int topRow, int bottomRow, int leftCol, int rightCol) throws ColumnOutOfRangeException, RowOutOfRangeException {
  		if(bottomRow>A.getRowDimension())
   		{
   			throw new RowOutOfRangeException("Dimension Error!");
   		}
   		if(rightCol>A.getColumnDimension())
   		{
   			throw new ColumnOutOfRangeException("Dimension Error!");
   		}
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
   		M.A=A.getMatrix(topRow,bottomRow,leftCol,rightCol);
		return M;
    }

    @Override
    public double determinant() {
    	return A.det();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix inverse() throws IrreversibleException {
    	if(A.inverse()==null)
    		throw new IrreversibleException("Irreversibl!");
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
   		M.A=A.inverse();
    	return M;
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix transpose() {
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
   		M.A=A.transpose();
    	return M;
    }

    @Override
    public double norm(String normName) {
    	if(normName=="F")
    		return A.normF();
    }
    
}
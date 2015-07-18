/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import static java.lang.Math.abs;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.IrreversibleException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw,Tangyiqi
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
    
    @Override
    public void clone(Matrix b)
    {
    	NaiveMatrix M;
    	M=(NaiveMatrix)b;
   		A=M.A.getMatrix(0,M.A.getRowDimension(),0,M.A.getColumnDimension());
	}
    @Override
    public Matrix times(Matrix b) throws DimensionNotAgreeException {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        if(M.A.getColumnDimension()!=((NaiveMatrix)b).A.getRowDimension())
        {
                throw new DimensionNotAgreeException();
        }
        M.A=A.times(((NaiveMatrix)b).A);
        return M;
    }

    @Override
    public Matrix cron(Matrix b) throws DimensionNotAgreeException {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        if(M.A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension()||M.A.getColumnDimension()!=((NaiveMatrix)b).A.getColumnDimension())
        {
            throw new DimensionNotAgreeException();
        }
        M.A=A.arrayTimes(((NaiveMatrix)b).A);
        return M;
    }

    @Override
    public Matrix add(Matrix b) throws DimensionNotAgreeException {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        if(M.A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension()||M.A.getColumnDimension()!=((NaiveMatrix)b).A.getColumnDimension())
        {
                throw new DimensionNotAgreeException();
        }
        M.A=A.plus(((NaiveMatrix)b).A);
        return M;
    }

    @Override
    public Matrix subtract(Matrix b) throws DimensionNotAgreeException {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        if(M.A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension()||M.A.getColumnDimension()!=((NaiveMatrix)b).A.getColumnDimension())
        {
                throw new DimensionNotAgreeException();
        }
    	M.A=A.minus(((NaiveMatrix)b).A);
        return M;
    }

    @Override
    public Matrix divide(Matrix b) throws DimensionNotAgreeException {
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
 		if(M.A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension()||M.A.getColumnDimension()!=((NaiveMatrix)b).A.getColumnDimension())
   		{
   			throw new DimensionNotAgreeException();
   		}
    	M.A=A.arrayRightDivide(((NaiveMatrix)b).A);
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
   		if(row>=A.getRowDimension())
   		{
   			throw new RowOutOfRangeException();
   		}
   		if(col>=A.getColumnDimension())
   		{
   			throw new ColumnOutOfRangeException();
   		}
    	return A.get(row,col);
    }

    @Override
    public Matrix getRow(int row) throws RowOutOfRangeException {
   		if(row>=A.getRowDimension())
   		{
   			throw new RowOutOfRangeException();
   		}
   		NaiveMatrix M=new NaiveMatrix(1,A.getColumnDimension());
   		M.A=A.getMatrix(row,row,0,A.getColumnDimension()-1);
        return M;
    }

    @Override
    public Matrix getCol(int col) throws ColumnOutOfRangeException {
   		if(col>=A.getColumnDimension())
   		{
   			throw new ColumnOutOfRangeException();
   		}
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),1);
   		M.A=A.getMatrix(0,A.getRowDimension()-1,col,col);
        return M;
    }

    @Override
    public void set(int row, int col, double x) throws ColumnOutOfRangeException, RowOutOfRangeException {
  		if(row>=A.getRowDimension())
   		{
   			throw new RowOutOfRangeException();
   		}
   		if(col>=A.getColumnDimension())
   		{
   			throw new ColumnOutOfRangeException();
   		}
        A.set(row, col, x);
    }
    @Override
    public void setM(int upperrow,int bottomrow, int leftcol,int rightcol, Matrix x) throws ColumnOutOfRangeException, RowOutOfRangeException {
  		if(bottomrow>=A.getRowDimension())
   		{
   			throw new RowOutOfRangeException();
   		}
   		if(rightcol>=A.getColumnDimension())
   		{
   			throw new ColumnOutOfRangeException();
   		}
        A.setMatrix(upperrow, bottomrow, leftcol, rightcol, ((NaiveMatrix)x).A);
    }

    @Override
    public void setRow(int row, Matrix b) throws RowOutOfRangeException, DimensionNotAgreeException {
  		if(row>A.getRowDimension())
   		{
   			throw new RowOutOfRangeException();
   		}
        A.setMatrix(row,row,0,A.getColumnDimension(),((NaiveMatrix)b).A);
    }

    @Override
    public void setCol(int col, Matrix b) throws ColumnOutOfRangeException, DimensionNotAgreeException {
   		if(col>=A.getColumnDimension())
   		{
   			throw new ColumnOutOfRangeException();
   		}
        A.setMatrix(0,A.getRowDimension(),col,col,((NaiveMatrix)b).A);
    }

    @Override
    public Matrix subMatrix(int topRow, int bottomRow, int leftCol, int rightCol) throws ColumnOutOfRangeException, RowOutOfRangeException {
  		if(bottomRow>=A.getRowDimension())
   		{
   			throw new RowOutOfRangeException();
   		}
   		if(rightCol>=A.getColumnDimension())
   		{
   			throw new ColumnOutOfRangeException();
   		}
   		NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
   		M.A=A.getMatrix(topRow,bottomRow,leftCol,rightCol);
		return M;
    }

    @Override
    public double determinant() {
    	return A.det();
    }
    
    @Override
    public Matrix inverse() throws IrreversibleException {
    	if(A.inverse()==null)throw new IrreversibleException();
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        M.A=A.inverse();
    	return M;
    }
    
    @Override
    public Matrix transpose() {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        M.A=A.transpose();
    	return M;
    }

    @Override
    public double norm(Norm normName) {
    	if(normName==Matrix.FROBENIUS_NORM)
            return A.normF();
        return 0;
    }
    
   @Override
    public boolean equals(Object object) {
        if (!(object instanceof NaiveMatrix)) {
            return false;
        }
        NaiveMatrix other = (NaiveMatrix) object;
        
        if(A.getRowDimension()!=other.A.getRowDimension()||A.getColumnDimension()!=other.A.getColumnDimension())return false;
        
        for(int row=0;row<A.getRowDimension();row++)
            for(int col=0;col<A.getColumnDimension();col++)
                try {
                    if(abs(get(row,col)-other.get(row,col))>1e-6)return false;
                } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        return true;
    }

    @Override
    public String toString() {
        String ret="{";
        for(int row=0;row<A.getRowDimension();row++) {
            ret+="{";
            for(int col=0;col<A.getColumnDimension();col++)
                try {
                    ret+=(Double)get(row,col);
                    if(A.getColumnDimension()-col>1)ret+=", ";
                } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
            ret+="}";
            if(A.getRowDimension()-row>1)ret+=", ";
        }
        return ret;
    }

    @Override
    public Matrix times(double lambda) {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        M.A=A.times(lambda);
    	return M;
    }

    @Override
    public Matrix orthonormalize() throws DimensionNotAgreeException {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        if(A.getColumnDimension()!=1)throw new DimensionNotAgreeException();
        double sum=0;
        for(int i=0;i<A.getRowDimension();i++)sum+=A.get(i, 0);
        for(int i=0;i<A.getRowDimension();i++)M.A.set(i, 0, A.get(i, 0)/sum);
    	return M;
    }
}

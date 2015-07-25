/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.Matrix;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.IrreversibleException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author 
 */
public class SparseMatrix implements Matrix {
    private final ArrayList<Map<Integer,Double>> A;
    private final int maxRow;
    private final int maxCol;
    private static final double ZERO=1e-100;
    public SparseMatrix(int row,int col) {
        maxCol=col;
        maxRow=row;
        A=new ArrayList<>();
        for(int r=0;r<row;r++)A.add(new HashMap<Integer,Double>());
    }
    
    public SparseMatrix(double data[][]) {
        maxCol=data[0].length;
        maxRow=data.length;
        A=new ArrayList<>();
        for(int r=0;r<data.length;r++) {
            Map<Integer,Double> col=new HashMap<>();
            for(int c=0;c<data[0].length;c++)
                if(abs(data[r][c]-0)>ZERO)col.put(c, data[r][c]);
            A.add(col);
        }
    }
    
    public SparseMatrix(int dim) {
        maxCol=dim;
        maxRow=dim;
        A=new ArrayList<>();
        for(int r=0;r<dim;r++) {
            A.add(new HashMap<Integer,Double>());
            A.get(r).put(r, 1.0);
        }
    }
    
    @Override
    public double get(int row, int col) throws ColumnOutOfRangeException, RowOutOfRangeException {
        if(row>=maxRow)throw new RowOutOfRangeException();
        if(col>=maxCol)throw new ColumnOutOfRangeException();
        
        double temp=A.get(row).get(col);
        return temp;
    }

    @Override
    public Matrix getRow(int row) throws RowOutOfRangeException {
        if(row>=maxRow)throw new RowOutOfRangeException();
        
        SparseMatrix M=new SparseMatrix(1,maxCol);
        for(Integer it:A.get(row).keySet())
        {
            double temp=A.get(row).get(it);
            M.A.get(0).put(it,temp);
        }
        return M;
    }

    @Override
    public Matrix getCol(int col) throws ColumnOutOfRangeException {
        if(col>=maxCol)throw new ColumnOutOfRangeException();
        
        SparseMatrix M=new SparseMatrix(maxRow,1);
        for(int r=0;r<maxRow;r++)
        {
            double temp=A.get(r).get(col);
            M.A.get(r).put(0,temp);
        }
        return M;
    }

    @Override
    public void set(int row, int col, double x) throws ColumnOutOfRangeException, RowOutOfRangeException {
        if(row>=maxRow)throw new RowOutOfRangeException();
        if(col>=maxCol)throw new ColumnOutOfRangeException();
        A.get(row).put(col,x);
    }

    @Override
    public void setRow(int row, Matrix b) throws RowOutOfRangeException, DimensionNotAgreeException {
        if(row>=maxRow)throw new RowOutOfRangeException();
        if(((SparseMatrix)b).maxCol!=1||((SparseMatrix)b).maxCol!=maxCol)throw new DimensionNotAgreeException();
        
        for(Integer it:((SparseMatrix)b).A.get(0).keySet())
        {
            double temp=((SparseMatrix)b).A.get(0).get(it);
            A.get(row).put(it,temp);
        }
    }

    @Override
    public void setCol(int col, Matrix b) throws ColumnOutOfRangeException, DimensionNotAgreeException {
        if(col>=maxCol)throw new ColumnOutOfRangeException();
        if(((SparseMatrix)b).maxRow!=1||((SparseMatrix)b).maxRow!=maxRow)throw new DimensionNotAgreeException();
        
        for(int r=0;r<maxRow;r++)
        {
            double temp=((SparseMatrix)b).A.get(r).get(0);
            A.get(r).put(col,temp);
        }
    }
    
    @Override
    public Matrix times(Matrix b) throws DimensionNotAgreeException {
        
        SparseMatrix M=new SparseMatrix(maxRow,((SparseMatrix)b).maxCol);
    	//System.out.println(maxRow+"+"+((SparseMatrix)b).maxCol);
    	//System.out.println(A.toString()+"\n+\n"+((SparseMatrix)b).toString());
        if(((SparseMatrix)b).maxRow!=maxCol)throw new DimensionNotAgreeException();
        
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
            {
                if(A.get(r).get(c)>ZERO)
                {
                    for(Integer k:((SparseMatrix)b).A.get(c).keySet())
                    {
                    	//System.out.println(r+","+c+","+k);
                    	//System.out.println(A.get(r).get(c));
                    	//System.out.println(((SparseMatrix)b).A.get(c).get(k));
                    	double temp=A.get(r).get(c)*((SparseMatrix)b).A.get(c).get(k);
                    	if(M.A.get(r).get(k)==null)
                            M.A.get(r).put(k,temp);
                    	else
                    		M.A.get(r).put(k,M.A.get(r).get(k)+temp);
                    }
                }
            }
        return M;
    
    }

    @Override
    public Matrix times(double lambda) {
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
                M.A.get(r).put(c,A.get(r).get(c)*lambda);
        return M;
    }

    @Override
    public Matrix copy() {
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
                M.A.get(r).put(c,A.get(r).get(c));
        return M;
    }

    @Override
    public Matrix cron(Matrix b) throws DimensionNotAgreeException {
    	if(((SparseMatrix)b).maxRow!=maxRow||((SparseMatrix)b).maxCol!=maxCol)throw new DimensionNotAgreeException();
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
                M.A.get(r).put(c,A.get(r).get(c)*((SparseMatrix)b).A.get(r).get(c));
        return M;
    }

    @Override
    public Matrix add(Matrix b) throws DimensionNotAgreeException {
    	if(((SparseMatrix)b).maxRow!=maxRow||((SparseMatrix)b).maxCol!=maxCol)throw new DimensionNotAgreeException();
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
                M.A.get(r).put(c,A.get(r).get(c)+((SparseMatrix)b).A.get(r).get(c));
        return M;
    }

    @Override
    public Matrix subtract(Matrix b) throws DimensionNotAgreeException {
    	if(((SparseMatrix)b).maxRow!=maxRow||((SparseMatrix)b).maxCol!=maxCol)throw new DimensionNotAgreeException();
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
                M.A.get(r).put(c,A.get(r).get(c)-((SparseMatrix)b).A.get(r).get(c));
        return M;
    }

    @Override
    public Matrix divide(Matrix b) throws DimensionNotAgreeException {
    	if(((SparseMatrix)b).maxRow!=maxRow||((SparseMatrix)b).maxCol!=maxCol)throw new DimensionNotAgreeException();
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
                M.A.get(r).put(c,A.get(r).get(c)/((SparseMatrix)b).A.get(r).get(c));
        return M;
    }

    @Override
    public Matrix sqrt() throws DimensionNotAgreeException {
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
                M.A.get(r).put(c,java.lang.Math.sqrt(A.get(r).get(c)));
        return M;
    }

    @Override
    public double determinant() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix inverse() throws IrreversibleException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix transpose() {
    	SparseMatrix M=new SparseMatrix(maxCol,maxRow);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
            	M.A.get(c).put(r,A.get(r).get(c));
        return M;
    }

    @Override
    public Matrix normalize() throws DimensionNotAgreeException {
    	NaiveMatrix M=new NaiveMatrix(maxRow,maxCol);
        
    	for(int c=0;c<maxCol;c++)
    	{
    		double sum=0;
			for(int r=0;r<maxRow;r++)
            	sum+=abs(A.get(r).get(c));
            if(abs(sum)<ZERO)
            	sum=ZERO;
            for(Integer r:A.get(c).keySet())
            	M.A.set(r, c, A.get(r).get(c)/abs(sum));
    	}
    	return M;
    }

    @Override
    public double norm(Norm normName) {
    	if(normName==Matrix.FROBENIUS_NORM)
    	{
			double sum=0;
    		for(int r=0;r<maxRow;r++)
                for(Integer c:A.get(r).keySet())
                	sum+=A.get(r).get(c);
    		return java.lang.Math.sqrt(sum);
    	}
    	if(normName==Matrix.FIRST_NORM)
    	{
    		double max=0;
    		for(Integer c=0;c<maxCol;c++)
    		{
    			double sum=0;
    			for(int r=0;r<maxRow;r++)
    				sum+=abs(A.get(r).get(c));
    			if(sum>max)
    				max=sum;
    		}
    		return max;
    	}
    	return 0;
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SparseMatrix)) return false;
        SparseMatrix other = (SparseMatrix) object;
        if(maxRow!=other.maxRow||maxCol!=other.maxCol)return false;
        for(int row=0;row<maxRow;row++)
            for(int col=0;col<maxCol;col++)
                try {
                    if(abs(get(row,col)-other.get(row,col))>1e-6)return false;
                } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        return true;
    }

    @Override
    public String toString() {
        String ret="{";
        for(int row=0;row<maxRow;row++) {
            ret+="{";
            for(int col=0;col<maxCol;col++)
                try {
                    ret+=(Double)get(row,col);
                    if(maxCol-col>1)ret+=", ";
                } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
            ret+="}";
            if(maxRow-row>1)ret+=", ";
        }
        ret+="}";
        return ret;
    }

}
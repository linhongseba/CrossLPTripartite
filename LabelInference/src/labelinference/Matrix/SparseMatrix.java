/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.Matrix;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

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
            try {
				this.set(r,r,1.0);
			} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    @Override
    public double get(int row, int col) throws ColumnOutOfRangeException, RowOutOfRangeException {
        if(row>=maxRow)throw new RowOutOfRangeException();
        if(col>=maxCol)throw new ColumnOutOfRangeException();
        
        double temp=A.get(row).getOrDefault(col, (double) 0);
        return temp;
    }

    @Override
    public Matrix getRow(int row) throws RowOutOfRangeException {
        if(row>=maxRow)throw new RowOutOfRangeException();
        
        SparseMatrix M=new SparseMatrix(1,maxCol);
        for(Integer it:A.get(row).keySet())
        {
            double temp;
			try {
				temp = this.get(row,it);
	            M.set(0,it,temp);
			} catch (ColumnOutOfRangeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return M;
    }

    @Override
    public Matrix getCol(int col) throws ColumnOutOfRangeException {
        if(col>=maxCol)throw new ColumnOutOfRangeException();
        
        SparseMatrix M=new SparseMatrix(maxRow,1);
        for(int r=0;r<maxRow;r++)
        {
            double temp;
			try {
				temp = this.get(r,col);
	            M.set(r,0,temp);
			} catch (RowOutOfRangeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return M;
    }

    @Override
    public void set(int row, int col, double x) throws ColumnOutOfRangeException, RowOutOfRangeException {
        if(row>=maxRow)throw new RowOutOfRangeException();
        if(col>=maxCol)throw new ColumnOutOfRangeException();
        if(abs(x)>ZERO)A.get(row).put(col,x);
    }

    @Override
    public void setRow(int row, Matrix b) throws RowOutOfRangeException, DimensionNotAgreeException {
        if(row>=maxRow)throw new RowOutOfRangeException();
        if(((SparseMatrix)b).maxRow!=1||((SparseMatrix)b).maxCol!=maxCol)throw new DimensionNotAgreeException();
        
        for(int it=0;it<maxCol;it++)
        {
            double temp;
			try {
				temp = ((SparseMatrix)b).get(0,it);
	            A.get(row).put(it,temp);
			} catch (ColumnOutOfRangeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    @Override
    public void setCol(int col, Matrix b) throws ColumnOutOfRangeException, DimensionNotAgreeException {
        if(col>=maxCol)throw new ColumnOutOfRangeException();
        if(((SparseMatrix)b).maxCol!=1||((SparseMatrix)b).maxRow!=maxRow)throw new DimensionNotAgreeException();
        
        for(int r=0;r<maxRow;r++)
        {
            double temp;
			try {
				temp = ((SparseMatrix)b).get(r,0);
	            this.set(r,col,temp);
			} catch (RowOutOfRangeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
                try {
					if(this.get(r,c)>ZERO)
					    for(Integer k:((SparseMatrix)b).A.get(c).keySet())
					    {
					    	double temp=this.get(r,c)*((SparseMatrix)b).get(c,k);
				    		M.set(r,k,M.get(r,k)+temp);
					    }
				} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        return M;
    
    }

    @Override
    public Matrix times(double lambda) {
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
				try {
					M.set(r,c,this.get(r,c)*lambda);
				} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        return M;
    }

    @Override
    public Matrix copy() {
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
				try {
					M.set(r,c,this.get(r,c));
				} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        return M;
    }

    @Override
    public Matrix cron(Matrix b) throws DimensionNotAgreeException {
    	if(((SparseMatrix)b).maxRow!=maxRow||((SparseMatrix)b).maxCol!=maxCol)throw new DimensionNotAgreeException();
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
				try {
					M.set(r,c,this.get(r,c)*((SparseMatrix)b).get(r,c));
				} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        return M;
    }

    @Override
    public Matrix add(Matrix b) throws DimensionNotAgreeException {
    	if(((SparseMatrix)b).maxRow!=maxRow||((SparseMatrix)b).maxCol!=maxCol)throw new DimensionNotAgreeException();
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
				try {
					M.set(r,c,this.get(r,c)+((SparseMatrix)b).get(r,c));
				} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        return M;
    }

    @Override
    public Matrix subtract(Matrix b) throws DimensionNotAgreeException {
    	if(((SparseMatrix)b).maxRow!=maxRow||((SparseMatrix)b).maxCol!=maxCol)throw new DimensionNotAgreeException();
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
				try {
					M.set(r,c,this.get(r,c)-((SparseMatrix)b).get(r,c));
				} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        return M;
    }

    @Override
    public Matrix divide(Matrix b) throws DimensionNotAgreeException {
    	if(((SparseMatrix)b).maxRow!=maxRow||((SparseMatrix)b).maxCol!=maxCol)throw new DimensionNotAgreeException();
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        try {
             for(int r=0;r<maxRow;r++)
                 for(Integer c:A.get(r).keySet())
    		            if(abs(b.get(r, c))<ZERO) {
                        if(b.get(r, c)<0)b.set(r, c, -ZERO);
                        else b.set(r, c, ZERO);
                    }
        }
        catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
				try {
					M.set(r,c,this.get(r,c)/((SparseMatrix)b).get(r,c));
				} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        return M;
    }

    @Override
    public Matrix sqrt() throws DimensionNotAgreeException {
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        for(int r=0;r<maxRow;r++)
            for(Integer c:A.get(r).keySet())
				try {
					M.set(r,c,java.lang.Math.sqrt(this.get(r,c)));
				} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        return M;
    }

    @Override
    public double determinant() {
    	if(maxRow!=maxCol||maxRow>=3)
    		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		try {
	    	if(maxRow==1)
	    		return this.get(0, 0);
	    	if(maxRow==2)
	     	   	return this.get(0, 0)*this.get(1, 1)-this.get(0, 1)*this.get(1, 0);
		} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return 0;
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
				try {
					M.set(c,r,this.get(r,c));
				} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        return M;
    }

    @Override
    public Matrix normalize() throws DimensionNotAgreeException {
    	SparseMatrix M=new SparseMatrix(maxRow,maxCol);

    	for(int c=0;c<maxCol;c++)
    	{
    		double sum=0;
			for(int r=0;r<maxRow;r++)
				try {
					sum+=abs(this.get(r,c));
				} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            if(abs(sum)<ZERO)
            	sum=ZERO;
			for(int r=0;r<maxRow;r++)
				try {
					M.set(r, c, this.get(r,c)/abs(sum));
				} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
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
					try {
						sum+=this.get(r,c)*this.get(r,c);
					} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    		return java.lang.Math.sqrt(sum);
    	}
    	if(normName==Matrix.FIRST_NORM)
    	{
    		double max=0;
    		for(Integer c=0;c<maxCol;c++)
    		{
    			double sum=0;
    			for(int r=0;r<maxRow;r++)
					try {
						sum+=abs(this.get(r,c));
					} catch (ColumnOutOfRangeException | RowOutOfRangeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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

    @Override
    public Matrix adjoint() throws DimensionNotAgreeException {
        SparseMatrix M=new SparseMatrix(maxRow,maxCol);
        try {
            for(int row=0;row<maxRow;row++)
                for(int col=0;col<maxCol;col++) {
                	SparseMatrix T=new SparseMatrix(maxRow-1,maxCol-1);
                    for(int i=0;i<maxRow;i++)
                        for(int j=0;j<maxCol;j++) {
                            if(i<row && j<col)T.set(i, j, get(i,j));
                            if(i<row && j>col)T.set(i, j-1, get(i,j));
                            if(i>row && j<col)T.set(i-1, j, get(i,j));
                            if(i>row && j>col)T.set(i-1, j-1, get(i,j));
                        }
                    M.set(col, row, pow(-1,row+col)*T.determinant());
                }
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        return M;
    }

}
package labelinference.Matrix;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.IrreversibleException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw,Tangyiqi
 * @since 1.8
 * the class implements the general matrix computing by jama
 */
public class NaiveMatrix implements Matrix{
    Jama.Matrix A; 
    public NaiveMatrix(int row,int col) {
        A= new Jama.Matrix(row,col);
    }
    
    public NaiveMatrix(double data[][]) {
        A= new Jama.Matrix(data);
    }
    
    public NaiveMatrix(int dim) {
        A= new Jama.Matrix(dim,dim);
        for(int row=0;row<dim;row++)A.set(row, row, 1);
    }
    
    @Override
    public Matrix copy() {
    	NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        M.A=A.getMatrix(0,M.A.getRowDimension()-1,0,M.A.getColumnDimension()-1);
        return M;
    }
    
    @Override
    public Matrix times(Matrix b) throws DimensionNotAgreeException {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        if(M.A.getColumnDimension()!=((NaiveMatrix)b).A.getRowDimension())throw new DimensionNotAgreeException();
        M.A=A.times(((NaiveMatrix)b).A);
        return M;
    }
    
    @Override
    public Matrix times_assign(Matrix b) throws DimensionNotAgreeException {
        if(A.getColumnDimension()!=((NaiveMatrix)b).A.getRowDimension())throw new DimensionNotAgreeException();
        A=A.times(((NaiveMatrix)b).A);
        return this;
    }

    @Override
    public double innerProduct(Matrix b) throws DimensionNotAgreeException, ColumnOutOfRangeException, RowOutOfRangeException {
        if(A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension())throw new DimensionNotAgreeException();
        if(A.getColumnDimension()!=1 || ((NaiveMatrix)b).A.getColumnDimension()!=1)throw new DimensionNotAgreeException();
        double ret=0;
        for(int row=0;row<A.getRowDimension();row++)ret+=this.get(row, 0)*b.get(row, 0);
        return ret;
    }
    
    @Override
    public Matrix cron(Matrix b) throws DimensionNotAgreeException {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        if(M.A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension()||M.A.getColumnDimension()!=((NaiveMatrix)b).A.getColumnDimension())throw new DimensionNotAgreeException();
        M.A=A.arrayTimes(((NaiveMatrix)b).A);
        return M;
    }

    @Override
    public Matrix cron_assign(Matrix b) throws DimensionNotAgreeException {
        if(A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension()||A.getColumnDimension()!=((NaiveMatrix)b).A.getColumnDimension())throw new DimensionNotAgreeException();
        A.arrayTimesEquals(((NaiveMatrix)b).A);
        return this;
    }

    @Override
    public Matrix add(Matrix b) throws DimensionNotAgreeException {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        if(M.A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension()||M.A.getColumnDimension()!=((NaiveMatrix)b).A.getColumnDimension())throw new DimensionNotAgreeException();
        M.A=A.plus(((NaiveMatrix)b).A);
        return M;
    }

    @Override
    public Matrix add_assign(Matrix b) throws DimensionNotAgreeException {
        if(A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension()||A.getColumnDimension()!=((NaiveMatrix)b).A.getColumnDimension())throw new DimensionNotAgreeException();
        A.plusEquals(((NaiveMatrix)b).A);
        return this;
    }

    @Override
    public Matrix subtract(Matrix b) throws DimensionNotAgreeException {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        if(M.A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension()||M.A.getColumnDimension()!=((NaiveMatrix)b).A.getColumnDimension())throw new DimensionNotAgreeException();
    	M.A=A.minus(((NaiveMatrix)b).A);
        return M;
    }

    @Override
    public Matrix subtract_assign(Matrix b) throws DimensionNotAgreeException {
        if(A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension()||A.getColumnDimension()!=((NaiveMatrix)b).A.getColumnDimension())throw new DimensionNotAgreeException();
        A.minusEquals(((NaiveMatrix)b).A);
        return this;
    }

    @Override
    public Matrix divide(Matrix b) throws DimensionNotAgreeException {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        if(M.A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension()||M.A.getColumnDimension()!=((NaiveMatrix)b).A.getColumnDimension())throw new DimensionNotAgreeException();
        int n=A.getRowDimension(),m=A.getColumnDimension();
        final double ZERO=1e-9;
        try {
            for(int i=0;i<n;i++)
                for(int j=0;j<m;j++)
                    if(abs(b.get(i, j))<ZERO) {
                        if(b.get(i, j)<0)b.set(i, j, -ZERO);
                        else b.set(i, j, ZERO);
                    }
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        M.A=A.arrayRightDivide(((NaiveMatrix)b).A);
        return M;
    }

    @Override
    public Matrix divide_assign(Matrix b) throws DimensionNotAgreeException {
        if(A.getRowDimension()!=((NaiveMatrix)b).A.getRowDimension()||A.getColumnDimension()!=((NaiveMatrix)b).A.getColumnDimension())throw new DimensionNotAgreeException();
        int n=A.getRowDimension(),m=A.getColumnDimension();
        final double ZERO=1e-9;
        try {
            for(int i=0;i<n;i++){
                for(int j=0;j<m;j++){
                    if(b.get(i, j)<ZERO) {
                        b.set(i, j, ZERO);
                    }
                }
            }
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        A.arrayRightDivideEquals(((NaiveMatrix)b).A);	
        return this;
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
    public Matrix sqrt_assign(){
    	int n=A.getRowDimension(),m=A.getColumnDimension();
        for(int i=0;i<n;i++)
        	for(int j=0;j<m;j++)
            A.set(i,j,java.lang.Math.sqrt(A.get(i,j)));
        return this;
    }

    @Override
    public double get(int row, int col) throws ColumnOutOfRangeException, RowOutOfRangeException {
        if(row>=A.getRowDimension())throw new RowOutOfRangeException();
        if(col>=A.getColumnDimension())throw new ColumnOutOfRangeException();
    	return A.get(row,col);
    }

    @Override
    public Matrix getRow(int row) throws RowOutOfRangeException {
        if(row>=A.getRowDimension())throw new RowOutOfRangeException();
        NaiveMatrix M=new NaiveMatrix(1,A.getColumnDimension());
        M.A=A.getMatrix(row,row,0,A.getColumnDimension()-1);
        return M;
    }

    @Override
    public Matrix getCol(int col) throws ColumnOutOfRangeException {
        if(col>=A.getColumnDimension())throw new ColumnOutOfRangeException();
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),1);
        M.A=A.getMatrix(0,A.getRowDimension()-1,col,col);
        return M;
    }

    @Override
    public void set(int row, int col, double x) throws ColumnOutOfRangeException, RowOutOfRangeException {
        if(row>=A.getRowDimension())throw new RowOutOfRangeException();
        if(col>=A.getColumnDimension())throw new ColumnOutOfRangeException();
        A.set(row, col, x);
    }

    @Override
    public void setRow(int row, Matrix b) throws RowOutOfRangeException, DimensionNotAgreeException {
        if(row>A.getRowDimension())throw new RowOutOfRangeException();
        A.setMatrix(row,row,0,A.getColumnDimension()-1,((NaiveMatrix)b).A);
    }

    @Override
    public void setCol(int col, Matrix b) throws ColumnOutOfRangeException, DimensionNotAgreeException {
        if(col>=A.getColumnDimension())throw new ColumnOutOfRangeException();
        A.setMatrix(0,A.getRowDimension()-1,col,col,((NaiveMatrix)b).A);
    }

    @Override
    public double determinant() {
    	return A.det();
    }
    
    @Override
    public Matrix inverse() throws IrreversibleException {
        if(A.getColumnDimension()!=A.getRowDimension())throw new IrreversibleException();
    	if(abs(A.det())<1e-20)throw new IrreversibleException();
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        M.A=A.inverse();
    	return M;
    }
    
    @Override
    public Matrix transpose() {
        NaiveMatrix M=new NaiveMatrix(A.getColumnDimension(),A.getRowDimension());
        M.A=A.transpose();
    	return M;
    }

    @Override
    public double norm(Norm normName) {
    	if(normName==Matrix.FROBENIUS_NORM)return A.normF();
        if(normName==Matrix.FIRST_NORM)return A.norm1();
        return 0;
    }
    
   @Override
    public boolean equals(Object object) {
        if (!(object instanceof NaiveMatrix)) return false;
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
        ret+="}";
        return ret;
    }

    @Override
    public Matrix times(double lambda) {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        M.A=A.times(lambda);
    	return M;
    }

    @Override
    public Matrix times_assign(double lambda) {
        A.timesEquals(lambda);
        return this;
    }

    @Override
    public Matrix normalize(){
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        final double ZERO=1e-9;
        double sum=0;
        double min=0;
        for(int col=0;col<A.getColumnDimension();col++)
            for(int row=0;row<A.getRowDimension();row++)
                if(A.get(row, col)<min)min=A.get(row, col);
        for(int col=0;col<A.getColumnDimension();col++)
            for(int row=0;row<A.getRowDimension();row++) {
                if(min<0)A.set(row, col, A.get(row, col)-2*min);
                if(A.get(row, col)<ZERO)A.set(row, col, ZERO);
                sum+=A.get(row, col);
            }
        sum/=A.getColumnDimension();
        for(int col=0;col<A.getColumnDimension();col++)
            for(int row=0;row<A.getRowDimension();row++)
                M.A.set(row, col, A.get(row, col)/sum);
    	return M;
    }
    
    @Override
    public Matrix normalize_assign(){
        final double ZERO=1e-9;
        double sum=0;
        double min=0;
        for(int col=0;col<A.getColumnDimension();col++)
            for(int row=0;row<A.getRowDimension();row++)
                if(A.get(row, col)<min)min=A.get(row, col);
        for(int col=0;col<A.getColumnDimension();col++)
            for(int row=0;row<A.getRowDimension();row++) {
                if(min<0)A.set(row, col, A.get(row, col)-2*min);
                if(A.get(row, col)<ZERO)A.set(row, col, ZERO);
                sum+=A.get(row, col);
            }
        sum/=A.getColumnDimension();
        for(int col=0;col<A.getColumnDimension();col++)
            for(int row=0;row<A.getRowDimension();row++)
                A.set(row, col, A.get(row, col)/sum);
        return this;
}

    @Override
    public Matrix adjoint() throws DimensionNotAgreeException {
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
        try {
            for(int row=0;row<A.getRowDimension();row++)
                for(int col=0;col<A.getColumnDimension();col++) {
                    NaiveMatrix T=new NaiveMatrix(A.getRowDimension()-1,A.getColumnDimension()-1);
                    for(int i=0;i<A.getRowDimension();i++)
                        for(int j=0;j<A.getColumnDimension();j++) {
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

    @Override
    public double trace() throws DimensionNotAgreeException {
        if(A.getColumnDimension()!=A.getRowDimension())throw new DimensionNotAgreeException();
        double ret=0;
        for(int r=0;r<A.getColumnDimension();r++)ret+=A.get(r, r);
        return ret;
    }

    @Override
    public Matrix projectpositive() {
         final double ZERO=1e-9;
        NaiveMatrix M=new NaiveMatrix(A.getRowDimension(),A.getColumnDimension());
         for(int col=0;col<A.getColumnDimension();col++){
             for(int row=0;row<A.getRowDimension();row++){
                 if(A.get(row, col)>ZERO){
                     try {
                         M.set(row, col, A.get(row, col));
                     } catch (ColumnOutOfRangeException ex) {
                         Logger.getLogger(NaiveMatrix.class.getName()).log(Level.SEVERE, null, ex);
                     } catch (RowOutOfRangeException ex) {
                         Logger.getLogger(NaiveMatrix.class.getName()).log(Level.SEVERE, null, ex);
                     }
                 }else{
                     try {
                         M.set(row, col, ZERO);
                     } catch (ColumnOutOfRangeException ex) {
                         Logger.getLogger(NaiveMatrix.class.getName()).log(Level.SEVERE, null, ex);
                     } catch (RowOutOfRangeException ex) {
                         Logger.getLogger(NaiveMatrix.class.getName()).log(Level.SEVERE, null, ex);
                     }
                 }
             }
         }             
    	return M;
    }

    @Override
    public Matrix projectpositive_assign() {
       final double ZERO=1e-9;
        for(int col=0;col<A.getColumnDimension();col++){
            for(int row=0;row<A.getRowDimension();row++){
                if(A.get(row, col)<ZERO){
                    A.set(row, col, ZERO);
                }
            }
        }
        return this;
    }

    @Override
    public void reset() {
        final double ZERO=1e-9;
        for(int col=0;col<A.getColumnDimension();col++){
            for(int row=0;row<A.getRowDimension();row++){
                    A.set(row, col, ZERO);
            }
        }
    }

    @Override
    public Matrix normone_assign() {
        double sum;
        for(int col=0;col<A.getColumnDimension();col++){
            sum=0;
            for(int row=0;row<A.getRowDimension();row++){
                    sum+=A.get(row, col);
            }
            for(int row=0;row<A.getRowDimension();row++){
                if(sum>1e-9)
                    A.set(row, col, A.get(row, col)/sum);
            }
        }
            
        return this;
    }

    @Override
    public int Getrownum() {
        return A.getRowDimension();
    }

    @Override
    public int Getcolnum() {
        return A.getColumnDimension();
    }
}

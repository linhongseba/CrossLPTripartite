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
        for(int r=0;r<row;r++)A.add(new HashMap<>());
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
            A.add(new HashMap<>());
            A.get(r).put(r, 1.0);
        }
    }
    
    @Override
    public double get(int row, int col) throws ColumnOutOfRangeException, RowOutOfRangeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix getRow(int row) throws RowOutOfRangeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix getCol(int col) throws ColumnOutOfRangeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set(int row, int col, double x) throws ColumnOutOfRangeException, RowOutOfRangeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRow(int row, Matrix b) throws RowOutOfRangeException, DimensionNotAgreeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCol(int col, Matrix b) throws ColumnOutOfRangeException, DimensionNotAgreeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Matrix times(Matrix b) throws DimensionNotAgreeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix times(double lambda) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix cron(Matrix b) throws DimensionNotAgreeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix add(Matrix b) throws DimensionNotAgreeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix subtract(Matrix b) throws DimensionNotAgreeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix divide(Matrix b) throws DimensionNotAgreeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix sqrt() throws DimensionNotAgreeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix normalize() throws DimensionNotAgreeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double norm(Norm normName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Matrix timesNum(double b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

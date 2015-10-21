package labelinference.Matrix;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.IrreversibleException;
import labelinference.exceptions.RowOutOfRangeException;


/**
*
* @author sailw

* @since 1.8
* 
* interface announces all matrix methods
*/
public interface Matrix {
    public class Norm{}
    
    Matrix times(Matrix b) throws DimensionNotAgreeException;
    Matrix times(double lambda);
    Matrix times_assign(Matrix b) throws DimensionNotAgreeException;
    Matrix times_assign(double lambda);
    double innerProduct(Matrix b)throws DimensionNotAgreeException,ColumnOutOfRangeException,RowOutOfRangeException;
    Matrix copy();
    Matrix cron(Matrix b) throws DimensionNotAgreeException;
    Matrix cron_assign(Matrix b) throws DimensionNotAgreeException;
    Matrix add(Matrix b) throws DimensionNotAgreeException;
    Matrix add_assign(Matrix b) throws DimensionNotAgreeException;
    Matrix subtract(Matrix b) throws DimensionNotAgreeException;
    Matrix subtract_assign(Matrix b) throws DimensionNotAgreeException;
    Matrix divide(Matrix b) throws DimensionNotAgreeException;
    Matrix divide_assign(Matrix b) throws DimensionNotAgreeException;
    Matrix sqrt() throws DimensionNotAgreeException;
    Matrix sqrt_assign() throws DimensionNotAgreeException;
    double get(int row,int col) throws ColumnOutOfRangeException,RowOutOfRangeException;
    Matrix getRow(int row) throws RowOutOfRangeException;
    Matrix getCol(int col) throws ColumnOutOfRangeException;
    void set(int row,int col,double x) throws ColumnOutOfRangeException,RowOutOfRangeException;
    void setRow(int row,Matrix b) throws RowOutOfRangeException,DimensionNotAgreeException;
    void setCol(int col,Matrix b) throws ColumnOutOfRangeException,DimensionNotAgreeException;
    double determinant();
    void reset();
    int Getrownum();
    int Getcolnum();
    Matrix inverse() throws IrreversibleException;
    Matrix adjoint () throws DimensionNotAgreeException;
    Matrix transpose();
    Matrix normalize();
    Matrix normalize_assign();
    Matrix normone_assign();
    Matrix projectpositive();
    Matrix projectpositive_assign();
    double norm(Norm normName);
    double trace() throws DimensionNotAgreeException;
    
    public static final Norm FROBENIUS_NORM=new Norm();
    public static final Norm FIRST_NORM=new Norm();
}

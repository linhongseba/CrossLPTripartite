/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.IrreversibleException;
import labelinference.exceptions.RowOutOfRangeException;



/**
 *
 * @author sailw
 */
public interface Matrix {
    Matrix times(Matrix b) throws DimensionNotAgreeException;
    Matrix cron(Matrix b) throws DimensionNotAgreeException;
    Matrix add(Matrix b) throws DimensionNotAgreeException;
    Matrix subtract(Matrix b) throws DimensionNotAgreeException;
    Matrix divide(Matrix b) throws DimensionNotAgreeException;
    Matrix sqrt() throws DimensionNotAgreeException;
    double get(int row,int col) throws ColumnOutOfRangeException,RowOutOfRangeException;
    Matrix getRow(int row) throws RowOutOfRangeException;
    Matrix getCol(int col) throws ColumnOutOfRangeException;
    void set(int row,int col,double x) throws ColumnOutOfRangeException,RowOutOfRangeException;
    void setRow(int row,Matrix b) throws RowOutOfRangeException,DimensionNotAgreeException;
    void setCol(int col,Matrix b) throws ColumnOutOfRangeException,DimensionNotAgreeException;
    Matrix subMatrix(int topRow,int bottomRow,int leftCol,int rightCol) throws ColumnOutOfRangeException,RowOutOfRangeException;
    double determinant();
    Matrix inverse() throws IrreversibleException;
    Matrix transpose();
    double norm(String normName);
    
    public static final String FROBENIUS_NORM="F";
}

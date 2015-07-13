/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

/**
 *
 * @author sailw
 */
public class MatrixFactory {
    private static MatrixFactory instance=null;
    
    private MatrixFactory() {
    }

    public static MatrixFactory getInstance() {
        if(instance==null)instance=new MatrixFactory();
        return instance;
    }

    public Matrix creatMatrix(int row, int col) {
        return new NaiveMatrix(row,col);

    }

    public Matrix creatMatrix(String path) {
        return new NaiveMatrix(path);
    }

    public Matrix creatMatrix(double data[][]) {
        return new NaiveMatrix(data);

    }

    public Matrix identityMatrix(int dim) {
        return new NaiveMatrix(dim);
    }	
}

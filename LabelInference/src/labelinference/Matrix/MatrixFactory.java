package labelinference.Matrix;

/**
*
* @author sailw

* @since 1.8
* 
* a class packing all initializations
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Matrix creatMatrix(double data[][]) {
        return new NaiveMatrix(data);

    }

    public Matrix identityMatrix(int dim) {
        return new NaiveMatrix(dim);
    }	
}

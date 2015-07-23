/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import labelinference.Graph.Graph;
import labelinference.Matrix.MatrixFactory;
import labelinference.Matrix.Matrix;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw
 */
public interface LabelInference {
    Graph getResult();
    
    public static Matrix defaultLabelInit(Integer k) {
        final MatrixFactory mf=MatrixFactory.getInstance();
        Matrix label=mf.creatMatrix(k,1);
        for(int i=0;i<k;i++)try {
            label.set(i, 0, 1.0/k);
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        return label;
    }
}

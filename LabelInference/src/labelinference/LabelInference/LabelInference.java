/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import static java.lang.Math.pow;
import java.util.HashMap;
import java.util.Map;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.Matrix.MatrixFactory;
import labelinference.Matrix.Matrix;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw
 */
public interface LabelInference {
    double getResult();
    
    public static Matrix defaultLabelInit(Integer k) {
        final MatrixFactory mf=MatrixFactory.getInstance();
        Matrix label=mf.creatMatrix(k,1);
        for(int i=0;i<k;i++)try {
            label.set(i, 0, 1.0/k);
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        return label;
    }
    
    public static double objective(Graph g, Map<Vertex,Matrix> Y0, int k) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
        Double obj=0.0;
        for(int i=0;i<k;i++)
            for(int j=0;j<k;j++) {
                Map<Vertex.Type,Double> sumij=new HashMap<>();
                for(Vertex v:g.getVertices())sumij.put(v.getType(), sumij.getOrDefault(v.getType(), 0.0)+v.getLabel().get(i, 0)*v.getLabel().get(j, 0));
                obj+=2*(sumij.get(Vertex.typeA)*sumij.get(Vertex.typeB)+sumij.get(Vertex.typeA)*sumij.get(Vertex.typeC)+sumij.get(Vertex.typeC)*sumij.get(Vertex.typeB));
            }
        for(Vertex v:g.getVertices()) {
            for(Vertex u:v.getNeighbors())
                obj+=pow(v.getEdge(u)-v.getLabel().transpose().times(u.getLabel()).get(0, 0),2)-pow(v.getLabel().transpose().times(u.getLabel()).get(0, 0),2);
            if(v.isY0())obj+=pow(Y0.get(v).subtract(v.getLabel()).norm(Matrix.FROBENIUS_NORM),2);
        }
        /*for(Vertex v:g.getVertices()) {
            for(Vertex u:g.getVertices()) 
                if(v.getType()!=u.getType())
                    obj+=pow(v.getEdge(u)-v.getLabel().transpose().times(u.getLabel()).get(0, 0),2);
            if(v.isY0())obj+=pow(Y0.get(v).subtract(v.getLabel()).norm(Matrix.FROBENIUS_NORM),2);
        }*/
        return obj;
    }
}

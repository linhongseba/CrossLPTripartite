/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.Matrix.Matrix;
import labelinference.Matrix.MatrixFactory;
import labelinference.exceptions.DimensionNotAgreeException;

/**
 *
 * @author sailw
 */
public class NewMultiplicative implements LabelInference {
    private final Graph g;
    private boolean isDone;
    private final double nuance;
    private final Function<Integer,Matrix> labelInit;
    private final int k;
    private final int maxIter;
	
    public NewMultiplicative(Graph _g) {	
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        nuance=1e-4;
        maxIter=100;
        labelInit=(Integer x)->LabelInference.defaultLabelInit(x);
    }
    
    public NewMultiplicative(Graph _g,double  _nuance, int _maxIter) {
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        nuance=_nuance;
        maxIter=_maxIter;
        labelInit=(Integer x)->LabelInference.defaultLabelInit(x);
    }
    
    public NewMultiplicative(Graph _g,double  _nuance, int _maxIter, Function<Integer,Matrix> _labelInit) {
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        nuance=_nuance;
        maxIter=_maxIter;
        labelInit=_labelInit;
    }
    @Override
    public Graph getResult() {
        if(isDone)return g;
        Map<Vertex,Matrix> Y0=new HashMap<>();
        for(Vertex v:g.getVertices())
            if(v.isY0())Y0.put(v, v.getLabel().copy());
            else v.setLabel(labelInit.apply(k));
        try {
        double delta;
        int iter=0;
            do {
                delta=update(Y0)/g.getVertices().size();
                iter++;
                System.err.println(delta);
            } while(delta>nuance && iter!=maxIter);
        } catch (DimensionNotAgreeException ex) {
            Logger.getLogger(NewMultiplicative.class.getName()).log(Level.SEVERE, null, ex);
        }
        isDone=true;
        return g;
    }

    private double update(Map<Vertex, Matrix> Y0) throws DimensionNotAgreeException {
        MatrixFactory mf=MatrixFactory.getInstance();
        Map<Vertex.Type,Matrix> A=new HashMap<>();
        Matrix emptyMat=mf.creatMatrix(k, k);
        for(Vertex.Type type:Vertex.types)
            for(Vertex v:g.getVertices(v->v.getType()!=type))
                A.put(type, A.getOrDefault(type, emptyMat).add(v.getLabel().times(v.getLabel().transpose())));
            
        double delta=0;
        for(Vertex v:g.getVertices()) {
            Matrix label= mf.creatMatrix(k, 1);
            for(Vertex u:v.getNeighbors())label=label.add(u.getLabel().times(v.getEdge(u)));
            if(v.isY0())label=label.add(Y0.get(v)).divide(A.get(v.getType()).times(v.getLabel()).add(v.getLabel()));
            else label=label.divide(A.get(v.getType()).times(v.getLabel()));
            label=v.getLabel().cron(label.sqrt()).normalize();
            delta+=v.getLabel().subtract(label).norm(Matrix.FIRST_NORM);
            v.setLabel(label);
        }
        return delta;
    }
}

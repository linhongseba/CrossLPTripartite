/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.Matrix.Matrix;
import labelinference.Matrix.MatrixFactory;
import labelinference.exceptions.DimensionNotAgreeException;

/**
 *
 * @author sailw
 */
public class NewMultiplicative extends AbstractLabelInference implements LabelInference {	
    public NewMultiplicative(Graph _g) {	
        super(_g);
    }
    
    public NewMultiplicative(Graph _g, Function<Integer,Matrix> _labelInit) {
        super(_g,_labelInit);
    }

    @Override
    protected double update(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex, Matrix> Y0) throws DimensionNotAgreeException {
        MatrixFactory mf=MatrixFactory.getInstance();
        Map<Vertex.Type,Matrix> A=new HashMap<>();
        Matrix emptyMat=mf.creatMatrix(k, k);
        for(Vertex.Type type:Vertex.types)
            for(Vertex u:candS)
                if(u.getType()!=type)
                    A.put(type, A.getOrDefault(type, emptyMat).add(u.getLabel().times(u.getLabel().transpose())));
        
        double delta=0;
        Map<Vertex, Matrix> Y=new HashMap<>();
        for(Vertex u:cand) {
            Matrix label= mf.creatMatrix(k, 1);
            for(Vertex v:u.getNeighbors())label=label.add(v.getLabel().times(u.getEdge(v)));
            if(u.isY0())label=label.times(1.0/maxE).add(Y0.get(u)).divide(A.get(u.getType()).times(u.getLabel()).add(u.getLabel()));
            else label=label.times(1.0/maxE).divide(A.get(u.getType()).times(u.getLabel()));
            //System.err.println(A.get(u.getType()).times(u.getLabel()));
            label=u.getLabel().cron(label.sqrt()).normalize();
            delta+=u.getLabel().subtract(label).norm(Matrix.FIRST_NORM);
            Y.put(u, label);
        }
        for(Vertex u:cand)u.setLabel(Y.get(u));
        return delta;
    }
}

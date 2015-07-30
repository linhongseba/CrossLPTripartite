/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import java.util.Collection;
import labelinference.Matrix.MatrixFactory;
import labelinference.Matrix.Matrix;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;

import labelinference.exceptions.DimensionNotAgreeException;

/**
 * @author Hermes777, SailWhite
 */
public class BlockCoordinateDescent extends AbstractLabelInference implements LabelInference {
    public BlockCoordinateDescent(Graph _g) {	
        super(_g);
    }
    
    public BlockCoordinateDescent(Graph _g, Function<Integer,Matrix> _labelInit) {
        super(_g,_labelInit);
    }
    
    @Override
    protected double update(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0) throws DimensionNotAgreeException {
        final MatrixFactory mf=MatrixFactory.getInstance();
        final Map<Vertex.Type,Matrix> A=new HashMap<>();
        final Map<Vertex.Type,Matrix> Ay0=new HashMap<>();
        Matrix emptyMat=mf.creatMatrix(k, k);
        Matrix identity=mf.identityMatrix(k);
        for(Vertex.Type type:Vertex.types) {
            for(Vertex v:candS)
                if(v.getType()!=type)
                    A.put(type, A.getOrDefault(type, emptyMat).add(v.getLabel().times(v.getLabel().transpose())));
            Ay0.put(type, A.get(type).add(identity).adjoint());
            A.put(type, A.get(type).adjoint());
        }
        
        double delta=0;
        Map<Vertex, Matrix> Y=new HashMap<>();
        for(Vertex u:cand) {
            Matrix label=mf.creatMatrix(k,1);
            for(Vertex v:u.getNeighbors())
                label=label.add(v.getLabel().times(u.getEdge(v)));
            if(u.isY0()) label=Ay0.get(u.getType()).times(label.times(1.0/maxE).add(Y0.get(u))).normalize();
            else label=A.get(u.getType()).times(label).normalize();
            delta+=u.getLabel().subtract(label).norm(Matrix.FIRST_NORM);
            Y.put(u, label);
        }
        for(Vertex v:cand)v.setLabel(Y.get(v));
        return delta;
    }
}

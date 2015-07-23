/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import labelinference.Matrix.MatrixFactory;
import labelinference.Matrix.Matrix;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.IrreversibleException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author Hermes777, SailWhite
 * 
 * @linhong: 
 * 1)please change hard coded number of classes 2 to a parameter:)
 * 2)the convergence nuance might reduce to a smaller value or set as a parameter too:)
 * 3)increse the max number of iterations or set as a parameter too:)
 * 
 * 
 */
public class BlockCoordinateDescent implements LabelInference {
    private final Graph g;
    private boolean isDone;
    private final double nuance;
    private final Function<Integer,Matrix> labelInit;
    private final int k;
    private final int maxIter;
	
    public BlockCoordinateDescent(Graph _g, int _k) {	
        g=_g;
        k=_k;
        isDone=false;
        nuance=1e-4;
        maxIter=100;
        labelInit=(Integer x)->LabelInference.defaultLabelInit(x);
    }
    
    public BlockCoordinateDescent(Graph _g, int _k,double  _nuance, int _maxIter) {
        g=_g;
        k=_k;
        isDone=false;
        nuance=_nuance;
        maxIter=_maxIter;
        labelInit=(Integer x)->LabelInference.defaultLabelInit(x);
    }
    
    public BlockCoordinateDescent(Graph _g, int _k,double  _nuance, int _maxIter, Function<Integer,Matrix> _labelInit) {
        g=_g;
        k=_k;
        isDone=false;
        nuance=_nuance;
        maxIter=_maxIter;
        labelInit=_labelInit;
    }
    
    private double update(Map<Vertex,Matrix> Y0) throws DimensionNotAgreeException,ColumnOutOfRangeException, RowOutOfRangeException, IrreversibleException {
        final MatrixFactory mf=MatrixFactory.getInstance();
        final Map<Vertex.Type,Double> A=new HashMap<>();
        final Map<Vertex.Type,Double> Ay0=new HashMap<>();
        A.put(Vertex.typeA, new Double(0));
        A.put(Vertex.typeB, new Double(0));
        A.put(Vertex.typeC, new Double(0));
        
        for(Vertex.Type type:A.keySet()) {
            for(Vertex v:g.getVertices()) 
                if(v.getType()!=type)
                    A.put(type, A.get(type)+v.getLabel().transpose().times(v.getLabel()).get(0, 0));
            Ay0.put(type, 1/(A.get(type)+1));	
            A.put(type, 1/A.get(type));
        }
        double delta=0;
        for(Vertex u:g.getVertices()) {
            Matrix label=mf.creatMatrix(k,1);
            for(Vertex v:u.getNeighbors())
                label=label.add(v.getLabel().timesNum(u.getEdge(v)));
            if(u.isY0()) label=label.add(Y0.get(u)).times(Ay0.get(u.getType())).normalize();
            else label=label.times(A.get(u.getType())).normalize();
            delta+=u.getLabel().subtract(label).norm(Matrix.FIRST_NORM);
            u.setLabel(label);
        }
        return delta;
    }
    
    @Override
    public Graph getResult(){
        if(isDone)return g;
        MatrixFactory mf=MatrixFactory.getInstance();
        try {
            final Map<Vertex,Matrix> Y0=new HashMap<>();
            for (Vertex v : g.getVertices()) {
                if(v.isY0())Y0.put(v, v.getLabel().copy());
                else v.setLabel(labelInit.apply(k));
            }

            double delta;
            int iter=0;
            do {
                delta=update(Y0)/g.getVertices().size();
                iter++;
                System.out.println(delta);
            } while(delta>nuance && iter!=maxIter);

            for(Vertex v:g.getVertices())
                if(v.isY0())v.setLabel(Y0.get(v));
        } catch (ColumnOutOfRangeException | DimensionNotAgreeException | IrreversibleException | RowOutOfRangeException ex) {
            Logger.getLogger(LabelPropagation.class.getName()).log(Level.SEVERE, null, ex);
        }
        isDone=true;
        return g;
    }
}

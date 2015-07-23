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

/**
 *
 * @author sailw
 */
public class LabelPropagation implements LabelInference {
    
    private final Graph g;
    private boolean isDone;
    private final double alpha;
    private final double nuance;
    private final Function<Integer,Matrix> labelInit;
    private final int k;
    private final int maxIter;
    
    public LabelPropagation(Graph _g, int _k) {
        g=_g;
        k=_k;
        isDone=false;
        alpha=0.2;
        nuance=1e-4;
        maxIter=100;
        labelInit=(Integer x)->LabelInference.defaultLabelInit(x);
    }
    
    public LabelPropagation(Graph _g, int _k, double _alpha,double  _nuance, int _maxIter) {
        g=_g;
        k=_k;
        isDone=false;
        alpha=_alpha;
        nuance=_nuance;
        maxIter=_maxIter;
        labelInit=(Integer x)->LabelInference.defaultLabelInit(x);
    }
    
    public LabelPropagation(Graph _g, int _k, double _alpha,double _nuance, int _maxIter, Function<Integer,Matrix> _labelInit) {
        g=_g;
        k=_k;
        isDone=false;
        alpha=_alpha;
        nuance=_nuance;
        maxIter=_maxIter;
        labelInit=_labelInit;
    }
    
    @Override
    public Graph getResult() {
        if(isDone)return g;
        final MatrixFactory mf=MatrixFactory.getInstance();
        try {
            for(Vertex v:g.getVertices()) {
                Matrix label=mf.creatMatrix(k, 2);
                if(v.isY0())label.setCol(0, v.getLabel());
                else label.setCol(0, labelInit.apply(k));
                v.setLabel(label);
            }

            int iter=0;
            double delta;
            do {
                delta=update()/g.getVertices().size();
                iter++;
                System.out.println(delta);
            }while(delta>nuance && iter!=maxIter);
            
            for(Vertex v:g.getVertices())v.setLabel(v.getLabel().getCol(0));
        } catch (ColumnOutOfRangeException | DimensionNotAgreeException | InterruptedException ex) {
            Logger.getLogger(LabelPropagation.class.getName()).log(Level.SEVERE, null, ex);
        }
        isDone=true;
        return g;
    }

    private double update() throws DimensionNotAgreeException, ColumnOutOfRangeException, InterruptedException {
        final MatrixFactory mf=MatrixFactory.getInstance();
        final Map<Vertex,Map<Vertex.Type,Matrix>> cache=new HashMap<>();
        for(Vertex u:g.getVertices()) {
            Map<Vertex.Type,Matrix> value=new HashMap<>();
            value.put(Vertex.typeA, mf.creatMatrix(k, 1));
            value.put(Vertex.typeB, mf.creatMatrix(k, 1));
            value.put(Vertex.typeC, mf.creatMatrix(k, 1));
            for(Vertex v:u.getNeighbors())
                value.put(v.getType(), value.get(v.getType()).add(v.getLabel().getCol(0).times(v.getEdge(u)/v.sumE())));
            cache.put(u, value);
        }
        
        double delta=0;
        for(Vertex u:g.getVertices()) {
                if(u.isY0())continue;
                Matrix label=u.getLabel();
                Matrix a=mf.creatMatrix(k, 1);
                Matrix b=mf.creatMatrix(k, 1);
                try {
                    for(Vertex v:u.getNeighbors()) {
                        a=a.add(v.getLabel().getCol(0).times(u.getEdge(v)));
                        b=b.add(cache.get(v).get(u.getType()).subtract(u.getLabel().getCol(0).times(u.getEdge(v)/u.sumE())).times(1.0/v.sumE()));
                    }
                    label.setCol(1, a.orthonormalize().times(1-alpha).add(b.orthonormalize().times(alpha)));
                } catch (ColumnOutOfRangeException | DimensionNotAgreeException ex) {
                    Logger.getLogger(LabelPropagation.class.getName()).log(Level.SEVERE, null, ex);
                }
                delta+=u.getLabel().getCol(1).subtract(u.getLabel().getCol(0)).norm(Matrix.FIRST_NORM);
        }
        
        for(Vertex u:g.getVertices()) {
            if(!u.isY0())u.getLabel().setCol(0, u.getLabel().getCol(1));  
        }
        return delta;
    }
}
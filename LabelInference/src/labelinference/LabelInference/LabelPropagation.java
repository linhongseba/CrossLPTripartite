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
 *
 * @author sailw
 */
public class LabelPropagation extends AbstractLabelInference implements LabelInference {
    double alpha;
    
    public LabelPropagation(Graph _g, double _alpha) {
        super(_g);
    }

    public LabelPropagation(Graph _g, double _alpha, Function<Integer,Matrix> _labelInit) {
        super(_g,_labelInit);
        alpha=_alpha;
    }

    @Override
    protected double update(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0) throws DimensionNotAgreeException {
        final MatrixFactory mf=MatrixFactory.getInstance();
        final Map<Vertex,Map<Vertex.Type,Matrix>> cache=new HashMap<>();
        Matrix emptyMat=mf.creatMatrix(k, 1);
        for(Vertex u:candS) {
            Map<Vertex.Type,Matrix> value=new HashMap<>();
            for(Vertex v:u.getNeighbors())
                value.put(v.getType(), value.getOrDefault(v.getType(), emptyMat).add(v.getLabel().times(v.getEdge(u)/v.sumE())));
            cache.put(u, value);
        }

        double delta=0;
        Map<Vertex, Matrix> Y=new HashMap<>();
        for(final Vertex u:cand) {
            if(u.isY0())continue;
            Matrix label;
            Matrix a=mf.creatMatrix(k, 1);
            Matrix b=mf.creatMatrix(k, 1);
            for(Vertex v:u.getNeighbors()) {
                a=a.add(v.getLabel().times(u.getEdge(v)));
                b=b.add(cache.get(v).get(u.getType()).subtract(u.getLabel().times(u.getEdge(v)/u.sumE())).times(u.getEdge(v)/v.sumE()));
            }
            label=a.normalize().times(1-alpha).add(b.normalize().times(alpha));
            delta+=label.subtract(u.getLabel()).norm(Matrix.FIRST_NORM);
            Y.put(u, label);
        }
        for(Vertex v:cand)if(!v.isY0())v.setLabel(Y.get(v));
        return delta;
    }
}
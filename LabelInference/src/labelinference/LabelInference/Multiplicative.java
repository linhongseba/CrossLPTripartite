/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
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
public class Multiplicative extends AbstractLabelInference implements LabelInference {
    public Multiplicative(Graph _g) {	
        super(_g);
    }
    
    public Multiplicative(Graph _g, BiConsumer<Matrix,Integer> _labelInit) {
        super(_g,_labelInit);
    }

    @Override
    protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS) throws DimensionNotAgreeException {
        MatrixFactory mf=MatrixFactory.getInstance();
        Map<Vertex.Type,Map<Vertex.Type,Matrix>> dBup=new HashMap<>();
        Map<Vertex.Type,Map<Vertex.Type,Matrix>> dBdown=new HashMap<>();
        Map<Vertex.Type,Matrix> product=new HashMap<>();
        for(Vertex.Type t0:Vertex.types) {
            dBup.put(t0, new HashMap<>());
            product.put(t0, mf.creatMatrix(k,k));
            for(Vertex.Type t1:Vertex.types)
                dBup.get(t0).put(t1,MatrixFactory.getInstance().creatMatrix(k,k));
        }
        
        for(Vertex u:cand)for(Vertex v:u.getNeighbors())
            dBup.get(u.getType()).put(v.getType(), dBup.get(u.getType()).get(v.getType()).add(u.getLabel().times(v.getLabel().transpose()).times(u.getEdge(v))));
        for(Vertex u:candS)product.put(u.getType(), product.get(u.getType()).add(u.getLabel().times(u.getLabel().transpose())));
        for(Vertex.Type t0:Vertex.types)for(Vertex.Type t1:Vertex.types)if(t0!=t1)
            B.get(t0).put(t1, B.get(t0).get(t1).cron(dBup.get(t0).get(t1).divide(product.get(t0).times(B.get(t0).get(t1)).times(product.get(t1))).times(1.0/maxE).sqrt()));
    }
    
    @Override
    protected Map<Vertex, Matrix> updateY(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex, Matrix> Y0) throws DimensionNotAgreeException {
        MatrixFactory mf=MatrixFactory.getInstance();
        Map<Vertex.Type,Matrix> A=new HashMap<>();
        Matrix emptyMat=mf.creatMatrix(k, k);
        for(Vertex.Type type:Vertex.types)
            for(Vertex u:candS)
                if(u.getType()!=type)
                    A.put(type, A.getOrDefault(type, emptyMat).add(B.get(type).get(u.getType()).times(u.getLabel()).times(u.getLabel().transpose()).times(B.get(type).get(u.getType()).transpose())));

        Map<Vertex, Matrix> Y=new HashMap<>();
        for(Vertex u:cand) {
            Matrix label= mf.creatMatrix(k, 1);
            for(Vertex v:u.getNeighbors())
                label=label.add(B.get(u.getType()).get(v.getType()).times(v.getLabel()).times(u.getEdge(v)));
            if(u.isY0())label=label.times(1.0/maxE).add(Y0.get(u)).divide(A.get(u.getType()).times(u.getLabel()).add(u.getLabel()));
            else label=label.times(1.0/maxE).divide(A.get(u.getType()).times(u.getLabel()));
            label=u.getLabel().cron(label.sqrt()).normalize();
            Y.put(u, label);
        }
        return Y;
    }
}

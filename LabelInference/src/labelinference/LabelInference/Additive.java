/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import java.util.Collection;
import labelinference.Matrix.MatrixFactory;
import labelinference.Matrix.Matrix;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 * @author Hermes777, SailWhite
 */
public class Additive extends AbstractLabelInference implements LabelInference {
    private double alpha;
    private double alphaNext;
    
    public Additive(Graph _g) {	
        super(_g);
        alpha=1;
    }
    
    public Additive(Graph _g, BiConsumer<Matrix,Integer> _labelInit) {
        super(_g,_labelInit);
        alpha=1;
    }
    
    @Override
    protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS) throws DimensionNotAgreeException {
        MatrixFactory mf=MatrixFactory.getInstance();
        Map<Vertex.Type,Map<Vertex.Type,Matrix>> dBleft=new HashMap<>();
        Map<Vertex.Type,Matrix> product=new HashMap<>();

        for(Vertex.Type t0:Vertex.types) {
            dBleft.put(t0, new HashMap<>());
            product.put(t0, mf.creatMatrix(k,k));
            for(Vertex.Type t1:Vertex.types)
                dBleft.get(t0).put(t1,MatrixFactory.getInstance().creatMatrix(k,k));
        }
        for(Vertex u:cand)for(Vertex v:u.getNeighbors())
            dBleft.get(u.getType()).put(v.getType(), dBleft.get(u.getType()).get(v.getType()).add(u.getLabel().times(v.getLabel().transpose()).times(u.getEdge(v))));

        for(Vertex u:candS)product.put(u.getType(), product.get(u.getType()).add(u.getLabel().times(u.getLabel().transpose())));
        alphaNext=(1+sqrt(4*alpha*alpha+1))/2;
        for(Vertex.Type t0:Vertex.types)for(Vertex.Type t1:Vertex.types)if(t0!=t1) {
            double L=(product.get(t1).times(product.get(t0))).norm(Matrix.FROBENIUS_NORM);
            double etab=(alphaNext+alpha-1)/alphaNext/L;
            Matrix dB=dBleft.get(t0).get(t1).times(1/maxE).subtract(product.get(t0).times(B.get(t0).get(t1)).times(product.get(t1)));
            B.get(t0).put(t1, B.get(t0).get(t1).add(dB.times(etab)));
        }
    }
    
    @Override
    protected Map<Vertex, Matrix> updateY(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0) throws DimensionNotAgreeException {
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
            double L=A.get(u.getType()).norm(Matrix.FROBENIUS_NORM)+u.sumE()/maxE;
            double eta=(alphaNext+alpha-1)/alphaNext/L;
            for(Vertex v:u.getNeighbors())
                label=label.add(B.get(u.getType()).get(v.getType()).times(v.getLabel()).times(u.getEdge(v)/maxE));
            Matrix t=A.get(u.getType()).times(u.getLabel()).times(2);
            label=label.subtract(t.times(label.norm(Matrix.FIRST_NORM)/t.norm(Matrix.FIRST_NORM)));
            if(u.isY0())label=label.add(Y0.get(u)).subtract(u.getLabel());
            label=u.getLabel().add(label.times(2*eta)).normalize();
            Y.put(u, label);
        }
        alpha=alphaNext;
        return Y;
    }
    
    @Override
    public void increase(Collection<Vertex> deltaGraph, int maxIter, double nuance, double a, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {
        alpha=1;
        alphaNext=1;
        super.increase(deltaGraph, maxIter, nuance, a, disp);
    }
}
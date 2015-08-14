/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

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
 * 
 * @since 1.8
 * 
 * TODO To implement the update of Y and B matrix in AR
 */
public class Additive extends AbstractLabelInference implements LabelInference {
    
	/** alpha:the step size of learning*/	    
    private double alpha;
    private double alphaNext;
    
    /**
     * @param _g:initial graph g with _g
	 */	    
    public Additive(Graph _g) {	
        super(_g);
        alpha=1;
    }
    
    /**
     * @param _g:initial graph g with _g
	 * @param _labelInit: initial labeled vertices
	 */	    
    public Additive(Graph _g, BiConsumer<Matrix,Integer> _labelInit) {
        super(_g,_labelInit);
        alpha=1;
    }
    
    
    /**
     * @param cand:the candidate graph
	 * @param candS:the next state of candidate graph
	 */
    @Override
    protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS) throws DimensionNotAgreeException {
        MatrixFactory mf=MatrixFactory.getInstance();
        Map<Vertex.Type,Map<Vertex.Type,Matrix>> dBup=new HashMap<>();
        Map<Vertex.Type,Map<Vertex.Type,Matrix>> dBdown=new HashMap<>();
        Map<Vertex.Type,Map<Vertex.Type,Matrix>> L=new HashMap<>();
        for(Vertex.Type t0:Vertex.types) {
            dBup.put(t0, new HashMap<>());
            dBdown.put(t0, new HashMap<>());
            L.put(t0, new HashMap<>());
            for(Vertex.Type t1:Vertex.types) {
                dBup.get(t0).put(t1,MatrixFactory.getInstance().creatMatrix(k,k));
                dBdown.get(t0).put(t1,MatrixFactory.getInstance().creatMatrix(k,k));
                L.get(t0).put(t1,MatrixFactory.getInstance().creatMatrix(k,k));
            }
        }
        for(Vertex u:cand)for(Vertex v:u.getNeighbors()) {
            dBup.get(u.getType()).put(v.getType(), dBup.get(u.getType()).get(v.getType()).add(u.getLabel().times(v.getLabel().transpose()).times(u.getEdge(v))));
            dBdown.get(u.getType()).put(v.getType(), dBdown.get(u.getType()).get(v.getType()).add(u.getLabel().times(u.getLabel().transpose()).times(B.get(u.getType()).get(v.getType())).times(v.getLabel()).times(v.getLabel().transpose()).times(u.getEdge(v))));
            L.get(u.getType()).put(v.getType(), L.get(u.getType()).get(v.getType()).add(u.getLabel().times(u.getLabel().transpose()).times(v.getLabel()).times(v.getLabel().transpose()).times(u.getEdge(v))));
        }
        //dBup_{tt'}=\Sigma{(Y(u)^T*Y(v)*G(u,v))} (u \in t, v \in t')
        //dBdown_{tt'}=\Sigma{(Y(u)*Y(u)^T*B(u,v)*Y(v)*Y(v)^T)} (u \in t, v \in t')
        //L_{tt'}=\Sigma{Y(u)*Y(u)^T*Y(v)*Y(v)^T*G(u,v)}(u\in t,v\in t')

        alphaNext=(1+sqrt(4*alpha*alpha+1))/2;
        for(Vertex.Type t0:Vertex.types)for(Vertex.Type t1:Vertex.types)if(t0!=t1) {
            double etab=(alphaNext+alpha-1)/alphaNext/L.get(t0).get(t1).norm(Matrix.FROBENIUS_NORM);
            //\eta=\frac{alphaNext+alpha-1}{alphaNext*L_{tt'}}
            B.get(t0).put(t1, B.get(t0).get(t1).add(dBup.get(t0).get(t1).subtract(dBdown.get(t0).get(t1)).times(etab/maxE)));
            //B_{tt'}=B_{tt'}+\eta_b(dBup_{tt'}-dBdown_{tt'})/maxE
            System.out.println(B.get(t0).get(t1));
        }
    }
    
    
    /**
     * @param cand:the candidate graph
	 * @param candS:the next state of candidate graph
	 * @param Y0: the initialized label
	 */
    @Override
    protected Map<Vertex, Matrix> updateY(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0) throws DimensionNotAgreeException {
        MatrixFactory mf=MatrixFactory.getInstance();
        Map<Vertex.Type,Matrix> A=new HashMap<>();
        Matrix emptyMat=mf.creatMatrix(k, k);
        for(Vertex.Type type:Vertex.types)
            for(Vertex u:candS)
                if(u.getType()!=type)
                    A.put(type, A.getOrDefault(type, emptyMat).add(B.get(type).get(u.getType()).times(u.getLabel()).times(u.getLabel().transpose()).times(B.get(type).get(u.getType()).transpose())));
        //A_t=\Sigma{B_{tt(u)}*Y(u)*Y(u)^T*B_{tt(u)}^T} (t(u)\neq{t} )
        
        Map<Vertex, Matrix> Y=new HashMap<>();
        for(Vertex u:cand) {
            Matrix label= mf.creatMatrix(k, 1);
            double L=A.get(u.getType()).norm(Matrix.FROBENIUS_NORM);
            double eta=(alphaNext+alpha-1)/alphaNext/L;
            //\eta=\frac{alphaNext+alpha-1}{alphaNext*\|A_{t(u)}\|_F}
            
            for(Vertex v:u.getNeighbors())
                label=label.add(B.get(u.getType()).get(v.getType()).times(v.getLabel()).times(u.getEdge(v)));
            Matrix t=A.get(u.getType()).times(u.getLabel()).times(2);
            label=label.subtract(t.times(label.norm(Matrix.FIRST_NORM)/t.norm(Matrix.FIRST_NORM))).times(1/maxE);
            if(u.isY0())label=label.add(Y0.get(u)).subtract(u.getLabel());
            label=u.getLabel().add(label.times(2*eta)).normalize();
            //Y(u)=\|Y(u)+2\eta*(\Sigma{B_{t(u)t(v)}*Y(v)*G(u,v)}-\frac{2*A_{t(u)}*Y(u)*\|Y(u)\|}{\|2*A_{t(u)}*Y(u)\|*maxE}+1_{YL}*(Y0(u)-Y(u)))\|
            Y.put(u, label);
        }
        alpha=alphaNext;
        return Y;
    }
    
    
    /**
     * TODO To initialize alpha and alphaNext in every increment procedure
	 */
    @Override
    public void increase(Collection<Vertex> deltaGraph, int maxIter, double nuance, double a, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {
        alpha=1;
        alphaNext=1;
        super.increase(deltaGraph, maxIter, nuance, a, disp);
    }
}
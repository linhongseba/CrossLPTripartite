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
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.exceptions.ColumnOutOfRangeException;

import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 * @author Hermes777, SailWhite
 */
public class Additive extends AbstractLabelInference implements LabelInference {

    double alpha;
    
    public Additive(Graph _g) {	
        super(_g);
        alpha=0.0000001;
    }
    
    public Additive(Graph _g, Function<Integer,Matrix> _labelInit) {
        super(_g,_labelInit);
        alpha=0.0000001;
    }
    
    @Override
    protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS) throws DimensionNotAgreeException {
        MatrixFactory mf=MatrixFactory.getInstance();
        Map<Vertex.Type,Map<Vertex.Type,Matrix>> dBleft=new HashMap<>();
        Map<Vertex.Type,Map<Vertex.Type,Matrix>> dBdown=new HashMap<>();
        Map<Vertex.Type,Matrix> proc=new HashMap<>();

        for(Vertex.Type t0:Vertex.types) {
            dBleft.put(t0, new HashMap<>());
            proc.put(t0, mf.creatMatrix(k,k));
            for(Vertex.Type t1:Vertex.types) {
                dBleft.get(t0).put(t1,MatrixFactory.getInstance().creatMatrix(k,k));
            }
        }
        
        for(Vertex u:cand)for(Vertex v:u.getNeighbors())
            dBleft.get(u.getType()).put(v.getType(), dBleft.get(u.getType()).get(v.getType()).add(u.getLabel().times(v.getLabel().transpose()).times(u.getEdge(v))));

        
        for(Vertex u:candS)proc.put(u.getType(), proc.get(u.getType()).add(u.getLabel().times(u.getLabel().transpose())));

        for(Vertex.Type t0:Vertex.types)for(Vertex.Type t1:Vertex.types)if(t0!=t1) {

        	
            double L=(proc.get(t1).times(proc.get(t0))).norm(Matrix.FROBENIUS_NORM);
            double alphaNext=(1+java.lang.Math.sqrt(4*alpha*alpha+1))/2;
            double etab=(alphaNext+alpha-1)/alphaNext/L;
            
            //Matrix dB=dBleft.get(t0).get(t1).times(1/maxE).subtract(proc.get(t0).times(B.get(t0).get(t1)).times(proc.get(t1)));
            Matrix dB=dBleft.get(t0).get(t1).subtract(proc.get(t0).times(B.get(t0).get(t1)).times(proc.get(t1)));
            //System.out.println(dBleft.get(t0).get(t1).times(1/maxE));
            //B.get(t0).put(t1, B.get(t0).get(t1).add(dB.times(eta/B.get(t0).get(t1).norm(Matrix.FIRST_NORM))));
            B.get(t0).put(t1, B.get(t0).get(t1).add(dB.times(etab)));


            double min=0;
            try {
                for(int row=0;row<k;row++)
                    for(int col=0;col<k;col++) {
                        if(B.get(t0).get(t1).get(row, col)<min)min=B.get(t0).get(t1).get(row, col);
                    }
                if(min<1e-9){
                    for(int row=0;row<k;row++)
                        for(int col=0;col<k;col++)
                            B.get(t0).get(t1).set(row, col, B.get(t0).get(t1).get(row, col)-2*min);
                }
                //B.get(t0).get(t1).normalize();
            } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
            System.out.println(B.get(t0).get(t1));
            System.out.println("etab="+etab);
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

        double alphaNext=(1+java.lang.Math.sqrt(4*alpha*alpha+1))/2;
        
        Map<Vertex, Matrix> Y=new HashMap<>();
        for(Vertex u:cand) {
            Matrix label= mf.creatMatrix(k, 1);
            
            double L=A.get(u.getType()).norm(Matrix.FROBENIUS_NORM);
            double eta=(alphaNext+alpha-1)/alphaNext/L;
            
            for(Vertex v:u.getNeighbors())
                label=label.add(B.get(u.getType()).get(v.getType()).times(v.getLabel()).times(u.getEdge(v)));
            Matrix t=A.get(u.getType()).times(u.getLabel()).times(2);
            label=label.times(1/maxE).subtract(t);
            if(u.isY0())label=label.add(Y0.get(u)).subtract(u.getLabel());
            //System.out.println(label);
            label=u.getLabel().add(label.times(2*eta)).normalize();
            Y.put(u, label);
        }
        alpha=alphaNext;
        //eta*=0.95;
        System.out.println("alpha="+alpha);
        return Y;
    }
}
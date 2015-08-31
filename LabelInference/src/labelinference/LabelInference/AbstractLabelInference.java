package labelinference.LabelInference;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.Matrix.Matrix;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;
import static labelinference.LabelInference.LabelInference.*;
import labelinference.Matrix.MatrixFactory;

/**
*
* @author sailw
* @since 1.8
* The framework of the overall procedure of incremental and non-incremental algorithms
* @see labelinference.Experiment.class#main(String[])
* 
*/
public abstract class AbstractLabelInference implements LabelInference{
    protected final Graph g;                                
    protected boolean isDone;                               
    protected final BiConsumer<Matrix,Integer> labelInit;    
    protected final int k; //the variable denotes the number of clusters
    protected double maxE=0; //the variable denotes the max number of edges
    protected Map<Vertex.Type,Map<Vertex.Type,Matrix>> B=new HashMap<>();
    
    /**
    * 
    * @param _g:initial graph g with _g
    */	
    public AbstractLabelInference(Graph _g) {	
        this(_g,LabelInference.defaultLabelInit);
    }
    
    /**
    * 
    * @param _g:initial graph g with _g
    * @param _labelInit: initial labeled vertices
    */	    
    public AbstractLabelInference(Graph _g, BiConsumer<Matrix,Integer> _labelInit) {
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        labelInit=_labelInit;
        for(Vertex.Type t0:Vertex.types) {
            B.put(t0, new HashMap<>());
            for(Vertex.Type t1:Vertex.types)
                //B.get(t0).put(t1,MatrixFactory.getInstance().identityMatrix(k));
                B.get(t0).put(t1,MatrixFactory.getInstance().creatMatrix(k,k));
        }
        for(Vertex u:g.getVertices()){
            if(u.isY0()==false)
                continue;
            for(Vertex v:u.getNeighbors()) {
                if(v.isY0()==false)
                    continue;
                Matrix temp=B.get(u.getType()).get(v.getType());
                for(int row=0;row<k;row++){
                    for(int col=0;col<k;col++){
                        try {
                            temp.set(row, col, temp.get(row, col)+u.getLabel().get(row, 0)*v.getLabel().get(col, 0));
                        } catch (ColumnOutOfRangeException ex) {
                            Logger.getLogger(AbstractLabelInference.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (RowOutOfRangeException ex) {
                            Logger.getLogger(AbstractLabelInference.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                B.get(u.getType()).put(v.getType(), temp);
            }
        }
        for(Vertex.Type t0:Vertex.types) {
            for(Vertex.Type t1:Vertex.types)
                //B.get(t0).put(t1,MatrixFactory.getInstance().identityMatrix(k));
                B.get(t0).put(t1,B.get(t0).get(t1).normalize());
        }
    }
    
    /**
    * 
    * @param maxIter: the max iterations times
    * @param nuance: a tiny number control when the procedure ends
    * @param disp: a code choose what to display
    * TODO To implement the overall procedure of the basic non-incremental algorithms
    */	
    @Override
    public void getResult(int maxIter, double nuance, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {
        if(isDone)return;                                        
        final Map<Vertex,Matrix> Y0=init(g.getVertices(),g.getVertices(), labelInit, g.getNumLabels());   
        double timeUsed=0;                                                                
        double delta; //the variable denotes the difference between Y and last produced Y.
        int iter=0; //the variable controls the iteration times.
        LabelInference.infoDisplay(disp&(DISP_ITER|DISP_LABEL|DISP_OBJ), iter, 0, 0, g.getVertices(),g.getVertices(), Y0,B,k);
        do {
            long nTime=System.nanoTime();
            long mTime=System.currentTimeMillis();
            updateB(g.getVertices(),g.getVertices());
            delta=0;
            Map<Vertex, Matrix> Y=updateY(g.getVertices(),g.getVertices(),Y0);
            for(Vertex u:g.getVertices()) {
                delta+=u.getLabel().subtract(Y.get(u)).norm(Matrix.FIRST_NORM);
                u.setLabel(Y.get(u));
            }
            delta/=g.getVertices().size();
            nTime=System.nanoTime()-nTime;
            mTime=System.currentTimeMillis()-mTime;
            timeUsed+=max(mTime,nTime/1000000.0);
            iter++;
            LabelInference.infoDisplay(disp&~DISP_TIME&~DISP_B&~(iter<10||iter%10==0?0:DISP_OBJ), iter, delta, timeUsed,g.getVertices(),g.getVertices(), Y0,B,k);
        } while(delta>nuance && iter!=maxIter);
        LabelInference.infoDisplay(disp&(DISP_TIME|DISP_B), iter, delta, timeUsed, g.getVertices(),g.getVertices(), Y0,B,k);
        isDone=true;
    }
    
    /**
    * 
    * @param deltaGraph: the additional graph
    * @param maxIter: the max iterations times
    * @param nuance: a tiny number control when the procedure ends
    * @param a: confidence level
    * @param disp: a code choose what to display
    * implement the overall procedure of the basic non-incremental algorithms
    */	
    @Override
    public void increase(Collection<Vertex> deltaGraph, int maxIter, double nuance, double a, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {
        if(!isDone)getResult(maxIter, nuance, disp);
        else {
            Map<Vertex.Type,Map<Vertex.Type,Double>> w=new HashMap<>();
            Map<Vertex.Type,Map<Vertex.Type,Double>> sigma=new HashMap<>();
            Map<Vertex.Type,Map<Vertex.Type,Double>> tot=new HashMap<>();
            for(Vertex.Type type:Vertex.types) {
                w.put(type, new HashMap<>());
                sigma.put(type, new HashMap<>());
                tot.put(type, new HashMap<>());
            }
            for(Vertex u:g.getVertices()) {
                if(deltaGraph.contains(u))continue;
                for(Vertex v:u.getNeighbors()) {
                    if(deltaGraph.contains(v))continue;
                    sigma.get(u.getType()).put(v.getType(), sigma.get(u.getType()).getOrDefault(v.getType(),0.0)+pow(u.getLabel().transpose().times(B.get(u.getType()).get(v.getType())).times(v.getLabel()).get(0, 0),2));
                    w.get(u.getType()).put(v.getType(), w.get(u.getType()).getOrDefault(v.getType(),0.0)+u.getLabel().transpose().times(B.get(u.getType()).get(v.getType())).times(v.getLabel()).get(0, 0));
                    tot.get(u.getType()).put(v.getType(), tot.get(u.getType()).getOrDefault(v.getType(),0.0)+1);
                }
            }
            for(Vertex.Type type0:Vertex.types)
                for(Vertex.Type type1:Vertex.types)if(type0!=type1) {
                    w.get(type0).put(type1, w.get(type0).getOrDefault(type1, 0.0)/tot.get(type0).getOrDefault(type1,0.0));
                    sigma.get(type0).put(type1, sqrt((sigma.get(type0).getOrDefault(type1, 0.0)/tot.get(type0).getOrDefault(type1,0.0)-w.get(type0).get(type1)*w.get(type0).get(type1))/(1-a)));
                    //square sigma and 1/(1-a) at the same time
                }
            //get \sigma_{tt'}*\sqrt{1/(1-a)}

            final Collection<Vertex> cand=new HashSet<>(deltaGraph);
            final Collection<Vertex> candS=new HashSet<>(cand);//additional graph with its related vertices
            for(Vertex u:cand) {
                g.addVertex(u);
                for(Vertex v:u.getNeighbors()) {
                    candS.add(v);
                    v.addEdge(u, u.getEdge(v));
                }
            }
            final Map<Vertex,Matrix> Y0=init(cand, candS, labelInit, g.getNumLabels());//original state of labels
            
            double timeUsed=0;                                                        
            double delta;                                                             
            int iter=0;                                                       
            LabelInference.infoDisplay(disp&(DISP_ITER|DISP_LABEL|DISP_OBJ), iter, 0, 0, cand,candS, Y0,B,k);
            do {
                long nTime=System.nanoTime();
                long mTime=System.currentTimeMillis();
                delta=0;
                Map<Vertex, Matrix> Y=updateY(cand,candS,Y0);
                for(Vertex u:cand) {
                    delta+=u.getLabel().subtract(Y.get(u)).norm(Matrix.FIRST_NORM);
                    u.setLabel(Y.get(u));
                }
                delta/=cand.size();
                Collection deltaCand=new HashSet<>();
                for(Vertex u:cand)
                    for(Vertex v:u.getNeighbors()) if(!cand.contains(v)) {
                        
                        if(abs(u.getLabel().transpose().times(B.get(u.getType()).get(v.getType())).times(u.getLabel()).get(0, 0)
                                -w.get(u.getType()).get(v.getType()))>sigma.get(u.getType()).get(v.getType())) {
                            deltaCand.add(v);
                            v.getNeighbors().forEach(candS::add);
                            if(v.isY0())Y0.put(v, v.getLabel().copy());
                        }
                    }
                //if \|Y(u)^{T}*Y(v)-w_{t(u)t(v)}\|>\sigma_{t(u)t(v)}*\sqrt{\frac{1}{1-\alpha}} , then add v to cand
                cand.addAll(deltaCand);
                nTime=System.nanoTime()-nTime;
                mTime=System.currentTimeMillis()-mTime;
                timeUsed+=max(mTime,nTime/1000000.0);
                iter++;
                LabelInference.infoDisplay(disp&~DISP_TIME&~DISP_B&~(iter<10||iter%10==0?0:DISP_OBJ), iter, delta, timeUsed, cand,candS, Y0,B,k);
            } while(delta>nuance && iter!=maxIter);
            LabelInference.infoDisplay(disp&(DISP_TIME|DISP_B), iter, delta, timeUsed, cand,candS, Y0,B,k);
        }
    }
    
    /**
    * 
    * @param g: a graph
    * @param labelInit: function giving a initialized k*k matrix
    * @param k: the number of clusters
    * TODO To implement the overall procedure of the basic non-incremental algorithms
     * @return 
    */	
    public Map<Vertex,Matrix> init(Collection<Vertex> cand, Collection<Vertex> candS, BiConsumer<Matrix,Integer> labelInit, int k) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
    	for(Vertex v:cand)
            for(Vertex u:v.getNeighbors())
                if(v.getEdge(u)>maxE)maxE=v.getEdge(u);
        final Map<Vertex,Matrix> Y0=new HashMap<>();
        for (Vertex v:cand)
            if(v.isY0()) {
                v.setLabel(v.getLabel().normalize());
                Y0.put(v, v.getLabel().copy());
            } else labelInit.accept(v.getLabel(),k);
        if(labelInit==LabelInference.randomLabelInit) {
            final Map<Vertex,Matrix> best=new HashMap<>();
            for(Vertex v:cand)best.put(v, v.getLabel());
            double minObj=LabelInference.objective(cand, candS, Y0, B, k);
            for(int i=0;i<100;i++) {
                for (Vertex v:cand)if(!v.isY0())labelInit.accept(v.getLabel(),k);
                double obj=LabelInference.objective(cand, candS, Y0, B, k);
                if(obj<minObj) {
                    for(Vertex v:cand)best.put(v, v.getLabel());
                    minObj=obj;
                }
            }
            for(Vertex v:cand)v.setLabel(best.get(v));
        }
        return Y0;
    }

    /**
    * 
    * @param cand: implement by cand
    * @param candS: implement by candS 
    * TODO To implement the updating procedure of B
    * 
    * This abstract method will be implemented in the three algorithms respectively
     * @throws labelinference.exceptions.DimensionNotAgreeException
    */	
    abstract protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS) throws DimensionNotAgreeException ;

    /**
     * 
	 * @param cand: implement by cand
	 * @param candS: implement by candS 
	 * @param Y0: the initialized label
	 * TODO To implement the updating procedure of Y
	 * 
	 * This abstract method will be implemented in the three algorithms respectively
     * @return 
     * @throws labelinference.exceptions.DimensionNotAgreeException
	 */	
    abstract protected Map<Vertex, Matrix> updateY(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex, Matrix> Y0) throws DimensionNotAgreeException ;
}
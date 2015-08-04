/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
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

 * TODO To give the framework of the overall procedure of incremental and non-incremental algorithms

 * @see labelinference.Experiment.class#main(String[])
 * 
 */

public abstract class AbstractLabelInference implements LabelInference{
    protected final Graph g;                                
    protected boolean isDone;                               
    protected final Function<Integer,Matrix> labelInit;     
    protected final int k;                                  
    protected double maxE=0;                                
    protected Map<Vertex.Type,Map<Vertex.Type,Matrix>> B=new HashMap<>();
    
    /**
     * 
	 * @param _g:initial graph g with _g
	 */	
    public AbstractLabelInference(Graph _g) {	
        this(_g,LabelInference::defaultLabelInit);
    }
    
    /**
     * 
	 * @param _g:initial graph g with _g
	 * @param _labelInit: initial labeled vertices
	 */	
    public AbstractLabelInference(Graph _g, Function<Integer,Matrix> _labelInit) {
    //this method deviced for initializing if we've known some of the labels in Yl
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        labelInit=_labelInit;
        for(Vertex.Type t0:Vertex.types) {
            B.put(t0, new HashMap<>());
            for(Vertex.Type t1:Vertex.types)
                B.get(t0).put(t1,MatrixFactory.getInstance().identityMatrix(k));
        }
    }
    
    @Override
    /**
     * 
	 * @param maxIter: the max iterations times
	 * @param nuance: a tiny number control when the procedure ends
	 * @param disp: a code choose what to display
	 * TODO To inplement the overall procedure of the basic non-incremental algorithms
	 */	
    public void getResult(int maxIter, double nuance, int disp) {

        if(isDone)return;                                        
        try {
            final Map<Vertex,Matrix> Y0=init(g.getVertices(), labelInit, g.getNumLabels());
            double timeUsed=0;   
            double delta;
            int iter=0;
            LabelInference.infoDisplay(disp&(DISP_ITER|DISP_LABEL|DISP_OBJ), iter, 0, 0, g.getVertices(),g.getVertices(), Y0,B,k);
            do {
                long nTime=System.nanoTime();
                long mTime=System.currentTimeMillis();
                delta=update(g.getVertices(),g.getVertices(),Y0)/g.getVertices().size();//call the update method
                nTime=System.nanoTime()-nTime;
                mTime=System.currentTimeMillis()-mTime;
                timeUsed+=max(mTime,nTime/1000000.0);
                iter++;
                LabelInference.infoDisplay(disp&~DISP_TIME, iter, delta, timeUsed,g.getVertices(),g.getVertices(), Y0,B,k);//write the infomation
            } while(delta>nuance && iter!=maxIter);
            //iterates until converge or iter>maxIter
            LabelInference.infoDisplay(disp&DISP_TIME, iter, delta, timeUsed, g.getVertices(),g.getVertices(), Y0,B,k);
            //write the final state
        } catch (DimensionNotAgreeException | ColumnOutOfRangeException | RowOutOfRangeException ex) {
            Logger.getLogger(BlockCoordinateDescent.class.getName()).log(Level.SEVERE, null, ex);
        }
        isDone=true;
    }
    
    @Override
    /**
     * 
	 * @param deltaGraph: the additional graph
	 * @param maxIter: the max iterations times
	 * @param nuance: a tiny number control when the procedure ends
	 * @param a: the constant alpha in algorithm2 line09
	 * @param disp: a code choose what to display
	 * TODO To inplement the overall procedure of the basic non-incremental algorithms
	 */	
    public void increase(Collection<Vertex> deltaGraph, int maxIter, double nuance, double a, int disp) {
    	
        if(!isDone)getResult(maxIter, nuance, disp);
        else try {
        	
        	final Map<Vertex,Matrix> Y0=init(deltaGraph, labelInit, g.getNumLabels());
            final Collection<Vertex> cand=new HashSet<>(deltaGraph);
            final Collection<Vertex> candS=new HashSet<>(cand);
            //cand denotes the latest state of the candS

            Map<Vertex.Type,Map<Vertex.Type,Double>> w=new HashMap<>();
            Map<Vertex.Type,Map<Vertex.Type,Double>> sigma=new HashMap<>();
            Map<Vertex.Type,Map<Vertex.Type,Double>> tot=new HashMap<>();
            for(Vertex.Type type:Vertex.types) {
                w.put(type, new HashMap<>());
                sigma.put(type, new HashMap<>());
                tot.put(type, new HashMap<>());
            }
            for(Vertex u:g.getVertices()) {
                for(Vertex v:u.getNeighbors()) {
                    sigma.get(u.getType()).put(v.getType(), sigma.get(u.getType()).getOrDefault(v.getType(),0.0)+pow(u.getLabel().transpose().times(v.getLabel()).get(0, 0),2));
                    w.get(u.getType()).put(v.getType(), w.get(u.getType()).getOrDefault(v.getType(),0.0)+u.getLabel().transpose().times(v.getLabel()).get(0, 0));
                    tot.get(u.getType()).put(v.getType(), tot.get(u.getType()).getOrDefault(v.getType(),0.0)+1);
                }
            }
            for(Vertex.Type type0:Vertex.types)
                for(Vertex.Type type1:Vertex.types)if(type0!=type1) {
                    w.get(type0).put(type1, w.get(type0).getOrDefault(type1, 0.0)/tot.get(type0).getOrDefault(type1,0.0));
                    sigma.get(type0).put(type1, sqrt((sigma.get(type0).getOrDefault(type1, 0.0)/tot.get(type0).getOrDefault(type1,0.0)-w.get(type0).get(type1)*w.get(type0).get(type1))/(1-a)));
                    //square sigma and 1/(1-a) at the same time
                }
            //get sigma*sqrt(1/(1-a))
            
            for(Vertex u:cand) {
                g.addVertex(u);
                for(Vertex v:u.getNeighbors()) {
                    candS.add(v);
                    v.addEdge(u, u.getEdge(v));
                    if(v.isY0())Y0.put(v, v.getLabel().copy());
                }
            }
            //add related vertices to candS
            
            cand.addAll(candS);
            for(Vertex u:cand)u.getNeighbors().forEach(candS::add);
            //update cand as candS
            
            double timeUsed=0;                                                        
            double delta;                                                            
            int iter=0;                                                                 
            
            LabelInference.infoDisplay(disp&(DISP_ITER|DISP_LABEL|DISP_OBJ), iter, 0, 0, cand,candS, Y0,B,k);
            do {
                long nTime=System.nanoTime();
                long mTime=System.currentTimeMillis();
                delta=update(cand,candS,Y0)/cand.size();
                Collection deltaCand=new HashSet<>();
                for(Vertex u:cand)
                    for(Vertex v:u.getNeighbors()) if(!cand.contains(v)) {
                        if(abs(u.getLabel().transpose().times(u.getLabel()).get(0, 0)-w.get(u.getType()).get(v.getType()))>sigma.get(u.getType()).get(v.getType())) {
                            deltaCand.add(v);
                            v.getNeighbors().forEach(candS::add);
                            if(v.isY0())Y0.put(v, v.getLabel().copy());
                        }
                    }
                //if abs(Yn'(u)*Yn(v)-w)>=sqrt(1/(1-alpha))*sigma, then add v to cand
                cand.addAll(deltaCand);
                nTime=System.nanoTime()-nTime;
                mTime=System.currentTimeMillis()-mTime;
                timeUsed+=max(mTime,nTime/1000000.0);
                iter++;
                LabelInference.infoDisplay(disp&~DISP_TIME, iter, delta, timeUsed, cand,candS, Y0,B,k);
            } while(delta>nuance && iter!=maxIter);
            //do the iteration and expand the cand set
            
            LabelInference.infoDisplay(disp&DISP_TIME, iter, delta, timeUsed, cand,candS, Y0,B,k);
        } catch (DimensionNotAgreeException | ColumnOutOfRangeException | RowOutOfRangeException ex) {
            Logger.getLogger(BlockCoordinateDescent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
	 * @param g: a graph
	 * @param labelInit: function giving a initialized k*k matrix
	 * @param k: the number of clusters
	 * TODO To inplement the overall procedure of the basic non-incremental algorithms
	 */	
    public Map<Vertex,Matrix> init(Collection<Vertex> g, Function<Integer,Matrix> labelInit, int k) {
        
    	for(Vertex v:g)
            for(Vertex u:v.getNeighbors())
                if(v.getEdge(u)>maxE)maxE=v.getEdge(u);
        //find the maxE
        
        final Map<Vertex,Matrix> Y0=new HashMap<>();
        for (Vertex v:g)
            if(v.isY0())Y0.put(v, v.getLabel().copy());
            else v.setLabel(labelInit.apply(k));
        //initialize Y0 with infomation in graph if v is in Yl, otherwise initialize Y0 with infomation with labelInit
        
        return Y0;
    }
    
    
    /**
     * 
	 * @param band: implement by cand
	 * @param bandS: implement by candS 
	 * @param Y0: the original state of Y
	 * TODO To implement the iteration procedure
	 * 
	 * This abstract method will be implemented in the three algorithms respectively
	 */	
    abstract protected double update(Collection<Vertex> band, Collection<Vertex> bandS, Map<Vertex,Matrix> Y0)  throws DimensionNotAgreeException,ColumnOutOfRangeException, RowOutOfRangeException ;
}
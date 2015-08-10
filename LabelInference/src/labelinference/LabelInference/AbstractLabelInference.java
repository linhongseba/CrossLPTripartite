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
import java.util.function.BiConsumer;
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
 */

/*
 * This class is an abstract class giving the framework of the overall procedure  algorithms, calling the three b.
 */
public abstract class AbstractLabelInference implements LabelInference{
    protected final Graph g;                                //the variable denotes the tripartite graph
    protected boolean isDone;                               //the variable denotes whether the algorithm had finished
    protected final BiConsumer<Matrix,Integer> labelInit;     //the function denote
    protected final int k;                                  //the variable denotes the number of clusters
    protected double maxE=0;                                //the variable denotes the max number of edges
    protected Map<Vertex.Type,Map<Vertex.Type,Matrix>> B=new HashMap<>();
    
    public AbstractLabelInference(Graph _g) {	
        this(_g,LabelInference::defaultLabelInit);
    }
    
    public AbstractLabelInference(Graph _g, BiConsumer<Matrix,Integer> _labelInit) {
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
    public void getResult(int maxIter, double nuance, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {
        if(isDone)return;                                        
        final Map<Vertex,Matrix> Y0=init(g.getVertices(), labelInit, g.getNumLabels());   //the variable denotes the original state of the Matrix Y
        double timeUsed=0;                                                                //the variable counts the total used time.
        double delta;                                                                     //the variable denotes the difference between Y and last produced Y.
        int iter=0;                                                                       //the variable controls the iteration times.
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
            LabelInference.infoDisplay(disp&~DISP_TIME, iter, delta, timeUsed,g.getVertices(),g.getVertices(), Y0,B,k);
        } while(delta>nuance && iter!=maxIter);
        LabelInference.infoDisplay(disp&DISP_TIME, iter, delta, timeUsed, g.getVertices(),g.getVertices(), Y0,B,k);
        isDone=true;
    }
    
    @Override
    public void increase(Collection<Vertex> deltaGraph, int maxIter, double nuance, double a, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {
        if(!isDone)getResult(maxIter, nuance, disp);
        else {
            final Map<Vertex,Matrix> Y0=init(deltaGraph, labelInit, g.getNumLabels());//original state of labels
            final Collection<Vertex> cand=new HashSet<>(deltaGraph);
            final Collection<Vertex> candS=new HashSet<>(cand);//additional graph with its related vertices

            Map<Vertex.Type,Map<Vertex.Type,Double>> w=new HashMap<>();
            Map<Vertex.Type,Map<Vertex.Type,Double>> sigma=new HashMap<>();
            Map<Vertex.Type,Map<Vertex.Type,Double>> tot=new HashMap<>();
            for(Vertex.Type type:Vertex.types) {
                w.put(type, new HashMap<>());
                sigma.put(type, new HashMap<>());
                tot.put(type, new HashMap<>());
            }
            for(Vertex u:g.getVertices()) {
                if(cand.contains(u))continue;
                for(Vertex v:u.getNeighbors()) {
                    if(cand.contains(v))continue;
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
            //get sigma*sqrt(1/(1-a)) and w for the following procedure
            
            for(Vertex u:cand) {
                g.addVertex(u);
                for(Vertex v:u.getNeighbors()) {
                    candS.add(v);
                    v.addEdge(u, u.getEdge(v));
                    if(v.isY0())Y0.put(v, v.getLabel().copy());
                }
            }
            //add related vertices to candS
            
            //cand.addAll(candS);
            //for(Vertex u:cand)u.getNeighbors().forEach(candS::add);
            double timeUsed=0;                                                        //the variable counts the total used time.
            double delta;                                                             //the variable denotes the difference between Y and last produced Y.
            int iter=0;                                                               //the variable controls the iteration times.     
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
                //if abs(Yn'(u)*Yn(v)-w)>=sqrt(1/(1-alpha))*sigma, then add v to cand
                cand.addAll(deltaCand);
                nTime=System.nanoTime()-nTime;
                mTime=System.currentTimeMillis()-mTime;
                timeUsed+=max(mTime,nTime/1000000.0);
                iter++;
                LabelInference.infoDisplay(disp&~DISP_TIME, iter, delta, timeUsed, cand,candS, Y0,B,k);
            } while(delta>nuance && iter!=maxIter);
            //iterates until converge or iter>maxIter
            LabelInference.infoDisplay(disp&DISP_TIME, iter, delta, timeUsed, cand,candS, Y0,B,k);
        }
    }
    
    public Map<Vertex,Matrix> init(Collection<Vertex> g, BiConsumer<Matrix,Integer> labelInit, int k) {
    	for(Vertex v:g)
            for(Vertex u:v.getNeighbors())
                if(v.getEdge(u)>maxE)maxE=v.getEdge(u);
        final Map<Vertex,Matrix> Y0=new HashMap<>();
        for (Vertex v:g)
            if(v.isY0())Y0.put(v, v.getLabel().copy());
            else labelInit.accept(v.getLabel(),k);
        return Y0;
    }

    abstract protected void updateB(Collection<Vertex> vertices, Collection<Vertex> vertices0) throws DimensionNotAgreeException ;

    abstract protected Map<Vertex, Matrix> updateY(Collection<Vertex> vertices, Collection<Vertex> vertices0, Map<Vertex, Matrix> Y0) throws DimensionNotAgreeException ;
}

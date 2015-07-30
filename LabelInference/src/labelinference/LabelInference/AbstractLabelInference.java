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

/**
 *
 * @author sailw
 */
public abstract class AbstractLabelInference implements LabelInference{
    protected final Graph g;
    protected boolean isDone;
    protected final Function<Integer,Matrix> labelInit;
    protected final int k;
    protected double maxE=0;
    
    public AbstractLabelInference(Graph _g) {	
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        labelInit=LabelInference::defaultLabelInit;
    }
    
    public AbstractLabelInference(Graph _g, Function<Integer,Matrix> _labelInit) {
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        labelInit=_labelInit;
    }
    
    @Override
    public void getResult(int maxIter, double nuance, int disp) {
        if(isDone)return;
        try {
            final Map<Vertex,Matrix> Y0=init(g.getVertices(), labelInit, g.getNumLabels());
            double timeUsed=0;
            double delta;
            int iter=0;
            LabelInference.infoDisplay(disp&(DISP_ITER|DISP_LABEL|DISP_OBJ), iter, 0, 0, g.getVertices(),g.getVertices(), Y0,k);
            do {
                long nTime=System.nanoTime();
                long mTime=System.currentTimeMillis();
                delta=update(g.getVertices(),g.getVertices(),Y0)/g.getVertices().size();
                nTime=System.nanoTime()-nTime;
                mTime=System.currentTimeMillis()-mTime;
                timeUsed+=max(mTime,nTime/1000000.0);
                iter++;
                LabelInference.infoDisplay(disp&~DISP_TIME, iter, delta, timeUsed,g.getVertices(),g.getVertices(), Y0,k);
            } while(delta>nuance && iter!=maxIter);
            LabelInference.infoDisplay(disp&DISP_TIME, iter, delta, timeUsed, g.getVertices(),g.getVertices(), Y0,k);
        } catch (DimensionNotAgreeException | ColumnOutOfRangeException | RowOutOfRangeException ex) {
            Logger.getLogger(BlockCoordinateDescent.class.getName()).log(Level.SEVERE, null, ex);
        }
        isDone=true;
    }
    
    @Override
    public void increase(Collection<Vertex> deltaGraph, int maxIter, double nuance, double a, int disp) {
        if(!isDone)getResult(maxIter, nuance, disp);
        else try {
            final Map<Vertex,Matrix> Y0=init(deltaGraph, labelInit, g.getNumLabels());
            final Collection<Vertex> cand=new HashSet<>(deltaGraph);
            final Collection<Vertex> candS=new HashSet<>(cand);

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
                    //System.err.println(sigma.get(type0).get(type1));
                    //System.err.println(tot.get(type0).get(type1));
                    //System.err.println(w.get(type0).get(type1));
                    
                }
            
            for(Vertex u:cand) {
                g.addVertex(u);
                for(Vertex v:u.getNeighbors()) {
                    candS.add(v);
                    v.addEdge(u, u.getEdge(v));
                    if(v.isY0())Y0.put(v, v.getLabel().copy());
                }
            }
            cand.addAll(candS);
            for(Vertex u:cand)u.getNeighbors().forEach(candS::add);
            
            double timeUsed=0;
            double delta;
            int iter=0;
            LabelInference.infoDisplay(disp&(DISP_ITER|DISP_LABEL|DISP_OBJ), iter, 0, 0, cand,candS, Y0,k);
            do {
                long nTime=System.nanoTime();
                long mTime=System.currentTimeMillis();
                delta=update(cand,candS,Y0)/cand.size();
                Collection deltaCand=new HashSet<>();
                for(Vertex u:cand)
                    for(Vertex v:u.getNeighbors()) if(!cand.contains(v)) {
                        //System.err.println(abs(u.getLabel().transpose().times(u.getLabel()).get(0, 0)-w.get(u.getType()).get(v.getType())));
                        //System.err.println(sigma.get(u.getType()).get(v.getType()));
                        if(abs(u.getLabel().transpose().times(u.getLabel()).get(0, 0)-w.get(u.getType()).get(v.getType()))>sigma.get(u.getType()).get(v.getType())) {
                            deltaCand.add(v);
                            v.getNeighbors().forEach(candS::add);
                            if(v.isY0())Y0.put(v, v.getLabel().copy());
                        }
                    }
                cand.addAll(deltaCand);
                nTime=System.nanoTime()-nTime;
                mTime=System.currentTimeMillis()-mTime;
                timeUsed+=max(mTime,nTime/1000000.0);
                iter++;
                LabelInference.infoDisplay(disp&~DISP_TIME, iter, delta, timeUsed, cand,candS, Y0,k);
            } while(delta>nuance && iter!=maxIter);
            LabelInference.infoDisplay(disp&DISP_TIME, iter, delta, timeUsed, cand,candS, Y0,k);
        } catch (DimensionNotAgreeException | ColumnOutOfRangeException | RowOutOfRangeException ex) {
            Logger.getLogger(BlockCoordinateDescent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Map<Vertex,Matrix> init(Collection<Vertex> g, Function<Integer,Matrix> labelInit, int k) {
        for(Vertex v:g)
            for(Vertex u:v.getNeighbors())
                if(v.getEdge(u)>maxE)maxE=v.getEdge(u);
        final Map<Vertex,Matrix> Y0=new HashMap<>();
        for (Vertex v:g)
            if(v.isY0())Y0.put(v, v.getLabel().copy());
            else v.setLabel(labelInit.apply(k));
        return Y0;
    }
    
    
    abstract protected double update(Collection<Vertex> band, Collection<Vertex> bandS, Map<Vertex,Matrix> Y0)  throws DimensionNotAgreeException,ColumnOutOfRangeException, RowOutOfRangeException ;
}

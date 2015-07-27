/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import static java.lang.Math.max;
import static java.lang.Math.pow;
import java.util.Calendar;
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
    
    public LabelPropagation(Graph _g) {
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        alpha=0.5;
        nuance=1e-4;
        maxIter=10000;
        labelInit=(Integer x)->LabelInference.defaultLabelInit(x);
    }
    
    public LabelPropagation(Graph _g, double _alpha,double  _nuance, int _maxIter) {
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        alpha=_alpha;
        nuance=_nuance;
        maxIter=_maxIter;
        labelInit=(Integer x)->LabelInference.defaultLabelInit(x);
    }
    
    public LabelPropagation(Graph _g, double _alpha,double _nuance, int _maxIter, Function<Integer,Matrix> _labelInit) {
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        alpha=_alpha;
        nuance=_nuance;
        maxIter=_maxIter;
        labelInit=_labelInit;
    }
    
    @Override
    public double getResult() {
        double timeUsed=0;
        if(isDone)return 0;
        double maxE=0;
        for(Vertex v:g.getVertices())
            for(Vertex u:v.getNeighbors())
                if(v.getEdge(u)>maxE)maxE=v.getEdge(u);
        for(Vertex v:g.getVertices())
            for(Vertex u:v.getNeighbors())
                u.addEdge(v, u.getEdge(v)/maxE);
        final Map<Vertex,Matrix> Y0=new HashMap<>();
        for (Vertex v : g.getVertices()) {
            if(v.isY0())Y0.put(v, v.getLabel().copy());
            else v.setLabel(labelInit.apply(k));
        }
        try {
            double delta;
            int iter=0;
            System.out.print(String.format("Cycle: %d\n",iter)); 
            for(Vertex v:g.getVertices()) {
                //System.out.print(v.getId()+v.getLabel().toString()+"\n"); 
            }
            do {
                long nTime=System.nanoTime();
                long mTime=System.currentTimeMillis();
                delta=update()/g.getVertices().size();
                nTime=System.nanoTime()-nTime;
                mTime=System.currentTimeMillis()-mTime;
                timeUsed+=max(mTime,nTime/1000000.0);
                iter++;
                //System.err.print(delta);
                System.out.print(String.format("Cycle: %d\n",iter));
                System.out.print(String.format("ObjValue: %f\n",LabelInference.objective(g,Y0,k)));
                for(Vertex v:g.getVertices()) {
                    //System.out.print(v.getId()+v.getLabel().toString()+"\n"); 
                }
            } while(delta>nuance && iter!=maxIter);
        } catch (DimensionNotAgreeException ex) {
            Logger.getLogger(NewMultiplicative.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {
            Logger.getLogger(LabelPropagation.class.getName()).log(Level.SEVERE, null, ex);
        }
        isDone=true;
        return timeUsed;
    }

    private double update() throws DimensionNotAgreeException {
        final MatrixFactory mf=MatrixFactory.getInstance();
        final Map<Vertex,Map<Vertex.Type,Matrix>> cache=new HashMap<>();
        Matrix emptyMat=mf.creatMatrix(k, 1);
        for(Vertex u:g.getVertices()) {
            Map<Vertex.Type,Matrix> value=new HashMap<>();
            for(Vertex v:u.getNeighbors())
                value.put(v.getType(), value.getOrDefault(v.getType(), emptyMat).add(v.getLabel().times(v.getEdge(u)/v.sumE())));
            cache.put(u, value);
        }

        double delta=0;
        for(final Vertex u:g.getVertices()) {
            if(u.isY0())continue;
            Matrix label=mf.creatMatrix(k, 1);
            Matrix a=mf.creatMatrix(k, 1);
            Matrix b=mf.creatMatrix(k, 1);
            for(Vertex v:u.getNeighbors()) {
                a=a.add(v.getLabel().times(u.getEdge(v)));
                b=b.add(cache.get(v).get(u.getType()).subtract(u.getLabel().times(u.getEdge(v)/u.sumE())).times(1.0/v.sumE()));
            }
            label=a.normalize().times(1-alpha).add(b.normalize().times(alpha));
            delta+=label.subtract(u.getLabel()).norm(Matrix.FIRST_NORM);
            u.setLabel(label); 
        }
        return delta;
    }
}
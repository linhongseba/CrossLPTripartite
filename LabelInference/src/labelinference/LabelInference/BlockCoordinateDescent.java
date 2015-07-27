/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import java.util.Calendar;
import java.util.Collection;
import labelinference.Matrix.MatrixFactory;
import labelinference.Matrix.Matrix;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.IrreversibleException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author Hermes777, SailWhite
 * 
 * @linhong: 
 * 1)please change hard coded number of classes 2 to a parameter:)
 * 2)the convergence nuance might reduce to a smaller value or set as a parameter too:)
 * 3)increse the max number of iterations or set as a parameter too:)
 * 
 * 
 */
public class BlockCoordinateDescent implements LabelInference {
    private final Graph g;
    private boolean isDone;
    private final double nuance;
    private final Function<Integer,Matrix> labelInit;
    private final int k;
    private final int maxIter;
	
    public BlockCoordinateDescent(Graph _g) {	
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        nuance=1e-4;
        maxIter=1000;
        labelInit=(Integer x)->LabelInference.defaultLabelInit(x);
    }
    
    public BlockCoordinateDescent(Graph _g,double  _nuance, int _maxIter) {
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        nuance=_nuance;
        maxIter=_maxIter;
        labelInit=(Integer x)->LabelInference.defaultLabelInit(x);
    }
    
    public BlockCoordinateDescent(Graph _g,double  _nuance, int _maxIter, Function<Integer,Matrix> _labelInit) {
        g=_g;
        k=g.getNumLabels();
        isDone=false;
        nuance=_nuance;
        maxIter=_maxIter;
        labelInit=_labelInit;
    }
    
    private double update(Map<Vertex,Matrix> Y0) throws DimensionNotAgreeException, IrreversibleException {
        final MatrixFactory mf=MatrixFactory.getInstance();
        final Map<Vertex.Type,Matrix> A=new HashMap<>();
        final Map<Vertex.Type,Matrix> Ay0=new HashMap<>();
        Matrix emptyMat=mf.creatMatrix(k, k);
        Matrix identity=mf.identityMatrix(k);
        for(Vertex.Type type:Vertex.types) {
            for(Vertex v:g.getVertices(v->v.getType()!=type))
                A.put(type, A.getOrDefault(type, emptyMat).add(v.getLabel().times(v.getLabel().transpose())));
            Ay0.put(type, A.get(type).add(identity).adjoint());
            A.put(type, A.get(type).adjoint());
        }
        double delta=0;
        
        for(Vertex u:g.getVertices()) {
            Matrix label=mf.creatMatrix(k,1);
            for(Vertex v:u.getNeighbors())
                label=label.add(v.getLabel().times(u.getEdge(v)));
            if(u.isY0()) label=Ay0.get(u.getType()).times(label.add(Y0.get(u))).normalize();
            else label=A.get(u.getType()).times(label).normalize();
            delta+=u.getLabel().subtract(label).norm(Matrix.FIRST_NORM);
            u.setLabel(label);
        }
        return delta;
    }
    
    @Override
    public double getResult(){
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
                delta=update(Y0)/g.getVertices().size();
                nTime=System.nanoTime()-nTime;
                mTime=System.currentTimeMillis()-mTime;
                timeUsed+=max(mTime,nTime/1000000.0);
                iter++;
                //System.out.print(delta);
                System.out.print(String.format("Cycle: %d\n",iter));
                System.out.print(String.format("ObjValue: %f\n",LabelInference.objective(g,Y0,k)));
                for(Vertex v:g.getVertices()) {
                    //System.out.print(v.getId()+v.getLabel().toString()+"\n"); 
                }
            } while(delta>nuance && iter!=maxIter);
        } catch (DimensionNotAgreeException ex) {
            Logger.getLogger(NewMultiplicative.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ColumnOutOfRangeException | RowOutOfRangeException | IrreversibleException ex) {
            Logger.getLogger(BlockCoordinateDescent.class.getName()).log(Level.SEVERE, null, ex);
        }
        isDone=true;
        return timeUsed;
    }
}

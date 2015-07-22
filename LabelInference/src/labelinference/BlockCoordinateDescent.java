/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import static java.lang.Math.abs;
import static java.lang.Math.random;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	
    private Matrix ranLabel() throws ColumnOutOfRangeException, RowOutOfRangeException {
        final MatrixFactory mf=MatrixFactory.getInstance();
        Matrix label=mf.creatMatrix(2,1);
        label.set(0, 0, random());
        label.set(1,0,1-label.get(0, 0));
        return label;
    }
	
	
    public BlockCoordinateDescent(Graph _g) {	
        g=_g;
    }
    
    private double update(Map<Vertex,Matrix> Y0) throws DimensionNotAgreeException,ColumnOutOfRangeException, RowOutOfRangeException, IrreversibleException {
        final MatrixFactory mf=MatrixFactory.getInstance();
    	final Matrix I=mf.identityMatrix(2);
        final double dData[][]={{1e-5,0},{0,1e-5}};
        final Matrix d=mf.creatMatrix(dData);
        final Map<Vertex.Type,Double> A=new HashMap<>();
        final Map<Vertex.Type,Double> Ay0=new HashMap<>();
        A.put(Vertex.typeA, null);
        A.put(Vertex.typeB, null);
        A.put(Vertex.typeC, null);
        
        for(Vertex.Type type:A.keySet()) {
            A.put(type,new Double(0));
            for(Vertex v:g.getVertices()) 
                if(v.getType()!=type)
                    A.put(type, A.get(type).doubleValue()+v.getLabel().times(v.getLabel().transpose()).get(0, 0));
            Ay0.put(type, 1/(A.get(type).doubleValue()+1));	
            A.put(type, 1/A.get(type).doubleValue());
            
        }
        double delta=0;
        for(Vertex u:g.getVertices()) {
            Matrix label=mf.creatMatrix(2,1);
            for(Vertex v:u.getNeighbors())
                label=label.add(v.getLabel().timesNum(u.getEdge(v)));
            if(u.isY0()) label=label.add(Y0.get(u)).times(Ay0.get(u.getType())).orthonormalize();
            else label=label.times(A.get(u.getType())).orthonormalize();
            delta+=u.getLabel().subtract(label).norm(Matrix.FIRST_NORM);
            u.setLabel(label);
        }
        return delta;
    }
    
    @Override
    public Graph getResult(){
        if(isDone)return g;
        MatrixFactory mf=MatrixFactory.getInstance();
        try {
            final Map<Vertex,Matrix> Y0=new HashMap<>();
            for (Vertex v : g.getVertices()) {
                if(v.isY0())Y0.put(v, v.getLabel().copy());
                else v.setLabel(ranLabel());
            }

            int iter=0;
            double delta;
            do {
                delta=update(Y0)/g.getVertices().size();
                System.out.println(delta);
                iter++;
            } while(iter<25);

            for(Vertex v:g.getVertices())
                if(v.isY0())
                    v.setLabel(Y0.get(v));
        } catch (ColumnOutOfRangeException | DimensionNotAgreeException | IrreversibleException | RowOutOfRangeException ex) {
            Logger.getLogger(LabelPropagation.class.getName()).log(Level.SEVERE, null, ex);
        }
        isDone=true;
        return g;
    }
}

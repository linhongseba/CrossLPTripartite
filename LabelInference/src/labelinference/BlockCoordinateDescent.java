/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import static java.lang.Math.abs;
import static java.lang.Math.random;
import java.util.HashMap;
import java.util.Map;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.IrreversibleException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author Hermes777, SailWhite
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
    
    private void update(Matrix Y, Matrix Y0) throws DimensionNotAgreeException,ColumnOutOfRangeException, RowOutOfRangeException, IrreversibleException {
        final MatrixFactory mf=MatrixFactory.getInstance();
    	final Matrix I=mf.identityMatrix(2);
        final double deltaData[][]={{1e-5,0},{0,1e-5}};
        final Matrix delta=mf.creatMatrix(deltaData);
        final Map<Vertex.Type,Matrix> A=new HashMap<>();
        final Map<Vertex.Type,Matrix> Ay0=new HashMap<>();
        A.put(Vertex.typeA, null);
        A.put(Vertex.typeB, null);
        A.put(Vertex.typeC, null);
        
        for(Vertex.Type type:A.keySet()) {
            A.put(type,mf.creatMatrix(2,2));
            for(Vertex v:g.getVertices()) 
                if(v.getType()!=type)
                    A.put(type, A.get(type).add(v.getLabel().times(v.getLabel().transpose())));
            if(abs(A.get(type).determinant())<1e-9) A.put(type,A.get(type).add(delta));
            Ay0.put(type, A.get(type).add(I));
            if(abs(Ay0.get(type).determinant())<1e-9)Ay0.put(type,Ay0.get(type).add(delta));	
            A.put(type, A.get(type).inverse());
            Ay0.put(type, Ay0.get(type).inverse());
        }
        
        int row=0;
        for(Vertex u:g.getVertices()) {
            Matrix tempY=mf.creatMatrix(2,1);
            for(Vertex v:u.getNeighbors())
                tempY=tempY.add(v.getLabel().timesNum(u.getEdge(v)));
            if(u.isY0()) u.setLabel(Ay0.get(u.getType()).times(tempY.add(Y0.getRow(row).transpose().orthonormalize())));
            else u.setLabel(A.get(u.getType()).times(tempY).orthonormalize());
            Y.setRow(row,u.getLabel().orthonormalize().transpose());
            row++;
        }
    }

    private boolean converge(Matrix Y, Matrix lastY) throws DimensionNotAgreeException {
        final double nuance=0.0001;
        double x=Y.subtract(lastY).norm(Matrix.FROBENIUS_NORM)/g.getVertices().size();
        System.out.println(x);
        return x<nuance;
    }
    
    @Override
    public Graph getResult(){
        if(isDone)return g;
        MatrixFactory mf=MatrixFactory.getInstance();
        try {
            final Map<Vertex,Matrix> y0Backup=new HashMap<>();
            final int nVertex=g.getVertices().size();
            final Matrix Y0=mf.creatMatrix(nVertex,2);
            final Matrix Y=mf.creatMatrix(nVertex,2);
            Matrix lastY;
            for (Vertex v : g.getVertices())
                if(v.isY0())y0Backup.put(v, v.getLabel().copy());

            int row=0;
            for(Vertex u:g.getVertices()) {
                if(u.isY0()) {
                    Y0.setRow(row, u.getLabel().transpose());
                    Y.setRow(row, u.getLabel().transpose());
                } else Y.setRow(row, ranLabel().transpose());
                row++;
            }//get Y0

            int iter=0;
            do {
                lastY=Y.copy();
                update(Y,Y0);
                iter++;
            } while(iter<5 && !converge(Y,lastY));

            for(Vertex v:g.getVertices())
                if(v.isY0())
                    v.setLabel(y0Backup.get(v));
        } catch (RowOutOfRangeException | ColumnOutOfRangeException | DimensionNotAgreeException | IrreversibleException ex) {}
        isDone=true;
        return g;
    }
}
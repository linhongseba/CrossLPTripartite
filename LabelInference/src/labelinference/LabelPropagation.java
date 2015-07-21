/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import static java.lang.Math.log;
import static java.lang.Math.random;
import static java.lang.Thread.sleep;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final double alpha=0.2;
    private final int maxThread=2000;
    private final double minDelta=0.03;
    private final double minDDelta=0.1;
    
    public LabelPropagation(Graph _g) {
        g=_g;
        isDone=false;
    }
    
    @Override
    public Graph getResult() {
        if(isDone)return g;
        try {
            if(isDone)return g;
            double delta=0;
            double lastDelta;
            init();
            do {
                lastDelta=delta;
                delta=update()/g.getVertices().size();
                if(lastDelta==0)lastDelta=2*delta;
                System.out.println(delta);
            }while(delta>minDelta && (lastDelta-delta)>minDDelta*delta);
            for(Vertex v:g.getVertices())v.setLabel(v.getLabel().getCol(0));
            return g;
        } catch (ColumnOutOfRangeException | DimensionNotAgreeException | RowOutOfRangeException | InterruptedException ex) {
            Logger.getLogger(LabelPropagation.class.getName()).log(Level.SEVERE, null, ex);
        }
        isDone=true;
        return g;
    }

    private double update() throws DimensionNotAgreeException, ColumnOutOfRangeException, InterruptedException {
        final MatrixFactory mf=MatrixFactory.getInstance();
        final Collection<Vertex> threads=new HashSet<>();
        final double dataA[][]={{0},{0}};
        final double dataB[][]={{0},{0}};
        final double dataR[][]={{0,1},{1,0}};
        final Matrix r=mf.creatMatrix(dataR);
        
        for(final Vertex u:g.getVertices()) {
            while(threads.size()>maxThread)sleep(1000);
            Thread thread=new Thread(() -> {
                if(u.isY0()) {
                    synchronized(threads) {
                        threads.remove(u);
                    }
                    return;
                }
                
                Matrix label=u.getLabel();
                Matrix a=mf.creatMatrix(dataA);
                Matrix b=mf.creatMatrix(dataB);
                try {
                    for(Vertex v:u.getNeighbors()) {
                        a=a.add(v.getLabel().getCol(0).times(u.getEdge(v)));
                        for(Vertex w:v.getNeighbors()) {
                            if(w.getType()!=u.getType())continue;
                            b=b.add(r.times(w.getLabel().getCol(0).times(v.getEdge(w)*v.getEdge(u))));
                        }
                    }
                    label.setCol(1, a.orthonormalize().times(1-alpha).add(b.orthonormalize().times(alpha)));
                } catch (ColumnOutOfRangeException | DimensionNotAgreeException ex) {
                    Logger.getLogger(LabelPropagation.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                synchronized(threads) {
                    threads.remove(u);
                }
            });
            synchronized(threads) {
                threads.add(u);
            }
            thread.start();
        }
        
        while(threads.size()>0)sleep(1000);
        double delta=0;
        for(Vertex u:g.getVertices()) {
            delta+=u.getLabel().getCol(1).subtract(u.getLabel().getCol(0)).norm(Matrix.FIRST_NORM);
            if(!u.isY0())u.getLabel().setCol(0, u.getLabel().getCol(1));
        }
        return delta;
    }

    private void init() throws ColumnOutOfRangeException, DimensionNotAgreeException, RowOutOfRangeException {
        final MatrixFactory mf=MatrixFactory.getInstance();
        for(Vertex v:g.getVertices()) {
            Matrix label=mf.creatMatrix(2, 2);
            if(v.isY0())label.setCol(0, v.getLabel());
            else label.setCol(0, ranLabel());
            v.setLabel(label);
        }
    }

    private Matrix ranLabel() throws ColumnOutOfRangeException, RowOutOfRangeException {
        final MatrixFactory mf=MatrixFactory.getInstance();
        Matrix label=mf.creatMatrix(2,1);
        label.set(0, 0, random());
        label.set(1,0,1-label.get(0, 0));
        return label;
    }
}
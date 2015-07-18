/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import static java.lang.Math.log;
import static java.lang.Math.random;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw
 */
public class LabelPropagation implements LabelInference{
    
    private final Graph g;
    private boolean isDone;
    private final double alpha=0.2;
    private final MatrixFactory mf=MatrixFactory.getInstance();
    private Map<Vertex.Type,Collection> vt;
    
    public LabelPropagation(Graph _g) {
        g=_g;
        isDone=false;
    }
    
    @Override
    public Graph getResult() {
        try {
            if(isDone)return g;
            double delta;
            double nuance=0.0001;
            init();
            do {
                delta=update()/g.getVertices().size();
                //System.out.println(delta);
            }while(delta>nuance);
            for(Vertex v:g.getVertices())v.setLabel(v.getLabel().getCol(0));
            return g;
        } catch (ColumnOutOfRangeException | DimensionNotAgreeException | RowOutOfRangeException ex) {
            Logger.getLogger(LabelPropagation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return g;
    }

    private double update() throws DimensionNotAgreeException, ColumnOutOfRangeException {
        double delta=0;
        for(Vertex u:g.getVertices()) {
            if(u.isY0())continue;
            Matrix label=u.getLabel();
            double dataA[][]={{0},{0}};
            double dataB[][]={{0},{0}};
            double dataR[][]={{0,1},{1,0}};
            Matrix a=mf.creatMatrix(dataA);
            Matrix b=mf.creatMatrix(dataB);
            Matrix r=mf.creatMatrix(dataR);
            
            for(Vertex v:u.getNeighbors()) {
                a=a.add(v.getLabel().getCol(0).times(u.getEdge(v)));
                for(Vertex w:v.getNeighbors()) {
                    if(w.getType()!=u.getType())continue;
                    b=b.add(r.times(w.getLabel().getCol(0).times(1.0/v.degree())));
                }
            }
            label.setCol(1, a.orthonormalize().times(1-alpha).add(b.orthonormalize().times(alpha)));
            
            /*
            for(Vertex v:u.getNeighbors()) {
                a=a.add(v.getLabel().getCol(0).times(u.getEdge(v)/u.degree()));
                for(Vertex w:v.getNeighbors()) {
                    if(w.getType()!=u.getType())continue;
                    b=b.add(w.getLabel().getCol(0).times(1/log(v.degree())));
                }
            }
            label.setCol(1, a.times(1-alpha).add(b.times(alpha)).orthonormalize());
            */
            delta+=label.getCol(1).subtract(label.getCol(0)).norm(Matrix.FIRST_NORM);
        }
        for(Vertex u:g.getVertices())
            if(!u.isY0())u.getLabel().setCol(0, u.getLabel().getCol(1));
        return delta;
    }

    private void init() throws ColumnOutOfRangeException, DimensionNotAgreeException, RowOutOfRangeException {
        for(Vertex v:g.getVertices()) {
            Matrix label=mf.creatMatrix(2, 2);
            if(v.isY0())label.setCol(0, v.getLabel());
            else label.setCol(0, ranLabel());
            v.setLabel(label);
        }
    }

    private Matrix ranLabel() throws ColumnOutOfRangeException, RowOutOfRangeException {
        Matrix label=mf.creatMatrix(2,1);
        label.set(0, 0, random());
        label.set(1,0,1-label.get(0, 0));
        return label;
    }
    
    
}
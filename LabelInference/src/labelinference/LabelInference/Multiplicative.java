
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package labelinference.LabelInference;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.Matrix.Matrix;
import labelinference.Matrix.MatrixFactory;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;


/**
 *
 * @author Tangyiqi
 */

public class Multiplicative extends AbstractLabelInference implements LabelInference {
    private Matrix Ya0,Yb0,Yc0;
    private Matrix Ya,Yb,Yc;
    private Matrix Gab,Gbc,Gac;
    private Matrix Sa,Sb,Sc;
    private final Map<Vertex,Integer> v2row=new HashMap();
    
    public Multiplicative(Graph _g) {	
        super(_g);
        initMat();
    }
    
    public Multiplicative(Graph _g, Function<Integer,Matrix> _labelInit) {
        super(_g,_labelInit);
        initMat();
    }
    
    private void initMat() {
        MatrixFactory mf=MatrixFactory.getInstance();
        try {
            int rowA=0,rowB=0,rowC=0;
            final Map<Vertex,Matrix> Y0=new HashMap<>();
            for(Vertex v:g.getVertices())
                if(v.isY0())Y0.put(v, v.getLabel().copy());
            else v.setLabel(labelInit.apply(k));
            for(Vertex u:g.getVertices()) {
                if(u.getType()==Vertex.typeA)rowA++;
                if(u.getType()==Vertex.typeB)rowB++;
                if(u.getType()==Vertex.typeC)rowC++;
            }

            Sa=mf.creatMatrix(rowA,rowA);
            Sb=mf.creatMatrix(rowB,rowB);
            Sc=mf.creatMatrix(rowC,rowC);
            Ya0=mf.creatMatrix(rowA,k);
            Yb0=mf.creatMatrix(rowB,k);
            Yc0=mf.creatMatrix(rowC,k);
            Ya=mf.creatMatrix(rowA,k);
            Yb=mf.creatMatrix(rowB,k);
            Yc=mf.creatMatrix(rowC,k);
            Gab=mf.creatMatrix(rowA,rowB);
            Gbc=mf.creatMatrix(rowB,rowC);
            Gac=mf.creatMatrix(rowA,rowC);

            rowA=0;rowB=0;rowC=0;
            for(Vertex point:g.getVertices()) {
                if(point.getType()==Vertex.typeA) {
                    v2row.put(point,rowA);
                    Ya0.setRow(rowA, point.getLabel().transpose());
                    if(point.isY0())Sa.set(rowA, rowA, 1);
                    rowA++;
                }
                if(point.getType()==Vertex.typeB) {
                    v2row.put(point,rowB);
                    Yb0.setRow(rowB, point.getLabel().transpose());
                    if(point.isY0())Sb.set(rowB, rowB, 1);
                    rowB++;
                }
                if(point.getType()==Vertex.typeC) {
                    v2row.put(point,rowC);
                    Yc0.setRow(rowC, point.getLabel().transpose());
                    if(point.isY0())Sc.set(rowC, rowC, 1);
                    rowC++;
                }
            }
            Ya=Ya0.copy();
            Yb=Yb0.copy();  
            Yc=Yc0.copy(); 

            rowA=0;rowB=0;rowC=0;
            for(Vertex u:g.getVertices()) {
                if(u.getType()==Vertex.typeA) {
                    for(Vertex v:u.getNeighbors())
                        if(v.getType()==Vertex.typeB)Gab.set(rowA,v2row.get(v),u.getEdge(v));
                        else Gac.set(rowA,v2row.get(v),u.getEdge(v));
                    rowA++;
                }
                if(u.getType()==Vertex.typeB) {
                    for(Vertex v:u.getNeighbors())
                        if(v.getType()==Vertex.typeA)Gab.set(v2row.get(v),rowB,u.getEdge(v));
                        else Gbc.set(rowB,v2row.get(v),u.getEdge(v));
                    rowB++;
                }
                if(u.getType()==Vertex.typeC) {
                    for(Vertex v:u.getNeighbors())
                        if(v.getType()==Vertex.typeA)Gac.set(v2row.get(v),rowC,u.getEdge(v));
                        else Gbc.set(v2row.get(v),rowC,u.getEdge(v));
                    rowC++;
                }
            }
        }   catch (RowOutOfRangeException | DimensionNotAgreeException | ColumnOutOfRangeException ex) {
            Logger.getLogger(Multiplicative.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    protected double update(Collection<Vertex> band, Collection<Vertex> bandS, Map<Vertex,Matrix> Y0) throws DimensionNotAgreeException,ColumnOutOfRangeException, RowOutOfRangeException 
    {
        MatrixFactory mf;
        mf=MatrixFactory.getInstance();
        Matrix Ya_new;
        Matrix Yb_new;
        Matrix Yc_new;

        Matrix temp_a_up=((Gab.times(Yb)).add(Gac.times(Yc))).add(Sa.times(Ya0));
        Matrix temp_a_down=Ya.times(Yb.transpose()).times(Yb).add(Ya.times(Yc.transpose()).times(Yc)).add(Sa.times(Ya));
        Ya_new=Ya.cron(temp_a_up.divide(temp_a_down).sqrt()).transpose().normalize().transpose();

        Matrix temp_b_up=((Gab.transpose()).times(Ya)).add(Gbc.times(Yc)).add(Sb.times(Yb0));
        Matrix temp_b_down=Yb.times(Ya.transpose()).times(Ya).add(Yb.times(Yc.transpose()).times(Yc)).add(Sb.times(Yb));
        Yb_new=Yb.cron(temp_b_up.divide(temp_b_down).sqrt()).transpose().normalize().transpose();

        Matrix temp_c_up=Gac.transpose().times(Ya).add(Gbc.transpose().times(Yb)).add(Sc.times(Yc0));
        Matrix temp_c_down=Yc.times(Ya.transpose()).times(Ya).add(Yc.times(Yb.transpose()).times(Yb)).add(Sc.times(Yc));
        Yc_new=Yc.cron(temp_c_up.divide(temp_c_down).sqrt()).transpose().normalize().transpose();

        double delta=Ya.subtract(Ya_new).norm(Matrix.FROBENIUS_NORM)+Yb.subtract(Yb_new).norm(Matrix.FROBENIUS_NORM)+Yc.subtract(Yc_new).norm(Matrix.FROBENIUS_NORM);
        Ya=Ya_new.copy();
        Yb=Yb_new.copy();
        Yc=Yc_new.copy();
        for(Vertex v:g.getVertices()){
            if(v.getType()==Vertex.typeA)v.setLabel(Ya.getRow(v2row.get(v)).transpose());
            if(v.getType()==Vertex.typeB)v.setLabel(Yb.getRow(v2row.get(v)).transpose());
            if(v.getType()==Vertex.typeC)v.setLabel(Yc.getRow(v2row.get(v)).transpose());
        }
        return delta;
    }
}

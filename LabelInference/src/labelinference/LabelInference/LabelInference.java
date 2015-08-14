/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import static java.lang.Math.pow;
import static java.lang.Math.random;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import labelinference.Graph.Vertex;
import labelinference.Matrix.MatrixFactory;
import labelinference.Matrix.Matrix;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
*
* @author sailw
* 
* @since 1.8

* TODO To implement initializations, counting the objective and writing the information of iteration procedure.

* @see labelinference.AbstractLabelInference.AbstractLabelInference(Graph _g)
*/
public interface LabelInference {
	
    /** TODO To declare the getResult function*/	
    void getResult(int maxIter, double nuance, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException;
    /** TODO To declare the increase function */	
    void increase(Collection<Vertex> deltaGraph, int maxIter, double nuance, double a, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException;
    
    /** 
     * @param k:the number of clusters
     * TODO To give a unified initialization of label 
     */	
    public static void defaultLabelInit(Matrix label, Integer k) {
        final MatrixFactory mf=MatrixFactory.getInstance();
        for(int i=0;i<k;i++)try {
            label.set(i, 0, 1.0/k);//set the label to (1/k .... 1/k)
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
    }
    
    /** TODO To initialize the Y matrix with random numbers*/	
    public static void randomLabelInit(Matrix label, Integer k) {
        final MatrixFactory mf=MatrixFactory.getInstance();
        for(int i=0;i<k;i++)try {
            label.set(i, 0, random());//set the label to a random number
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        label=label.normalize();
    }
    	
    public static void NoneInit(Matrix label, Integer k) {}
    
    /** TODO To count the objective number*/	
    public static double objective(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0, Map<Vertex.Type,Map<Vertex.Type,Matrix>> B, int k) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
    	Double obj=0.0;
        for(Vertex v:cand) {
            for(Vertex u:v.getNeighbors())
                obj+=pow(v.getEdge(u)-v.getLabel().transpose().times(B.get(u.getType()).get(v.getType())).times(u.getLabel()).get(0, 0),2);
            if(v.isY0())obj+=pow(Y0.get(v).subtract(v.getLabel()).norm(Matrix.FROBENIUS_NORM),2);
        }
        //obj=\Sigma {(G(u,v)-Y(u)^T*B_{t(u)t(v)}*Y(v))}+1_{YL(u)}*\|Y0(v)-Y(v)\|_F^2  (v \in  cand,u \in N(v))
        return obj;
    }

    final int DISP_ITER=1;
    final int DISP_DELTA=2;
    final int DISP_OBJ=4;
    final int DISP_TIME=8;
    final int DISP_LABEL=16;
    final int DISP_SIZE=32;
    final int DISP_ALL=255;
    final int DISP_NONE=0;
    
    /** 
     * 
     * @param disp:to select which of the outputs would be chose to write, other parameter are for displaying
     * TODO To displays all the needed information
     */	
    public static void infoDisplay(int disp, int iter, double delta, double time, Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0, Map<Vertex.Type,Map<Vertex.Type,Matrix>> B, int k) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
        if((disp&DISP_ITER)!=0)System.out.print(String.format("Iter = %d\n",iter));
        if((disp&DISP_SIZE)!=0)System.out.print(String.format("Size = %d\n",cand.size()));
        if((disp&DISP_DELTA)!=0)System.out.print(String.format("Delta = %.6f\n",delta));
        if((disp&DISP_OBJ)!=0)System.out.print(String.format("ObjValue = %.6f\n",LabelInference.objective(cand,candS,Y0,B,k)));
        if((disp&DISP_LABEL)!=0)for(Vertex v:cand) {
            System.out.print(v.getId()+v.getLabel().toString()+"\n"); 
        }
        if((disp&DISP_TIME)!=0)System.out.print(String.format("Processed in %.3f ms\n",time));
    }
}
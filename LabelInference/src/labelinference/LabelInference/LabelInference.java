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
 */

/*
 * This class is the abstract class giving methods inplementing initializations, counting the objective and writing the information of iteration procedure.
 */
public interface LabelInference {
    void getResult(int maxIter, double nuance, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException;
    //declare the getResult function
    void increase(Collection<Vertex> deltaGraph, int maxIter, double nuance, double a, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException;
    //declare the increase function
    
    public static void defaultLabelInit(Matrix label, Integer k) {
    //this method giving a unified initialization of label
        final MatrixFactory mf=MatrixFactory.getInstance();
        for(int i=0;i<k;i++)try {
            label.set(i, 0, 1.0/k);//set the label to (1/k .... 1/k)
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
    }
    
    public static void randomLabelInit(Matrix label, Integer k) {
    //this method giving a unified initialization of label
        final MatrixFactory mf=MatrixFactory.getInstance();
        for(int i=0;i<k;i++)try {
            label.set(i, 0, random());//set the label to (1/k .... 1/k)
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        label=label.normalize();
    }
    
    public static void LPInit(Matrix label, Integer k) {}
    
    public static double objective(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0, Map<Vertex.Type,Map<Vertex.Type,Matrix>> B, int k) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
    //this method counts the objective number
    	Double obj=0.0;
        for(Vertex v:cand) {
            for(Vertex u:v.getNeighbors())
                obj+=pow(v.getEdge(u)-v.getLabel().transpose().times(B.get(u.getType()).get(v.getType())).times(u.getLabel()).get(0, 0),2);
            if(v.isY0())obj+=pow(Y0.get(v).subtract(v.getLabel()).norm(Matrix.FROBENIUS_NORM),2);
        }
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
    //define all possible situation for displaying
    
    public static void infoDisplay(int disp, int iter, double delta, double time, Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0, Map<Vertex.Type,Map<Vertex.Type,Matrix>> B, int k) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
    //disp selects which of the outputs would be chose to write, other parameter are for displaying
    //this method displays all the needed information
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

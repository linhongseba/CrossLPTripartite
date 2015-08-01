/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import static java.lang.Math.pow;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import labelinference.Graph.Graph;
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
    void getResult(int maxIter, double nuance, int disp);
    //declare the getResult function
    void increase(Collection<Vertex> deltaGraph, int maxIter, double nuance, double a, int disp);
    //declare the increase function
    
    public static Matrix defaultLabelInit(Integer k) {
    //this method giving a unified initialization of label
        final MatrixFactory mf=MatrixFactory.getInstance();
        Matrix label=mf.creatMatrix(k,1);
        for(int i=0;i<k;i++)try {
            label.set(i, 0, 1.0/k);//set the label to (1/k .... 1/k)
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        return label;
    }
    
    public static double objective(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0, Map<Vertex.Type,Map<Vertex.Type,Matrix>> B, int k) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
    //this method counts the objective number
    	Double obj=0.0;
        Map<Vertex.Type,Double>[][] sum=new Map[k][k];
        for(int i=0;i<k;i++)
            for(int j=0;j<k;j++) {
                sum[i][j]=new HashMap<>();
                for(Vertex v:candS)sum[i][j].put(v.getType(), sum[i][j].getOrDefault(v.getType(), 0.0)+v.getLabel().get(i, 0)*v.getLabel().get(j, 0)); 
            }
        for(int i=0;i<k;i++)for(int j=0;j<k;j++)for(int m=0;m<k;m++)for(int n=0;n<k;n++)for(Vertex.Type t0:Vertex.types)for(Vertex.Type t1:Vertex.types)if(t0!=t1)
            obj+=sum[i][m].get(t0)*sum[j][n].get(t1)*B.get(t0).get(t1).get(i,j)*B.get(t0).get(t1).get(m,n);

        for(Vertex v:cand) {
            for(Vertex u:v.getNeighbors())
                obj+=pow(v.getEdge(u)-v.getLabel().transpose().times(u.getLabel()).get(0, 0),2)-pow(v.getLabel().transpose().times(u.getLabel()).get(0, 0),2);
            if(v.isY0())obj+=pow(Y0.get(v).subtract(v.getLabel()).norm(Matrix.FROBENIUS_NORM),2);
        }
        return obj;
    }

    final int DISP_ITER=1;
    final int DISP_DELTA=2;
    final int DISP_OBJ=4;
    final int DISP_TIME=8;
    final int DISP_LABEL=16;
    final int DISP_ALL=255;
    final int DISP_NONE=0;
    //define all possible situation for displaying
    
    public static void infoDisplay(int disp, int iter, double delta, double time, Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0, Map<Vertex.Type,Map<Vertex.Type,Matrix>> B, int k) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
    //disp selects which of the outputs would be chose to write, other parameter are for displaying
    //this method displays all the needed information
        if((disp&DISP_ITER)!=0)System.out.print(String.format("Iter = %d\n",iter));
        if((disp&DISP_DELTA)!=0)System.out.print(String.format("Delta = %.6f\n",delta));
        if((disp&DISP_OBJ)!=0)System.out.print(String.format("ObjValue = %.6f\n",LabelInference.objective(cand,candS,Y0,B,k)));
        if((disp&DISP_LABEL)!=0)for(Vertex v:cand) {
            System.out.print(v.getId()+v.getLabel().toString()+"\n"); 
        }
        if((disp&DISP_TIME)!=0)System.out.print(String.format("Processed in %.3f ms\n",time));
    }
}

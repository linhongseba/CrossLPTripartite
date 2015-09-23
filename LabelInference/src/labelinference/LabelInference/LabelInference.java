package labelinference.LabelInference;

import static java.lang.Math.pow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.Matrix.Matrix;
import labelinference.Matrix.MatrixFactory;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
*
* @author sailw
* 
* @since 1.8

* implement initializations, counting the objective and writing the information of iteration procedure

* @see labelinference.AbstractLabelInference.AbstractLabelInference(Graph _g)
*/
public interface LabelInference {
    void getResult(int maxIter, double nuance, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException;
    void increase(Collection<Vertex> deltaGraph, int maxIter, double nuance, double a, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException;
    void recompute(Collection<Vertex> deltaGraph, int maxIter, double nuance, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException;
    
    /**
     * @param label
     * @param k:the number of clusters
     * unified initialization of label 
     */	
    public static void defaultLabelInit(Matrix label, Integer k) {
        for(int i=0;i<k;i++)try {
            label.set(i, 0, 1.0/k);//set the label to (1/k .... 1/k)
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
    }
    public static BiConsumer<Matrix,Integer> defaultLabelInit=LabelInference::defaultLabelInit;
    
    /** initialize Y matrix with random numbers*/
    static Random random=new Random(1008611);
    public static void randomLabelInit(Matrix label, Integer k) {
        try {
            for(int i=0;i<k;i++)label.set(i, 0, random.nextDouble());//set the label to a random number
            label.normalize_assign();
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
    }
    public static BiConsumer<Matrix,Integer> randomLabelInit=LabelInference::randomLabelInit;
    
    public static void noneInit(Matrix label, Integer k) {}
    public static BiConsumer<Matrix,Integer> noneInit=LabelInference::noneInit;
    
    /** count objective numbe
     * @param cand
     * @param candS
     * @param Y0
     * @param B
     * @param k	
     * @return 	
     * @throws labelinference.exceptions.ColumnOutOfRangeException	
     * @throws labelinference.exceptions.RowOutOfRangeException	
     * @throws labelinference.exceptions.DimensionNotAgreeException*/	
    public static double objective(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0, Map<Vertex.Type,Map<Vertex.Type,Matrix>> B, int k) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
    	Double obj=0.0;
    	Double lableObj = 0.0;

        Map<Vertex.Type,Double>[][] sum=new Map[k][k];
        for(int i=0;i<k;i++)
            for(int j=0;j<k;j++) {
                sum[i][j]=new HashMap<>();
                for(Vertex.Type t:Vertex.types)sum[i][j].put(t, 0.0);
                for(Vertex v:candS)sum[i][j].put(v.getType(), sum[i][j].get(v.getType())+v.getLabel().get(i, 0)*v.getLabel().get(j, 0)); 
            }
        for(int i=0;i<k;i++)for(int j=0;j<k;j++)for(int m=0;m<k;m++)for(int n=0;n<k;n++)for(Vertex.Type t0:Vertex.types)for(Vertex.Type t1:Vertex.types)if(t0!=t1)
            obj+=sum[i][m].get(t0)*sum[j][n].get(t1)*B.get(t0).get(t1).get(i,j)*B.get(t0).get(t1).get(m,n);
        
    	//obj=\Sigma {(G(u,v)-Y(u)^T*B_{t(u)t(v)}*Y(v))}+1_{YL(u)}*\|Y0(v)-Y(v)\|_F^2  (v \in  cand,u \in N(v))
        for(Vertex v:cand) {
            for(Vertex u:v.getNeighbors()) {
                obj+=pow(v.getEdge(u)
                        -v.getLabel().transpose()
                        .times_assign(B.get(u.getType()).get(v.getType()))
                        .times_assign(u.getLabel()).get(0, 0),2)
                    -pow(v.getLabel().transpose()
                        .times_assign(B.get(u.getType()).get(v.getType()))
                        .times_assign(u.getLabel()).get(0, 0),2);
            }
            if(v.isY0()) lableObj+=pow(Y0.get(v).subtract(v.getLabel()).norm(Matrix.FROBENIUS_NORM),2);
        }
        obj = Math.sqrt(obj);
        return obj + lableObj;
    }

    /**
     * Full obj
     * @param cand
     * @param candS
     * @param Y0
     * @param B
     * @param k
     * @return
     * @throws ColumnOutOfRangeException
     * @throws RowOutOfRangeException
     * @throws DimensionNotAgreeException
     */
    public static double objectiveFull(Graph g, Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0, Map<Vertex.Type,Map<Vertex.Type,Matrix>> B, int k) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
        Double lableObj = 0.0;
        
        MatrixFactory matrixFactory = MatrixFactory.getInstance();
        Matrix lableA = matrixFactory.creatMatrix(k, g.cntA);
        Matrix lableB = matrixFactory.creatMatrix(k, g.cntB);
        Matrix lableC = matrixFactory.creatMatrix(k, g.cntC);
        
        int num = 0;
        for (Vertex a: g.aMap.keySet()) {
            lableA.setCol(num, a.getLabel());
            num++;
        }
        
        num = 0;
        for (Vertex b: g.bMap.keySet()) {
            lableB.setCol(num, b.getLabel());
            num++;
        }
        
        num = 0;
        for (Vertex c: g.cMap.keySet()) {
            lableC.setCol(num, c.getLabel());
            num++;
        }
        
        double abObj = Math.pow(g.mAB.subtract(lableA.transpose().times(B.get(Vertex.typeA).get(Vertex.typeB)).times(lableB))
                .norm(Matrix.FROBENIUS_NORM), 2);
        
        double acObj = Math.pow(g.mAC.subtract(lableA.transpose().times(B.get(Vertex.typeA).get(Vertex.typeC)).times(lableC))
                .norm(Matrix.FROBENIUS_NORM), 2);
        
        double bcObj = Math.pow(g.mBC.subtract(lableB.transpose().times(B.get(Vertex.typeB).get(Vertex.typeC)).times(lableC))
                .norm(Matrix.FROBENIUS_NORM), 2);
        
        abObj = Math.sqrt(abObj/(g.cntA*g.cntB));
        acObj = Math.sqrt(acObj/(g.cntA*g.cntC));
        bcObj = Math.sqrt(bcObj/(g.cntB*g.cntC));
        
        for(Vertex v:cand) {
            if(v.isY0()) lableObj += pow(Y0.get(v).subtract(v.getLabel()).norm(Matrix.FROBENIUS_NORM),2);
        }
        
        return abObj + acObj + bcObj + lableObj;
    }
    
    final int DISP_ITER=1;
    final int DISP_DELTA=2;
    final int DISP_OBJ=4;
    final int DISP_TIME=8;
    final int DISP_LABEL=16;
    final int DISP_SIZE=32;
    final int DISP_B=64;
    final int DISP_ALL=255;
    final int DISP_NONE=0;
    
    /** 
     * 
     * @param disp:to select which of the outputs would be chose to write, other parameter are for displaying
     * TODO To displays all the needed information
     * @param iter
     * @param delta
     * @param time
     * @param cand
     * @param candS
     * @param Y0
     * @param B
     * @param k
     * @param obj
     * @throws labelinference.exceptions.ColumnOutOfRangeException
     * @throws labelinference.exceptions.RowOutOfRangeException
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */	
    public static void infoDisplay(int disp, int iter, double delta, double time, Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0, Map<Vertex.Type,Map<Vertex.Type,Matrix>> B, int k,double obj) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
        if((disp&DISP_ITER)!=0)System.out.print(String.format("Iter = %d\n",iter));
        if((disp&DISP_SIZE)!=0)System.out.print(String.format("Size = %d\n",cand.size()));
        if((disp&DISP_DELTA)!=0)System.out.print(String.format("Delta = %.6f\n",delta));
        if((disp&DISP_OBJ)!=0)System.out.print(String.format("ObjValue = %.6f\n",obj));
        if((disp&DISP_LABEL)!=0)for(Vertex v:cand) {
            System.out.print(v.getId()+v.getLabel().toString()+"\n"); 
        }
        if((disp&DISP_B)!=0) {
            System.out.print("B =\n");
            for(Vertex.Type t0:Vertex.types)
                for(Vertex.Type t1:Vertex.types)if(t0!=t1)
                    System.out.print(B.get(t0).get(t1).toString()+"\n");
        }
        if((disp&DISP_TIME)!=0)System.out.print(String.format("Processed in %.3f ms(update only)\n",time));
    }
}
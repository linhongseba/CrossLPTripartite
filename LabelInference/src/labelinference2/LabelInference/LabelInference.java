package labelinference2.LabelInference;

import static java.lang.Math.pow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.Matrix.Matrix;
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
    void SetBeta(double _beta);
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
    public static double objective(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0, Map<Vertex.Type,Map<Vertex.Type,Matrix>> B, int k, double beta) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
    	double obj=0.0;
    	double labelObj = 0.0;
        //MatrixFactory mf=MatrixFactory.getInstance();
        Map<Vertex.Type,Double>[][] sum=new Map[k][k];
        for(int i=0;i<k;i++){
            for(int j=0;j<k;j++) {
                sum[i][j]=new HashMap<>();
                for(Vertex.Type t:Vertex.types)
                    sum[i][j].put(t, 0.0);
                for(Vertex v:candS){
                    //if(v.getLabel().get(i, 0)>1.0e-9)
                        //System.out.println("label information is "+v.getLabel().get(i, 0));
                    double value=sum[i][j].get(v.getType());
                    value+=(v.getLabel().get(i, 0)*v.getLabel().get(j, 0));
                    sum[i][j].put(v.getType(), value); 
                }
            }
        }
        for(int i=0;i<k;i++){
            for(int j=0;j<k;j++){
                for(int m=0;m<k;m++){
                    for(int n=0;n<k;n++){
                        for(Vertex.Type t0:Vertex.types){
                            for(Vertex.Type t1:Vertex.types){
                                if(t0!=t1){
                                    double v=sum[i][m].get(t0)*sum[j][n].get(t1)*B.get(t0).get(t1).get(i,j)*B.get(t0).get(t1).get(m,n);
                                    obj+=v;
                                }
                            }
                        }
                    }
                }
            }
        }
        //System.out.println(obj);
        
    	//obj=\Sigma {(G(u,v)-Y(u)^T*B_{t(u)t(v)}*Y(v))}+1_{YL(u)}*\|Y0(v)-Y(v)\|_F^2  (v \in  cand,u \in N(v))
       for(Vertex v:cand) {
            for(Vertex u:v.getNeighbors()) 
                {
                Matrix temp=v.getLabel().transpose();
                temp.times_assign(B.get(v.getType()).get(u.getType()));
                temp.times_assign(u.getLabel());
                obj+=(pow(v.getEdge(u)-temp.get(0, 0),2)-pow(temp.get(0, 0),2));
            }
            if(v.isY0()) labelObj+=pow(Y0.get(v).subtract(v.getLabel()).norm(Matrix.FROBENIUS_NORM),2)*beta;
        }
        obj+=labelObj;
        //System.out.print("SquaredObj="+obj+"\n"); 
        obj=Math.sqrt(obj);
        return obj;
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
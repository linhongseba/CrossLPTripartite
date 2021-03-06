package labelinference2.LabelInference;

import static java.lang.Math.max;
import static java.lang.Math.max;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.Matrix.Matrix;
import labelinference.Matrix.MatrixFactory;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;
import static labelinference2.LabelInference.LabelInference.*;
import static labelinference2.LabelInference.LabelInference.DISP_TIME;

/**
*
* @author sailw
* @since 1.8
* The framework of the overall procedure of incremental and non-incremental algorithms
* @see labelinference.Experiment.class#main(String[])
* 
*/
public abstract class AbstractLabelInference implements LabelInference{
    protected final Graph g;                                                             
    protected final BiConsumer<Matrix,Integer> labelInit;    
    protected final int k; //the variable denotes the number of clusters
    protected Map<Vertex.Type,Map<Vertex.Type,Matrix>> B=new HashMap<>();
    Map<Vertex,Matrix> Y;
    double beta;
    
    /**
    * 
    * @param _g:initial graph g with _g
    */	
    public AbstractLabelInference(Graph _g) {	
        this(_g,LabelInference.defaultLabelInit);
    }
    
    /**
    * 
    * @param _g:initial graph g with _g
    * @param _labelInit: initial labeled vertices
    */	    
    public AbstractLabelInference(Graph _g, BiConsumer<Matrix,Integer> _labelInit) {
            g=_g;
            k=g.getNumLabels();
            System.out.println("number of labels is "+k);
            labelInit=_labelInit;
            beta=1.0;
            for(Vertex.Type t0:Vertex.types) {
                B.put(t0, new HashMap<>());
             
                for(Vertex.Type t1:Vertex.types){
                    B.get(t0).put(t1,MatrixFactory.getInstance().identityMatrix(k));
                }
                
            }
            try{
            for(Vertex u:g.getVertices(v->v.isY0())){
                for(Vertex v:u.getNeighbors())if(v.isY0()){
                    B.get(u.getType()).get(v.getType()).add_assign(
                            u.getLabel()
                                    .times(v.getLabel().transpose()));
                    //B.get(v.getType()).get(u.getType()).add_assign(u.getLabel()
                                    //.times(v.getLabel().transpose()));
                }
            }
            
            for(Vertex.Type t0:Vertex.types) {
                for(Vertex.Type t1:Vertex.types){
                    B.get(t0).get(t1).maxnorm_assign();
                    //System.out.println("Initilization of B");
                   // System.out.println(B.get(t0).get(t1));
                }
            }
        } catch (DimensionNotAgreeException ex) {
            Logger.getLogger(AbstractLabelInference.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    @Override
    public void SetBeta(double _beta){
        this.beta=_beta;
    }
    /**
    * 
    * @param maxIter: the max iterations times
    * @param nuance: a tiny number control when the procedure ends
    * @param disp: a code choose what to display
    * overall procedure of the basic non-incremental algorithms
     * @throws labelinference.exceptions.DimensionNotAgreeException
     * @throws labelinference.exceptions.RowOutOfRangeException
     * @throws labelinference.exceptions.ColumnOutOfRangeException
    */	
    @Override
    public void getResult(int maxIter, double nuance, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {                                       
        final Map<Vertex,Matrix> Y0=init(g.getVertices(),g.getVertices(), labelInit, g.getNumLabels());   
        double timeUsed=0;
        
        double delta; //the variable denotes the difference between Y and last produced Y.
        double oldObj=0;
        double obj;
        double deltaObj=0;
//        for(Vertex u:g.getVertices()){
//            Matrix temp=mf.creatMatrix(2, 1);
//            temp.Setdefault();
//            if(u.isY0()==true){
//                u.setLabel(temp);
//            }
//        }
        for(Vertex u:g.getVertices()) {
            u.setTempLabelRef(u.getLabel().copy());
        }
        int iter=0; //the variable controls the iteration times.
        oldObj = LabelInference.objective(g.getVertices(), g.getVertices(), Y0, B, k,beta);
        Y=new HashMap<>(g.getVertices().size());
        for(Vertex u:g.getVertices()) {
            Y.put(u,u.getLabel().copy());
        }
        LabelInference.infoDisplay(disp&(DISP_ITER|DISP_OBJ|DISP_B), iter, 0, 0, g.getVertices(),g.getVertices(), Y0,B,k,oldObj);
        do {
            long nTime=System.nanoTime();
            long mTime=System.currentTimeMillis();
            updateB(g.getVertices(),g.getVertices());
            updateY(g.getVertices(),g.getVertices(),Y0); 
            delta=0;
            for(Vertex u:g.getVertices()) {
                //if(iter==0)
                //    System.out.print(u.getLabel().toString()+"\n");
                delta+=u.getLabel().subtract(Y.get(u)).norm(Matrix.FIRST_NORM);
                u.setLabel(Y.get(u));
            }
            //LabelInference.infoDisplay(disp&~DISP_TIME&~DISP_B&~DISP_LABEL, iter, delta, timeUsed,g.getVertices(),g.getVertices(), Y0,B,k,oldObj);
            obj=LabelInference.objective(g.getVertices(), g.getVertices(), Y0, B, k,beta);
            deltaObj = Math.abs(oldObj-obj)/g.getVertices().size();
            //System.out.println("deltaObj is "+deltaObj);
            oldObj = obj;
            if (iter>0 && ((delta<=nuance )||(deltaObj<=nuance)))
            {
                System.out.print(String.format("Result@_@Delta = %.6f\n",delta));
                System.out.print(String.format("Result@_@Delta = %.6f\n",deltaObj));
                break;
            }
            nTime=System.nanoTime()-nTime;
            mTime=System.currentTimeMillis()-mTime;
            timeUsed+=max(mTime,nTime/1000000.0);
            iter++;
            if(iter<10){
                    if(iter==1||iter==5)
                        LabelInference.infoDisplay(disp&~DISP_TIME&~DISP_B&~DISP_LABEL, iter, delta, timeUsed, g.getVertices(),g.getVertices(), Y0,B,k,obj);
                }else{
                    if(iter%10==0)
                        LabelInference.infoDisplay(disp&~DISP_TIME&~DISP_B&~DISP_LABEL, iter, delta, timeUsed, g.getVertices(),g.getVertices(), Y0,B,k,obj);
                }
            //LabelInference.infoDisplay(disp&~DISP_TIME&~DISP_B&~DISP_LABEL, iter, delta, timeUsed,g.getVertices(),g.getVertices(), Y0,B,k,obj);
        } while(iter!=maxIter);
        for(Vertex u:g.getVertices()){
            u.setLabel(Y.get(u).normone_assign());
        }
        LabelInference.infoDisplay(disp&(DISP_TIME|DISP_B|DISP_LABEL), iter, delta, timeUsed, g.getVertices(),g.getVertices(), Y0,B,k,obj);
    }
    
    /**
     *
     * @param deltaGraph
     * @param maxIter
     * @param nuance
     * @param disp
     * @throws DimensionNotAgreeException
     * @throws RowOutOfRangeException
     * @throws ColumnOutOfRangeException
     */
    @Override
    public void recompute(Collection<Vertex> deltaGraph, int maxIter, double nuance, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {
        for(Vertex u:deltaGraph) {
            g.addVertex(u);
            for(Vertex v:u.getNeighbors())
                v.addEdge(u, u.getEdge(v));
        }
        final Map<Vertex,Matrix> Y0=init(deltaGraph,g.getVertices(), labelInit, g.getNumLabels());
        for(Vertex v:g.getVertices(v->v.isY0()))Y0.put(v, v.getLabel());
        double timeUsed=0;                                                                
        double delta; //the variable denotes the difference between Y and last produced Y.
        double oldObj=0;
        double deltaObj;
        double obj;
        for(Vertex u:g.getVertices()) {
            u.setTempLabelRef(u.getLabel().copy());
        }
        int iter=0; //the variable controls the iteration times.
        oldObj = LabelInference.objective(g.getVertices(), g.getVertices(), Y0, B, k,beta);
        Y=new HashMap<>(g.getVertices().size());
        for(Vertex u:g.getVertices()) {
            Y.put(u,u.getLabel().copy());
        }
        LabelInference.infoDisplay(disp&(DISP_ITER|DISP_OBJ), iter, 0, 0, g.getVertices(),g.getVertices(), Y0,B,k,oldObj);
        do {
            long nTime=System.nanoTime();
            long mTime=System.currentTimeMillis();
            updateB(g.getVertices(),g.getVertices());
            updateY(g.getVertices(),g.getVertices(),Y0);
            delta=0;
            for(Vertex u:g.getVertices()) {
                delta+=u.getLabel().subtract(Y.get(u)).norm(Matrix.FIRST_NORM);
                u.setLabel(Y.get(u));
            }
            obj = LabelInference.objective(g.getVertices(), g.getVertices(), Y0, B, k,beta);
            deltaObj =Math.abs(oldObj-obj)/g.getVertices().size();
            oldObj = obj;
            if (iter>0 && ((delta<=nuance )||(deltaObj<=nuance))){
                System.out.print(String.format("Result@_@Delta = %.6f\n",delta));
                System.out.print(String.format("Result@_@Delta = %.6f\n",deltaObj));
                break;
            }
            nTime=System.nanoTime()-nTime;
            mTime=System.currentTimeMillis()-mTime;
            timeUsed+=max(mTime,nTime/1000000.0);
            iter++;
            LabelInference.infoDisplay(disp&~DISP_TIME&~DISP_B&~DISP_LABEL, iter, delta, timeUsed,g.getVertices(),g.getVertices(), Y0,B,k,obj);
        } while(iter!=maxIter);
        for(Vertex u:g.getVertices()){
            u.setLabel(Y.get(u).normone_assign());
        }
        LabelInference.infoDisplay(disp&(DISP_TIME|DISP_B|DISP_LABEL), iter, delta, timeUsed, g.getVertices(),g.getVertices(), Y0,B,k,obj);
    }
    
    /**
    * 
    * @param deltaGraph: the additional graph
    * @param maxIter: the max iterations times
    * @param nuance: a tiny number control when the procedure ends
    * @param a: conffidence level
    * @param disp: a code choose what to display
    * overall procedure of the basic non-incremental algorithms
     * @throws labelinference.exceptions.DimensionNotAgreeException
     * @throws labelinference.exceptions.RowOutOfRangeException
     * @throws labelinference.exceptions.ColumnOutOfRangeException
    */	
    @Override
    public void increase(Collection<Vertex> deltaGraph, int maxIter, double nuance, double a, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {
            Map<Vertex.Type,Map<Vertex.Type,Double>> w=new HashMap<>();
            Map<Vertex.Type,Map<Vertex.Type,Double>> sigma=new HashMap<>();
            Map<Vertex.Type,Map<Vertex.Type,Double>> tot=new HashMap<>();
            for(Vertex.Type type:Vertex.types) {
                w.put(type, new HashMap<>());
                sigma.put(type, new HashMap<>());
                tot.put(type, new HashMap<>());
            }
            for(Vertex u:g.getVertices()) {
                if(deltaGraph.contains(u))continue;
                for(Vertex v:u.getNeighbors()) {
                    if(deltaGraph.contains(v))continue;
                    sigma.get(u.getType()).put(v.getType(), 
                        sigma.get(u.getType()).getOrDefault(v.getType(),0.0)
                            +pow(u.getLabel().transpose()
                            .times_assign(B.get(u.getType()).get(v.getType()))
                            .times_assign(v.getLabel()).get(0, 0),2));
                    w.get(u.getType()).put(v.getType(), 
                        w.get(u.getType()).getOrDefault(v.getType(),0.0)
                            +u.getLabel().transpose()
                            .times_assign(B.get(u.getType()).get(v.getType()))
                            .times_assign(v.getLabel()).get(0, 0));
                    tot.get(u.getType()).put(v.getType(), tot.get(u.getType()).getOrDefault(v.getType(),0.0)+1);
                }
            }
            for(Vertex.Type type0:Vertex.types)
                for(Vertex.Type type1:Vertex.types)if(type0!=type1) {
                    w.get(type0).put(type1, w.get(type0).getOrDefault(type1, 0.0)/tot.get(type0).getOrDefault(type1,0.0));
                    sigma.get(type0).put(type1, sqrt((sigma.get(type0).getOrDefault(type1, 0.0)/tot.get(type0).getOrDefault(type1,0.0)-w.get(type0).get(type1)*w.get(type0).get(type1))/(1-a)));
                    //square sigma and 1/(1-a) at the same time
                }
            //get \sigma_{tt'}*\sqrt{1/(1-a)}

            final Collection<Vertex> cand=new HashSet<>(deltaGraph);
            final Collection<Vertex> candS=new HashSet<>(cand);//additional graph with its related vertices
            for(Vertex u:cand) {
                g.addVertex(u);
                for(Vertex v:u.getNeighbors()) {
                    candS.add(v);
                    v.addEdge(u, u.getEdge(v));
                }
            }
            final Map<Vertex,Matrix> Y0=init(cand, candS, labelInit, g.getNumLabels());//original state of labels
            
            double timeUsed=0;                                                        
            double delta; 
            double oldObj=0;
            double obj;
            for(Vertex u:g.getVertices()) {
                u.setTempLabelRef(u.getLabel().copy());
            }
            int iter=0; //the variable controls the iteration times.
            oldObj = LabelInference.objective(cand, candS, Y0, B, k,beta);
            Y=new HashMap<>(g.getVertices().size());
            for(Vertex u:g.getVertices()) {
                Y.put(u,u.getLabel().copy());
            }
            do {
                long nTime=System.nanoTime();
                long mTime=System.currentTimeMillis();
                updateY(cand,candS,Y0);
                for(Vertex u:cand)
                    u.setLabel(Y.get(u));
                obj=LabelInference.objective(cand, candS, Y0, B, k,beta);
                delta=Math.abs(oldObj-obj)/cand.size();
                oldObj=obj;
                Collection deltaCand=new HashSet<>();
                for(Vertex u:cand)
                    for(Vertex v:u.getNeighbors()) if(!cand.contains(v)) {
                        
                        if(Math.abs(u.getLabel().transpose().times_assign(B.get(u.getType()).get(v.getType())).times_assign(u.getLabel()).get(0, 0)
                                -w.get(u.getType()).get(v.getType()))>sigma.get(u.getType()).get(v.getType())) {
                            deltaCand.add(v);
                            v.getNeighbors().forEach(candS::add);
                            if(v.isY0())Y0.put(v, v.getLabel().copy());
                        }
                    }
                //if \|Y(u)^{T}*Y(v)-w_{t(u)t(v)}\|>\sigma_{t(u)t(v)}*\sqrt{\frac{1}{1-\alpha}} , then add v to cand
                cand.addAll(deltaCand);
                nTime=System.nanoTime()-nTime;
                mTime=System.currentTimeMillis()-mTime;
                timeUsed+=max(mTime,nTime/1000000.0);
                iter++;
                if(iter<10){
                    if(iter==1||iter==5)
                        LabelInference.infoDisplay(disp&~DISP_TIME&~DISP_B&~DISP_LABEL, iter, delta, timeUsed, cand,candS, Y0,B,k,obj);
                }else{
                    if(iter%10==0)
                        LabelInference.infoDisplay(disp&~DISP_TIME&~DISP_B&~DISP_LABEL, iter, delta, timeUsed, cand,candS, Y0,B,k,obj);
                }
            } while(delta>nuance && iter!=maxIter);
            for(Vertex u:g.getVertices()){
                u.setLabel(Y.get(u).normone_assign());
            }
            LabelInference.infoDisplay(disp&(DISP_TIME|DISP_B|DISP_LABEL), iter, delta, timeUsed, cand,candS, Y0,B,k,0);
    }
    
    /**
     * 
     * @param cand
     * @param candS
     * @param labelInit: function giving a initialized k*k matrix
     * @param k: the number of clusters
     * TODO To implement the overall procedure of the basic non-incremental algorithms
     * @return 
     * @throws labelinference.exceptions.ColumnOutOfRangeException 
     * @throws labelinference.exceptions.RowOutOfRangeException 
     * @throws labelinference.exceptions.DimensionNotAgreeException 
     */	
    public Map<Vertex,Matrix> init(Collection<Vertex> cand, Collection<Vertex> candS, BiConsumer<Matrix,Integer> labelInit, int k) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
        final Map<Vertex,Matrix> Y0=new HashMap<>();
        for (Vertex v:cand)
            if(v.isY0()) {
                v.getLabel().normalize_assign();
                Y0.put(v, v.getLabel().copy());
            } else labelInit.accept(v.getLabel(),k);
        if(labelInit==LabelInference.randomLabelInit) {
            final Map<Vertex,Matrix> best=new HashMap<>();
            for(Vertex v:cand)best.put(v, v.getLabel());
            double minObj=LabelInference.objective(cand, candS, Y0, B, k,beta);
            for(int i=0;i<100;i++) {
                for (Vertex v:cand)if(!v.isY0())labelInit.accept(v.getLabel(),k);
                double obj=LabelInference.objective(cand, candS, Y0, B, k,beta);
                if(obj<minObj) {
                    for(Vertex v:cand)best.put(v, v.getLabel());
                    minObj=obj;
                }
            }
            for(Vertex v:cand)v.setLabel(best.get(v));
        }
        return Y0;
    }

    /**
     * 
     * @param cand: implement by cand
     * @param candS: implement by candS 
     * update procedure of B
     * This abstract method will be implemented in the three algorithms respectively
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */	
    abstract protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS) throws DimensionNotAgreeException ;

    /**
     * 
     * @param cand: implement by cand
     * @param candS: implement by candS 
     * @param Y0: the initialized label
     * update procedure of Y
     * 
     * This abstract method will be implemented in the three algorithms respectively
     * @return 
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */	
    abstract protected Map<Vertex, Matrix> updateY(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex, Matrix> Y0) throws DimensionNotAgreeException ;
}
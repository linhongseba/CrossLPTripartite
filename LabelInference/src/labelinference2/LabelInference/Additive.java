package labelinference2.LabelInference;
import java.util.Collection;
import labelinference.Matrix.MatrixFactory;
import labelinference.Matrix.Matrix;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 * @author Hermes777, SailWhite
 *
 * @since 1.8
 *
 * TODO To implement the update of Y and B matrix in AR
 */
public class Additive extends AbstractLabelInference implements LabelInference {

	/** alpha:the step size of learning*/
    protected double alpha;
    protected double L;

    /**
     * @param _g:initial graph g with _g
	 */
    public Additive(Graph _g) {
        super(_g);
        alpha=1;
    }

    /**
     * @param _g:initial graph g with _g
	 * @param _labelInit: initial labeled vertices
	 */
    public Additive(Graph _g, BiConsumer<Matrix,Integer> _labelInit) {
        super(_g,_labelInit);
        alpha=1;
    }

    @Override
    /**
     * @param cand:the candidate graph
	 * @param candS: the candidate graph with its adjacent nodes
	 */
    protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS) throws DimensionNotAgreeException {
        MatrixFactory mf=MatrixFactory.getInstance();
        //A_t=\Sigma{B_{tt(u)}*Y(u)*Y(u)^T*B_{tt(u)}^T} (t(u)\neq{t} )
        Map<Vertex.Type,Map<Vertex.Type,Matrix>> dBleft=new HashMap<>();
        Map<Vertex.Type,Matrix> dBright=new HashMap<>();
        for(Vertex.Type t0:Vertex.types) {
            dBleft.put(t0, new HashMap<>());
            for(Vertex.Type t1:Vertex.types) {
                if(t0!=t1)
                    dBleft.get(t0).put(t1,mf.creatMatrix(k,k));
            }
            dBright.put(t0,mf.creatMatrix(k,k));
        }
        //dBleft_{tt'}=\Sigma{(Y(u)^T*Y(v)*G(u,v))} (u \in t, v \in t')
        for(Vertex u:cand)
            for(Vertex v:u.getNeighbors()) {
            Matrix temp=v.getLabel().transpose();
            Matrix temp2=u.getLabel().times(temp);
            dBleft.get(u.getType()).get(v.getType()).add_assign(
                temp2.times_assign(u.getEdge(v)));
        }
        //dBright_{t}=\sum_{u\in V_t}Y(u)*Y(u)^T
        //int count=0;
        for(Vertex u:cand){
            dBright.get(u.getType()).add_assign(  
                u.getLabel().times(u.getLabel().transpose()));
            //count++;
            //if(count%1000==0){
                //System.out.println("dBright is:"+dBright.get(u.getType()));
            //}
        }
        //L_{tt'}=\Sigma{Y(u)*Y(u)^T*Y(v)*Y(v)^T*G(u,v)}(u\in t,v\in t')
        for(Vertex.Type t0:Vertex.types){
            //System.out.println("DBright matrix is "+dBright.get(t0));
            for(Vertex.Type t1:Vertex.types)
                if(t0!=t1) {
             Matrix temp=dBright.get(t0).times(dBright.get(t1));
             //System.out.println("DBright matrix is "+dBright.get(t1).toString());
             double Lb=temp.norm(Matrix.FROBENIUS_NORM);
             if(Lb<1e-11){
                 Lb=1e-11;
             }
             //System.out.println("Lb for B is "+Lb);
             //System.out.println("Lb for B is "+(1.0/Lb));
//             if(Lb<1){
//                 System.out.println("Lb for B is"+ Lb);
//                 for(Vertex.Type t2:Vertex.types)
//                     System.out.println(dBright.get(t2));
//             }
            //double etab;
            //if(temp>1e-16)
            //    etab=(alphaNext+alpha-1)/alphaNext/temp;
            //else
            //    etab=(alphaNext+alpha-1)/alphaNext;
             
             temp=dBright.get(t0).times(B.get(t0).get(t1));
             temp.times_assign(dBright.get(t1));
             //System.out.println("left part is "+dBleft.get(t0).get(t1));
             //System.out.println("right part is "+temp.toString());
             Matrix temp2=dBleft.get(t0).get(t1).subtract(temp);
             temp2.times_assign(1.0/Lb);
             //System.out.println("incremental part is "+temp2);
             //System.out.println("old B value is "+Btemp.get(t0).get(t1).toString());
             B.get(t0).get(t1).add_assign(temp2).projectpositive_assign();
             //System.out.println(B.get(t0).get(t1));
            //B_{tt'}=B_{tt'}+\eta_b(dBleft_{tt'}-dBright_{tt'})/maxE
            //System.out.println(L.get(t0).get(t1).norm(Matrix.FROBENIUS_NORM));
             //System.out.println("Btemp matrix is "+Btemp.get(t0).get(t1));
             //B.get(t0).get(t1).projectpositive();
            }
        }
//        try {
//            final double ZERO=1e-9;
//            for(Vertex.Type t0:Vertex.types)for(Vertex.Type t1:Vertex.types) {
//                double maxItem=0;
//                double minItem=0;
//                Matrix curB=B.get(t0).get(t1);
//                for(int row=0;row<k;row++)
//                    for(int col=0;col<k;col++) {
//                        double curItem=curB.get(row, col);
//                        if(curItem>maxItem)maxItem=curItem;
//                        if(curItem<minItem)minItem=curItem;
//                    }
//                if(minItem<ZERO) {
//                    if(minItem>0)minItem=-ZERO;
//                    maxItem-=2*minItem;
//                    for(int row=0;row<k;row++)
//                        for(int col=0;col<k;col++)
//                            curB.set(row, col, (curB.get(row, col)-2*minItem)/maxItem);
//                }
//                else {
//                    for(int row=0;row<k;row++)
//                        for(int col=0;col<k;col++)
//                            curB.set(row, col, curB.get(row, col)/maxItem);
//                }
//            }
//        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
    }

    @Override
    /**
     * @param cand:the candidate graph
	 * @param candS:the next state of candidate graph
	 * @param Y0: the initialized label
	 */
    protected Map<Vertex, Matrix> updateY(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0) throws DimensionNotAgreeException {
        MatrixFactory mf=MatrixFactory.getInstance();
        //check whether getTempLabel is initilized;
        Map<Vertex.Type,Matrix> A;
        A=new HashMap<>();
        for(Vertex.Type type:Vertex.types){
             //System.out.println("candS size"+candS.size());
            //Start updating At
            A.put(type, mf.creatMatrix(k, k));
            for(Vertex u:cand){
                if(u.getType()!=type){
                    //System.out.println("B vale is "+B.get(type).get(u.getType()));
                    //System.out.println("Yu value is "+Y.get(u));
                    Matrix temp= B.get(type).get(u.getType()).times(Y.get(u));
                    //System.out.println("temp matrix value is "+temp);
                    temp.times_assign(Y.get(u).transpose());
                    //System.out.println("temp matrix value 1 is "+temp);
                    temp.times_assign(B.get(type).get(u.getType()).transpose());
                    //System.out.println("temp matrix value  2 is "+temp);
                    A.get(type).add_assign(temp);
                }
            }
            //System.out.println("A matrix is "+A.get(type));
            //finish updating Atype
            L=A.get(type).norm(Matrix.FROBENIUS_NORM);
            //Update vertex type by type
            for(Vertex u:cand){
                if(u.getType()==type){
                    Matrix label_temp= mf.creatMatrix(k, 1);
                    for(Vertex v:u.getNeighbors()){
                        Matrix temp=B.get(u.getType()).get(v.getType()).times(Y.get(v));
                        temp.times_assign(u.getEdge(v));
                        label_temp.add_assign(temp);
                    } 
                    Matrix t=A.get(u.getType()).times(u.getTempLabel());
                    label_temp.subtract_assign(t);
                    if(u.isY0())
                        label_temp.add_assign(Y0.get(u).times(5)).subtract_assign(u.getTempLabel().times(5));
                    //Y(u)=\|Y(u)+2\eta*(\Sigma{B_{t(u)t(v)}*Y(v)*G(u,v)}-\frac{2*A_{t(u)}*Y(u)*\|\Sigma{B_{t(u)t(v)}*Y(v)*G(u,v)}\|}{\|2*A_{t(u)}*Y(u)\|*maxE}+1_{YL}*(Y0(u)-Y(u)))\|
                    label_temp.times_assign(1.0/L);
                    label_temp.add_assign(u.getTempLabel());
                    u.setTempLabel(label_temp);
                    //System.out.println(u.getTempLabel());
                }
            }
            for(Vertex u:cand){
                if(u.getType()==type){
                    Y.put(u, u.getTempLabel().projectpositive());
                    u.setTempLabel(Y.get(u));
                }
            }  
        }
        //    for(Vertex u:g.getVertices()) {
        //        delta+=Y.get(u).subtract(Yold.get(u)).norm(Matrix.FIRST_NORM);
        //    }
        //System.out.print(String.format("ADDITIVE@_@Delta = %.6f\n",(alpha-1)/alphaNext));;
        return Y;
    }

    @Override
    /**
     * initialize alpha and alphaNext in every increment procedure
     */
    public void increase(Collection<Vertex> deltaGraph, int maxIter, double nuance, double a, int disp) throws DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {
        alpha=1;
        super.increase(deltaGraph, maxIter, nuance, a, disp);
    }
}

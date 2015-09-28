package labelinference.LabelInference;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.Matrix.Matrix;
import labelinference.Matrix.MatrixFactory;
import labelinference.exceptions.DimensionNotAgreeException;

/**
*
* @author sailw
* 
* @since 1.8
* 
* TODO To implement the update of Y and B matrix in MR
* 
**/
public class Multiplicative extends AbstractLabelInference implements LabelInference {
	/**
     * @param _g:initial graph g with _g
	 */	
    public Multiplicative(Graph _g) {	
        super(_g);
    }
    
    /**
    * @param _g:initial graph g with _g
    * @param _labelInit: initial labeled vertices
    */	    
    public Multiplicative(Graph _g, BiConsumer<Matrix,Integer> _labelInit) {
        super(_g,_labelInit);
    }

    /**
    * @param cand:the candidate graph
    * @param candS:the next state of candidate graph
    * @throws labelinference.exceptions.DimensionNotAgreeException
    */
    @Override
    protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS) throws DimensionNotAgreeException {
        MatrixFactory mf=MatrixFactory.getInstance();
        Map<Vertex.Type,Map<Vertex.Type,Matrix>> dBup=new HashMap<>();
        Map<Vertex.Type,Matrix> dBdown=new HashMap<>();
        for(Vertex.Type t0:Vertex.types) {
            dBup.put(t0, new HashMap<>());
            for(Vertex.Type t1:Vertex.types) {
                dBup.get(t0).put(t1,mf.creatMatrix(k,k));
            }
            dBdown.put(t0,mf.creatMatrix(k,k));
        }
        for(Vertex u:cand)for(Vertex v:u.getNeighbors()) {
            dBup.get(u.getType()).get(v.getType()).add_assign(
                u.getLabel()
                .times(v.getLabel().transpose())
                .times_assign(u.getEdge(v)));
        }
        for(Vertex u:cand){
            dBdown.get(u.getType()).add_assign(
                u.getLabel().times(u.getLabel().transpose()));
        }
        //dBup_{tt'}=\Sigma{(Y(u)^T*Y(v)*G(u,v))} (u \in t, v \in t')
        //dBdown_{tt'}=\Sigma{(Y(u)*Y(u)^T*B(u,v)*Y(v)*Y(v)^T)} (u \in t, v \in t')

        for(Vertex.Type t0:Vertex.types)for(Vertex.Type t1:Vertex.types)if(t0!=t1)
            B.get(t0).get(t1)
                .cron_assign(dBup.get(t0).get(t1)
                .divide_assign(dBdown.get(t0).times(B.get(t0).get(t1)).times_assign(dBdown.get(t1))).sqrt_assign());
        //B_{tt'}=B_{tt'}\circ\sqrt{\frac{dBup_{tt'}}{dBdown_{tt'}}}
    }
    
    /**
    * @param cand:the candidate graph
    * @param candS:the next state of candidate graph
    * @param Y0: the initialized label
    * @return 
    * @throws labelinference.exceptions.DimensionNotAgreeException
    */
     @Override
    protected Map<Vertex, Matrix> updateY(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex, Matrix> Y0) throws DimensionNotAgreeException {
        MatrixFactory mf=MatrixFactory.getInstance();
        Map<Vertex.Type,Matrix> A=new HashMap<>();
        for(Vertex.Type type:Vertex.types) {
            A.put(type, mf.creatMatrix(k, k));
            for(Vertex u:candS)
                if(u.getType()!=type)
                    A.get(type).add_assign(
                        B.get(type).get(u.getType())
                        .times(u.getLabel())
                        .times_assign(u.getLabel().transpose())
                        .times_assign(B.get(type).get(u.getType()).transpose()));
        }
        //A_t=\Sigma{B_{tt(u)}*Y(u)*Y(u)^T*B_{tt(u)}^T} (t(u)\neq{t} )
        
        Map<Vertex, Matrix> Y=new HashMap<>();
        for(Vertex u:cand) {
            Matrix label= mf.creatMatrix(k, 1);
            for(Vertex v:u.getNeighbors())
                label.add_assign(
                    B.get(u.getType()).get(v.getType())
                    .times(v.getLabel())
                    .times_assign(u.getEdge(v)));
            if(u.isY0())label.add_assign(
                Y0.get(u))
                .divide_assign(A.get(u.getType())
                    .times(u.getLabel())
                    .add_assign(u.getLabel()));
            else label.divide_assign(A.get(u.getType()).times(u.getLabel()));
            label.sqrt_assign().cron_assign(u.getLabel()).normalize_assign();
            Y.put(u, label);
            //Y(u)=Y(u)\circ\sqrt{\frac{\Sigma{B_{t(u)t(v)}*Y(v)*G(u,v)}/maxE+1_{YL}*Y0(u)}{A_{t(u)}+1_{YL}*Y(u)}}
        }
        return Y;
    }
}
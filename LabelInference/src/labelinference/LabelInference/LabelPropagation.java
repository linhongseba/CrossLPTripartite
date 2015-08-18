package labelinference.LabelInference;

import java.util.Collection;
import labelinference.Matrix.MatrixFactory;
import labelinference.Matrix.Matrix;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.exceptions.DimensionNotAgreeException;

/**
*
* @author sailw
* 
* @since 1.8

* initializations with label propagation algorithms

* @see labelinference.AbstractLabelInference.AbstractLabelInference(Graph _g)
*/
public class LabelPropagation extends AbstractLabelInference implements LabelInference {
    double alpha;
    /**
     * 
     * @param _g
     * @param _alpha
     */
    public LabelPropagation(Graph _g, double _alpha) {
        super(_g);
    }

    /**
     * 
     * @param _g
     * @param _alpha
     * @param _labelInit
     */
    public LabelPropagation(Graph _g, double _alpha, BiConsumer<Matrix,Integer> _labelInit) {
        super(_g,_labelInit);
        alpha=_alpha;
    }

    /**
     * @param cand: the candidate graph
     * @param candS: the candidate graph with its adjacent nodes
     */
    protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS) throws DimensionNotAgreeException {}
    
    @Override
    /**
     * @param cand: the candidate graph
     * @param candS: the candidate graph with its adjacent nodes
     * @param Y0: the initial state of Y matrix
     * TODO: TO update Y using label propagation algorithm
     */
    protected Map<Vertex, Matrix> updateY(Collection<Vertex> cand, Collection<Vertex> candS, Map<Vertex,Matrix> Y0) throws DimensionNotAgreeException {
        final MatrixFactory mf=MatrixFactory.getInstance();
        final Map<Vertex,Map<Vertex.Type,Matrix>> cache=new HashMap<>();
        Matrix emptyMat=mf.creatMatrix(k, 1);
        for(Vertex u:candS) {
            Map<Vertex.Type,Matrix> value=new HashMap<>();
            for(Vertex v:u.getNeighbors())
                value.put(v.getType(), value.getOrDefault(v.getType(), emptyMat).add(v.getLabel().times(v.getEdge(u)/v.sumE())));
            cache.put(u, value);
        }

        Map<Vertex, Matrix> Y=new HashMap<>();
        for(final Vertex u:cand) {
            if(u.isY0()) {
                Y.put(u, u.getLabel());
                continue;
            }
            Matrix label;
            Matrix a=mf.creatMatrix(k, 1);
            Matrix b=mf.creatMatrix(k, 1);
            for(Vertex v:u.getNeighbors()) {
                a=a.add(v.getLabel().times(u.getEdge(v)));
                b=b.add(cache.get(v).get(u.getType()).subtract(u.getLabel().times(u.getEdge(v)/u.sumE())).times(u.getEdge(v)/v.sumE()));
            }
            label=a.normalize().times(1-alpha).add(b.normalize().times(alpha));
            Y.put(u, label);
        }
        return Y;
    }
}
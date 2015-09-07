package labelinference.Selector;

import labelinference.Graph.Vertex;
import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

/**
*
* @author sailw

* @since 1.8

*/
public class SimpleHeuristicSelector extends HashSet<Vertex> implements Selector {
    public SimpleHeuristicSelector(Collection<Vertex> vertices, int threshold) {
        TreeSet<Vertex> H=new TreeSet<>((Vertex x,Vertex y)->{
            if(y.degree()!=x.degree())return y.degree()-x.degree();
            return y.hashCode()-x.hashCode();
        });
        H.addAll(vertices);
        while(!H.isEmpty() && size()<threshold) {
            Vertex u=H.pollFirst();
            Collection<Vertex> neighbors=new HashSet<>((Collection)u.getNeighbors());
            neighbors.removeIf(x->!vertices.contains(x)||contains(x));
            
            if(!neighbors.isEmpty()) {
                Vertex w=(Vertex)(new ArrayList(neighbors)).get((int)(random()*(neighbors.size())));
                add(w);
                for(Vertex v:w.getNeighbors())H.remove(v);
                for(Vertex v:u.getNeighbors())H.remove(v);
            }
        }
    }
}

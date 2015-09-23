package labelinference.Selector;

import java.util.Collection;
import labelinference.Graph.Vertex;
import java.util.HashSet;
import java.util.TreeSet;

/**
 *
 * @author sailw
 
 * @since 1.8
 */
public class DegreeSelector extends HashSet<Vertex> implements Selector {
    public DegreeSelector(Collection<Vertex> vertices, int threshold) {
        TreeSet<Vertex> H=new TreeSet<>((Vertex x,Vertex y)->{
            if(y.degree()!=x.degree())return y.degree()-x.degree();
            return y.getId().compareTo(x.getId());
        });
        H.addAll(vertices);
        for(int i=0;i<threshold && !H.isEmpty();i++)add(H.pollFirst());
    }
}

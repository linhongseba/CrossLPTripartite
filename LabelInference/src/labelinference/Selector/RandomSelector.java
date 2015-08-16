package labelinference.Selector;

import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author sailw
 
 * @since 1.8
 */
public class RandomSelector extends ArrayList<Vertex> implements Selector {
    public RandomSelector(Collection<Vertex> vertices, int threshold) {
        ArrayList<Vertex> list=new ArrayList<>(vertices);
        Collections.shuffle(list);
        addAll(list.subList(0, threshold));
    }
}

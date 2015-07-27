/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.Selector;

import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

/**
 *
 * @author sailw
 */
public class SimpleHeuristicSelector extends HashSet implements Selector {
    public SimpleHeuristicSelector(Collection<Vertex> vertices, int threshold) {
        TreeSet<Vertex> H=new TreeSet<>((Vertex x,Vertex y)->{
            if(y.degree()!=x.degree())return y.degree()-x.degree();
            return y.hashCode()-x.hashCode();
        });
        H.addAll(vertices);
        while(!H.isEmpty() && size()<threshold) {
            Vertex u=H.pollFirst();
            Collection<Vertex> neighbors=new HashSet<>((Collection)u.getNeighbors());
            neighbors.removeIf(x->!vertices.contains(x));
            
            if(!neighbors.isEmpty()) {
                Vertex w=(Vertex)(new ArrayList(neighbors)).get((int)(random()*(neighbors.size())));
                add(w);
                for(Vertex v:w.getNeighbors())H.remove(v);
                for(Vertex v:u.getNeighbors())H.remove(v);
            }
        }
    }
}

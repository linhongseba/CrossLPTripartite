/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

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
    SimpleHeuristicSelector(Graph g, int threshold) {
        TreeSet<Vertex> H=new TreeSet<>((Vertex x,Vertex y)->{
            if(y.degree()!=x.degree())return y.degree()-x.degree();
            return y.hashCode()-x.hashCode();
        });
        H.addAll(g.getVertices());
        while(!H.isEmpty() && size()<threshold) {
            Vertex u=H.pollFirst();
            Vertex w=(Vertex)(new ArrayList((Collection) u.getNeighbors())).get((int)(random()*(u.degree())));
            add(w);
            for(Vertex v:w.getNeighbors())H.remove(v);
            for(Vertex v:u.getNeighbors())H.remove(v);
        }
    }
}

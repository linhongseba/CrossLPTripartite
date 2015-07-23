/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import java.util.HashSet;
import java.util.TreeSet;

/**
 *
 * @author sailw
 */
public class DegreeSelector extends HashSet implements Selector {
    DegreeSelector(Graph g, int threshold) {
        TreeSet<Vertex> H=new TreeSet<>((Vertex x,Vertex y)->{
            if(y.degree()!=x.degree())return y.degree()-x.degree();
            return y.hashCode()-x.hashCode();
        });
        H.addAll(g.getVertices());
        for(int i=0;i<threshold && !H.isEmpty();i++)add(H.pollFirst());
    }
}

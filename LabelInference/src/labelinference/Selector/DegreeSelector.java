/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.Selector;

import java.util.Collection;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import java.util.HashSet;
import java.util.TreeSet;

/**
 *
 * @author sailw
 */
public class DegreeSelector extends HashSet<Vertex> implements Selector {
    public DegreeSelector(Collection<Vertex> vertices, int threshold) {
        TreeSet<Vertex> H=new TreeSet<>((Vertex x,Vertex y)->{
            if(y.degree()!=x.degree())return y.degree()-x.degree();
            return y.hashCode()-x.hashCode();
        });
        H.addAll(vertices);
        for(int i=0;i<threshold && !H.isEmpty();i++)add(H.pollFirst());
    }
}

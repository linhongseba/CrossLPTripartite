/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.Selector;

import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author sailw
 */
public class RandomSelector extends ArrayList implements Selector {
    public RandomSelector(Graph g, int threshold) {
        ArrayList<Vertex> list=new ArrayList<>(g.getVertices());
        Collections.shuffle(list);
        addAll(list.subList(0, threshold));
    }
}

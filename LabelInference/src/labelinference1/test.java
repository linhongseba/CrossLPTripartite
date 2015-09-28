/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.LabelInference.Additive;
import labelinference.LabelInference.LabelInference;
import static labelinference.LabelInference.LabelInference.*;
import labelinference.LabelInference.LabelPropagation;
import labelinference.LabelInference.Multiplicative;
import labelinference.Selector.DegreeSelector;
import labelinference.Selector.RandomSelector;
import labelinference.Selector.Selector;
import labelinference.Selector.SimpleHeuristicSelector;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw
 */
public class test {
    public static void main(String args[]) throws FileNotFoundException, ColumnOutOfRangeException, RowOutOfRangeException {
        double ratios[]={0.05,0.1};
        String graphs[]={"data/graph-1.txt","data/graph-30.txt","data/graph-37.txt"};
        
        for(double ratio:ratios) {
            Map<String,Function<Collection<Vertex>,Selector>> selectors=new HashMap<>();
            selectors.put("RND", g->new RandomSelector(g, (int)(g.size()*ratio)));
            selectors.put("DEG", g->new DegreeSelector(g, (int)(g.size()*ratio)));
            selectors.put("SHR", g->new SimpleHeuristicSelector(g, (int)(g.size()*ratio)));
            for(String selector:selectors.keySet()) {
                for(String path:graphs) {
                    Graph graph=new Graph(path);
                    Selector selected=selectors.get(selector).apply(graph.getVertices(v->v.isY0()));
                    int nei=0;
                    for(Vertex v:selected)if(v.getLabel().get(0,0)<v.getLabel().get(1,0))nei++;
                    System.out.println(String.format("%s %s %f %d:%d",path,selector,ratio,selected.size()-nei,nei));
                }
            }
        }
    }
}

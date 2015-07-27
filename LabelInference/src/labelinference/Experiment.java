/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.LabelInference.BlockCoordinateDescent;
import labelinference.LabelInference.LabelInference;
import labelinference.LabelInference.LabelPropagation;
import labelinference.LabelInference.Multiplicative;
import labelinference.LabelInference.NewMultiplicative;

/**
 *
 * @author sailw
 */
public class Experiment {
    public static void main(String args[]) throws FileNotFoundException {
        Map<String,Function<Graph,LabelInference>> inferencers=new HashMap<>();
        inferencers.put("MA", g->new Multiplicative(g,0,15));
        inferencers.put("BCD", g->new BlockCoordinateDescent(g,0,15));
        inferencers.put("LP", g->new LabelPropagation(g, 0.2,0,15));
        inferencers.put("NMA", g->new NewMultiplicative(g,0,15));
        
        for(String inferencer:inferencers.keySet()) {
            System.out.println(inferencer);
            Graph graph=new Graph("data/graph-0.txt");
            for(Vertex v:graph.getVertices())
                if(!v.getId().equals("B0") && !v.getId().equals("C1"))v.init(v.getType(), null, false);
            LabelInference li=inferencers.get(inferencer).apply(graph);
            System.out.println(String.format("Processed in %.3f ms\n",li.getResult()));
        }
    }
}

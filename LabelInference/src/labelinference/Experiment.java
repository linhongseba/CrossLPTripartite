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
import static labelinference.LabelInference.LabelInference.DISP_ALL;
import labelinference.LabelInference.LabelPropagation;
import labelinference.LabelInference.Multiplicative;

/**
 *
 * @author sailw
 */
public class Experiment {
    public static void main(String args[]) throws FileNotFoundException {
        Map<String,Function<Graph,LabelInference>> inferencers=new HashMap<>();
        //inferencers.put("MA", g->new Multiplicative(g));
        inferencers.put("BCD", g->new Additive(g,1e-5,1e-3));
        inferencers.put("LP", g->new LabelPropagation(g,0));
        inferencers.put("MA", g->new Multiplicative(g));
        
        for(String inferencer:inferencers.keySet()) {
            System.out.println(inferencer);
            Graph graph=new Graph("data/graph-0.txt");
            for(Vertex v:graph.getVertices())
                if(!v.getId().equals("B0") && !v.getId().equals("C1"))v.init(v.getType(), null, false);
            inferencers.get(inferencer).apply(graph).getResult(15,0,DISP_ALL);
            
            graph=new Graph("data/graph-0.txt");
            for(Vertex v:graph.getVertices())
                if(!v.getId().equals("B0") && !v.getId().equals("C1"))v.init(v.getType(), null, false);
            Collection<Vertex> deltaGraph=new HashSet<>();
            deltaGraph.add(graph.findVertexByID("A2"));
            deltaGraph.add(graph.findVertexByID("B4"));
            for(Vertex u:deltaGraph) {
                for(Vertex v:u.getNeighbors())
                    v.removeEdge(u);
                graph.removeVertex(u);
            }
            LabelInference li=inferencers.get(inferencer).apply(graph);
            System.out.println("\nSubgraph");
            li.getResult(15,0,DISP_ALL);
            System.out.println("\nIncrement");
            li.increase(deltaGraph, 15, 0, 0.9, DISP_ALL);
        }
    }
}

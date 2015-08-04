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
import labelinference.LabelInference.BlockCoordinateDescent;
import labelinference.LabelInference.LabelInference;
import static labelinference.LabelInference.LabelInference.*;
import labelinference.LabelInference.LabelPropagation;
import labelinference.LabelInference.Multiplicative;
import labelinference.LabelInference.NewMultiplicative;

/**
 *
 * @author sailw

*  TODO This class used for testing toygraph
*/
public class Experiment {
    /**the main method*/
	public static void main(String args[]) throws FileNotFoundException {
        Map<String,Function<Graph,LabelInference>> inferencers=new HashMap<>();
        
        inferencers.put("BCD", g->new BlockCoordinateDescent(g));
        inferencers.put("LP", g->new LabelPropagation(g,0));
        inferencers.put("MA", g->new NewMultiplicative(g));
        //distribute names to their function respectively
        
        for(String inferencer:inferencers.keySet()) {
        	
        	/**Test basic algorithms*/
            System.out.println(inferencer);
            Graph graph=new Graph("data/graph-0.txt");
            
            for(Vertex v:graph.getVertices())
                if(!v.getId().equals("B0") && !v.getId().equals("C1"))v.init(v.getType(), null, false);
            
            inferencers.get(inferencer).apply(graph).getResult(15,0,DISP_ALL);
            
        	/**Test incremental algorithm with different basic algorithms*/
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

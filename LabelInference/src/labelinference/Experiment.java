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
import static labelinference.LabelInference.LabelInference.DISP_NONE;
import labelinference.LabelInference.LabelPropagation;
import labelinference.LabelInference.Multiplicative;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw
 */
public class Experiment {
    public static void main(String args[]) throws FileNotFoundException, DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {
        Map<String,Function<Graph,LabelInference>> inferences=new HashMap<>();
        inferences.put("MAR", g->new Multiplicative(g,LabelInference::defaultLabelInit));
        inferences.put("GDR", g->new Additive(g,LabelInference::defaultLabelInit));
        inferences.put("MAG", g->new Multiplicative(g,LabelInference::NoneInit));
        inferences.put("GDG", g->new Additive(g,LabelInference::NoneInit));
        inferences.put("LP", g->new LabelPropagation(g, 0));
        
        for(String inference:inferences.keySet()) {
            System.out.println(inference);
            Graph graph=new Graph("data/graph-0.txt");
            for(Vertex v:graph.getVertices())
                if(!v.getId().equals("B0") && !v.getId().equals("C1"))v.init(v.getType(), v.getLabel(), false);
            if(inference.charAt(inference.length()-1)=='G')new LabelPropagation(graph,1).getResult(10, 0, DISP_NONE);
            inferences.get(inference).apply(graph).getResult(15,0,DISP_ALL);
            
            graph=new Graph("data/graph-0.txt");
            for(Vertex v:graph.getVertices())
                if(!v.getId().equals("B0") && !v.getId().equals("C1"))v.init(v.getType(), v.getLabel(), false);
            Collection<Vertex> deltaGraph=new HashSet<>();
            deltaGraph.add(graph.findVertexByID("A2"));
            deltaGraph.add(graph.findVertexByID("B4"));
            for(Vertex u:deltaGraph) {
                for(Vertex v:u.getNeighbors())
                    v.removeEdge(u);
                graph.removeVertex(u);
            }
            LabelInference li=inferences.get(inference).apply(graph);
            System.out.println("\nSubgraph");
            LabelInference lp=new LabelPropagation(graph,1);
            if(inference.charAt(inference.length()-1)=='G')lp.getResult(10, 0, DISP_NONE);
            li.getResult(15,0,DISP_ALL);
            System.out.println("\nIncrement");
            if(inference.charAt(inference.length()-1)=='G')lp.increase(deltaGraph,10, 0,0.9, DISP_NONE);
            li.increase(deltaGraph, 15, 0, 0.9, DISP_ALL);
        }
    }
}

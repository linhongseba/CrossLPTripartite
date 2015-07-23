/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.Selector;

import labelinference.Selector.SimpleHeuristicSelector;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;
import labelinference.Labor;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.RowOutOfRangeException;
import org.junit.Test;

/**
 *
 * @author sailw
 */
public class SimpleHeuristicSelectorTest {
    
    public SimpleHeuristicSelectorTest() {
    }
    
    @Test
    public void testSimpleHeuristicSelector() throws ColumnOutOfRangeException, RowOutOfRangeException, FileNotFoundException {
        System.out.println("\nSimpleHeuristicSelector");
        Labor labor=Labor.getInstance();
        Map<Integer,Vertex> expResult;
        Map<Integer,Vertex> result;
        
        System.out.println("graph 0:");
        test("data/0/testGraph.g",3);
        
        System.out.println("graph 1:");
        test("data/1/testGraph.g",0);
        
        System.out.println("graph 30:");
        test("data/30/testGraph.g",0);
    }
    
    private void test(String path,int threshold) throws ColumnOutOfRangeException, RowOutOfRangeException, FileNotFoundException {
        Labor labor=Labor.getInstance();
        Map<Integer,Vertex> graph=labor.readGraph(path);
        Graph g=new Graph(graph.values());
        Collection<Vertex> result=new SimpleHeuristicSelector(g,threshold>0?threshold:g.getVertices().size()/10);
        System.out.println(result.size());
        for(Integer vid:graph.keySet()) {
            if(result.contains(graph.get(vid)))
                System.out.println(vid);
        }
    }    
}
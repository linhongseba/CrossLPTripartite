/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.RowOutOfRangeException;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sailw
 */
public class SimpleHeuristicSelectorTest {
    
    public SimpleHeuristicSelectorTest() {
    }
    
    Map<Integer,Vertex> readGraph(String path) throws ColumnOutOfRangeException, RowOutOfRangeException, FileNotFoundException {
        MatrixFactory matrixFactory=MatrixFactory.getInstance();
        Scanner in=new Scanner(new FileReader(new File(path)));
        int nVertex=in.nextInt();
        Map<Integer,Vertex> graph=new HashMap<>();
        
        for(int i=0;i<nVertex;i++) {
            int vid=in.nextInt();
            int nNei=in.nextInt();
            int lNum=in.nextInt();
            Matrix label=matrixFactory.creatMatrix(2, 1);
            String typeS=in.next();
            Vertex.Type type;
            boolean isY0;
            if(!graph.containsKey(vid))graph.put(vid, new Vertex());
            Vertex vertex=graph.get(vid);
            
            if(lNum==0) {
                isY0=false;
            } else if(lNum==1) {
                isY0=true;
                label.set(0, 0, 1);
                label.set(1, 0, 0);
            } else {
                isY0=true;
                label.set(0, 0, 0);
                label.set(1, 0, 1);
            }
            
            if(typeS.equals("A"))type=Vertex.typeA;
            else if(typeS.equals("B"))type=Vertex.typeB;
            else type=Vertex.typeC;
            
            vertex.init(type, label, isY0);
            
            for(int j=0;j<nNei;j++) {
                int nid=in.nextInt();
                double weight=in.nextDouble();
                if(!graph.containsKey(nid))graph.put(nid, new Vertex());
                vertex.addEdge(graph.get(nid), weight);
            }
        }
        return graph;
    }

    @Test
    public void testSimpleHeuristicSelector() throws ColumnOutOfRangeException, RowOutOfRangeException, FileNotFoundException {
        System.out.println("select");
        
        System.out.println("graph 0:");
        test("data/0/testGraph.g",3);
        
        System.out.println("graph 1:");
        test("data/1/testGraph.g",0);
        
        System.out.println("graph 30:");
        test("data/30/testGraph.g",0);
    }
    
    private void test(String path,int threshold) throws ColumnOutOfRangeException, RowOutOfRangeException, FileNotFoundException {
        Map<Integer,Vertex> graph=readGraph(path);
        Graph g=new Graph(graph.values());
        Collection<Vertex> result=new SimpleHeuristicSelector(g,threshold>0?threshold:g.getVertices().size()/10);
        System.out.println(result.size());
        for(Integer vid:graph.keySet()) {
            if(result.contains(graph.get(vid)))
                System.out.println(vid);
        }
    }
    
}

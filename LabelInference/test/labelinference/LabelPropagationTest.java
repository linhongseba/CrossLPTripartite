/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
public class LabelPropagationTest {
    
    public LabelPropagationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
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
    
    Map<Integer,Vertex> test(String path) throws ColumnOutOfRangeException, RowOutOfRangeException, FileNotFoundException {
        Map<Integer,Vertex> graph=readGraph(path);
        LabelPropagation labelPropagation=new LabelPropagation(new Graph(graph.values()));
        labelPropagation.getResult();

        return graph;
    }
    
    /**
     * Test of getResult method, of class LabelPropagation.
     */
    @Test
    public void testGetResult() throws FileNotFoundException, ColumnOutOfRangeException, RowOutOfRangeException {
        System.out.println("\nLabelPropagation");
        
        System.out.println("graph 1,top 5%:");
        Map<Integer,Vertex> expResult=readGraph("data/1/testGraph.g");
        Map<Integer,Vertex> result=test("data/1/trainGraph5.g");
        check(expResult,result);
        
        System.out.println("graph 1,top 10%:");
        result=test("data/1/trainGraph10.g");
        check(expResult,result);
        
        System.out.println("graph 30,top 5%:");
        expResult=readGraph("data/30/testGraph.g");
        result=test("data/30/trainGraph5.g");
        check(expResult,result);
        
        System.out.println("graph 30,top 10%:");
        result=test("data/30/trainGraph10.g");
        check(expResult,result);
    }

    private void check(Map<Integer, Vertex> expResult, Map<Integer, Vertex> result) throws ColumnOutOfRangeException, RowOutOfRangeException {
        double correct=0;
        double totle=0;
        for(Map.Entry<Integer, Vertex> v:expResult.entrySet()) {
            Vertex expV=v.getValue();
            Vertex resV=result.get(v.getKey());
            
            if(resV==null)System.out.println("null "+v.getKey());
            
            if(resV.isY0() && !resV.getLabel().equals(expV.getLabel())) {
                System.out.println("Y0 cahnged in vertex "+v.getKey());
                fail("Y0 changed in vertex "+v.getKey());
            }
            
            if(expV.isY0()) {
                totle+=1;
                if(expV.getLabel().get(0, 0)==1) {
                    if(resV.getLabel().get(0, 0)>resV.getLabel().get(1, 0))correct++;
                } else {
                    if(resV.getLabel().get(0, 0)<resV.getLabel().get(1, 0))correct++;
                }
            }
        }
        
        Double acc=correct/totle;
        System.out.println("Accuracy="+acc);
        //if(acc<0.5)fail("Accuracy is too low");
    }
}

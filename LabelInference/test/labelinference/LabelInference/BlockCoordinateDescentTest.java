/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.LabelInference.BlockCoordinateDescent;
import java.io.FileNotFoundException;
import java.util.Map;
import labelinference.Labor;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;
import org.junit.Test;

/**
 *
 * @author sailw
 */
public class BlockCoordinateDescentTest {
    public BlockCoordinateDescentTest() {
    }
    
    public Map<Integer,Vertex> test(String path) throws ColumnOutOfRangeException, RowOutOfRangeException, FileNotFoundException, DimensionNotAgreeException {
        Labor labor=Labor.getInstance();
        Map<Integer,Vertex> graph=labor.readGraph(path);
        BlockCoordinateDescent blockCoordinateDescent=new BlockCoordinateDescent(new Graph(graph.values()),2);
        blockCoordinateDescent.getResult();
        return graph;
    }
    
    /**
     * Test of getResult method, of class BlockCoordinateDescent.
     * @throws java.io.FileNotFoundException
     * @throws labelinference.exceptions.ColumnOutOfRangeException
     * @throws labelinference.exceptions.RowOutOfRangeException
     * @throws labelinference.exceptions.DimensionNotAgreeException
     */
    @Test
    public void testGetResult() throws FileNotFoundException, ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
        System.out.println("\nBlockCoordinateDescent");
        Labor labor=Labor.getInstance();
        Map<Integer,Vertex> expResult;
        Map<Integer,Vertex> result;
        
        System.out.println("graph 1,top 5%:");
        expResult=labor.readGraph("data/1/testGraph.g");
        result=test("data/1/trainGraph5.g");
        labor.check(expResult,result);
        
        System.out.println("graph 1,top 10%:");
        result=test("data/1/trainGraph10.g");
        labor.check(expResult,result);
        
        System.out.println("graph 0:");
        expResult=labor.readGraph("data/0/testGraph.g");
        result=test("data/0/trainGraph.g");
        labor.check(expResult,result);
        
        System.out.println("graph 30,top 5%:");
        expResult=labor.readGraph("data/30/testGraph.g");
        result=test("data/30/trainGraph5.g");
        labor.check(expResult,result);
        
        System.out.println("graph 30,top 10%:");
        result=test("data/30/trainGraph10.g");
        labor.check(expResult,result);
    }
}

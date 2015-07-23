/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;
import org.junit.Test;

/**
 *
 * @author sailw
 */
public class MultiplicativeTest {
    
    public MultiplicativeTest() {
    }
    
    Map<Integer,Vertex> test(String path) throws ColumnOutOfRangeException, RowOutOfRangeException, FileNotFoundException {
        Labor labor=Labor.getInstance();
        Map<Integer,Vertex> graph=labor.readGraph(path);
        try {
            Multiplicative multiplicative=new Multiplicative(new Graph(graph.values()));
            multiplicative.getResult();
        } catch (DimensionNotAgreeException ex) {
            Logger.getLogger(MultiplicativeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return graph;
    }
    
    /**
     * Test of getResult method, of class Multiplicative.
     * @throws java.io.FileNotFoundException
     * @throws labelinference.exceptions.ColumnOutOfRangeException
     * @throws labelinference.exceptions.RowOutOfRangeException
     */
    @Test
    public void testGetResult() throws FileNotFoundException, ColumnOutOfRangeException, RowOutOfRangeException {
        System.out.println("\nMultiplicative");
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
    }
}

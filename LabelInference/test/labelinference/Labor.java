/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import labelinference.Graph.Vertex;
import java.io.FileNotFoundException;
import static java.lang.Math.abs;
import java.util.Collection;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.Graph.Graph;
import labelinference.LabelInference.LabelInference;
import labelinference.Selector.Selector;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.RowOutOfRangeException;
import static org.junit.Assert.fail;

/**
 *
 * @author sailw
 */
public class Labor {
    private static Labor instance;
    
    private Labor(){}
    
    public static Labor getInstance() {
        if(instance==null)instance=new Labor();
        return instance;
    }
    
    public void testLabelInference(String path, Function<Collection<Vertex>,Selector> selector, Function<Graph,LabelInference> labelInference) throws FileNotFoundException{
        System.out.println("\n"+path);
        Labor labor=Labor.getInstance();
        Graph expResult=new Graph(path);
        Graph graph=new Graph(path);
        Collection<Vertex> Y0=graph.getVertices(v->v.isY0());
        Selector selected=selector.apply(Y0);
        for(Vertex v:graph.getVertices())
            if(!selected.contains(v))v.init(v.getType(), v.getLabel(), false);
        labelInference.apply(graph).getResult();
        check(expResult,graph);
    }
    
    public void check(Graph expResult, Graph result){
        double correct=0;
        double totle=0;
        for(Vertex expV:expResult.getVertices()) {
            Vertex resV=result.findVertexByID(expV.getId());
            if(resV==null)System.out.println("null "+expV.getId());
            if(expV.isY0() && !resV.isY0()) {
                try {
                    totle+=1;
                    int expLabel=0;
                    int resLabel=0;
                    for(int row=0;row<expResult.getNumLabels();row++)
                        if(abs(expV.getLabel().get(row, 0)-1)<1e-9)expLabel=row;
                    for(int row=0;row<result.getNumLabels();row++)
                        if(resV.getLabel().get(row, 0)>resV.getLabel().get(resLabel, 0))resLabel=row;
                    if(expLabel==resLabel)correct++;
                } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {
                    Logger.getLogger(Labor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        Double acc=correct/totle;
        System.out.println("Accuracy="+acc);
        if(acc<0.5)fail("Accuracy is too low");
    }
    
    public void testSelector(String path, Function<Collection<Vertex>,Selector> selector) throws FileNotFoundException {
        Labor labor=Labor.getInstance();
        Graph graph=new Graph(path);
        Collection<Vertex> Y0=graph.getVertices(v->v.isY0());
        Selector selected=selector.apply(Y0);
        System.out.println(selected.size());
        for(Vertex v:graph.getVertices()) {
            if(selected.contains(v))
                System.out.println(v.getId());
        }
    }
}

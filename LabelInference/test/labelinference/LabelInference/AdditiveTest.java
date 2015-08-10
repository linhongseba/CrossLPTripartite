/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.function.Function;
import labelinference.Labor;
import labelinference.Selector.DegreeSelector;
import labelinference.Selector.Selector;
import org.junit.Test;
import static labelinference.LabelInference.LabelInference.*;
import labelinference.Selector.RandomSelector;
import labelinference.Selector.SimpleHeuristicSelector;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw
 */
public class AdditiveTest {
    /**
     * Test of getResult method, of class BlockCoordinateDescent.
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testGetResult() throws FileNotFoundException, DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException{
        System.out.println("\nBlockCoordinateDescent");
        Labor labor=Labor.getInstance();
        Function<Collection<Vertex>,Selector> selector10=g->new DegreeSelector(g,g.size()/10);
        Function<Collection<Vertex>,Selector> selector5=g->new DegreeSelector(g,g.size()/20);
        Function<Graph,LabelInference> labelInference=g->new Additive(g, LabelInference::randomLabelInit);
        
        //labor.testLabelInference("data/graph-1.txt",selector10,labelInference,100,0,DISP_ITER|DISP_DELTA|DISP_OBJ);
        labor.testLabelInference("data/graph-1.txt",selector5,labelInference,100,0,DISP_OBJ);
    }
}

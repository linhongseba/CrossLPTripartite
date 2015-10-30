/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.LabelInference;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.function.Function;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.Labor;
import labelinference.Selector.DegreeSelector;
import labelinference.Selector.Selector;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;
import labelinference2.LabelInference.LabelInference;
import static labelinference2.LabelInference.LabelInference.*;
import labelinference2.LabelInference.Multiplicative;
import org.junit.Test;

/**
 *
 * @author sailw
 */
public class MultiplicativeTest {
    /**
     * Test of getResult method, of class Multiplicative.
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testGetResult() throws FileNotFoundException, DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {
        System.out.println("\nMultiplicative");
        Labor labor=Labor.getInstance();
        Function<Collection<Vertex>,Selector> selector10=g->new DegreeSelector(g,g.size()/10);
        Function<Collection<Vertex>,Selector> selector5=g->new DegreeSelector(g,g.size()/20);
        Function<Graph,LabelInference> labelInference=g->new Multiplicative(g,LabelInference::noneInit);
        
        
        labor.testLabelInference("data/graph-30.txt",selector5,labelInference,100,-1,DISP_ITER|DISP_DELTA|DISP_OBJ);
        //labor.testLabelInference("data/graph-1.txt",selector5,labelInference,100,0,DISP_ITER|DISP_DELTA|DISP_OBJ|DISP_LABEL);
    }
}

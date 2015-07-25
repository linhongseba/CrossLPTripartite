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
import org.junit.Test;

/**
 *
 * @author sailw
 */
public class NewMultiplicativeTest {
    @Test
    public void testGetResult() throws FileNotFoundException {
        System.out.println("\nNewMultiplicative");
        Labor labor=Labor.getInstance();
        Function<Collection<Vertex>,Selector> selector10=g->new DegreeSelector(g,g.size()/10);
        Function<Collection<Vertex>,Selector> selector5=g->new DegreeSelector(g,g.size()/20);
        Function<Graph,LabelInference> labelInference=g->new NewMultiplicative(g);
        
        labor.testLabelInference("data/graph-30.txt",selector10,labelInference);
        labor.testLabelInference("data/graph-30.txt",selector5,labelInference);
    }
    
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.Selector;

import labelinference.Graph.Vertex;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.function.Function;
import labelinference.Labor;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.RowOutOfRangeException;
import org.junit.Test;

/**
 *
 * @author sailw
 */
public class RandomSelectorTest {
    @Test
    public void testRandomSelector() throws ColumnOutOfRangeException, RowOutOfRangeException, FileNotFoundException {
        System.out.println("\nRandomSelector");
        Labor labor=Labor.getInstance();
        Function<Collection<Vertex>,Selector> selector10=g->new RandomSelector(g,g.size()/10);

        labor.testSelector("data/graph-1.txt",selector10);
        labor.testSelector("data/graph-30.txt",selector10);
    }
}

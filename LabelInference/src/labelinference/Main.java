/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import java.io.FileNotFoundException;
import static java.lang.Math.abs;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.LabelInference.BlockCoordinateDescent;
import labelinference.LabelInference.LabelInference;
import labelinference.LabelInference.LabelPropagation;
import labelinference.LabelInference.Multiplicative;
import labelinference.Selector.DegreeSelector;
import labelinference.Selector.RandomSelector;
import labelinference.Selector.Selector;
import labelinference.Selector.SimpleHeuristicSelector;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw
 */
public class Main {
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        String path=args[0];
        double ratio=Double.parseDouble(args[1]);
        double nuance=Double.parseDouble(args[2]);
        int maxIter=Integer.parseInt(args[3]);
        
        Map<String,Function<Collection<Vertex>,Selector>> selectors=new HashMap<>();
        Map<String,Function<Graph,LabelInference>> inferencers=new HashMap<>();
        selectors.put("RND", g->new RandomSelector(g, (int)(g.size()*ratio)));
        selectors.put("DEG", g->new DegreeSelector(g, (int)(g.size()*ratio)));
        selectors.put("SHR", g->new SimpleHeuristicSelector(g, (int)(g.size()*ratio)));
        inferencers.put("MA", g->new Multiplicative(g,nuance,maxIter));
        inferencers.put("BCD", g->new BlockCoordinateDescent(g,nuance,maxIter));
        inferencers.put("LP", g->new LabelPropagation(g, 0.2,nuance,maxIter));
        
        for(String selector:selectors.keySet()) {
            System.out.print("Selector="+selector+"\n");
            for(String inferencer:inferencers.keySet()) {
                System.out.print("\tInferencer="+inferencer+"\n");
                
                System.out.print("\tReading graph data..."+"\n");
                Graph expResult=new Graph(path);
                Graph result=new Graph(path);

                System.out.print("\tSelecting train set..."+"\n");
                Collection<Vertex> Y0=result.getVertices(v->v.isY0());
                Selector selected=selectors.get(selector).apply(Y0);
                for(Vertex v:result.getVertices())
                    if(!selected.contains(v))v.init(v.getType(), v.getLabel(), false);

                System.out.print("\tInferencing..."+"\n");
                inferencers.get(inferencer).apply(result).getResult();

                System.out.print("\tChecking result..."+"\n");
                check(expResult,result);

                System.out.print("\tDone.\n"+"\n");
            }
        }
    }
    
    public static void check(Graph expResult, Graph result){
        double correct=0;
        double totle=0;
        for(Vertex expV:expResult.getVertices()) {
            Vertex resV=result.findVertexByID(expV.getId());
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
                } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
            }
        }

        Double acc=correct/totle;
        System.out.print("\tAccuracy="+acc+"\n");
    }
}

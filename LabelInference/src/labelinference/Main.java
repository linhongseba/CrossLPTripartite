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
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.LabelInference.Additive;
import labelinference.LabelInference.LabelInference;
import static labelinference.LabelInference.LabelInference.*;
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
		if(args.length<4){
			System.out.println("Usage: Java -jar [graph path] [inference algorithm] [selector algorithm] options");
			System.out.println("graph path (string type)");
			System.out.println("inference algorithm (string type): MA, BCD, LP");
			System.out.println("selector algorithm (string type): RND, DEG, SHR");
			System.out.println("option1: (double type): percentage of labeled data, default 0.05");
			System.out.println("option2: (double type): LP parameter");
			System.out.println("option3: (integer type): maximum number of iterations");
			System.exit(2);
		}
        String path=args[0]; //graph data directory
        String inferencer=args[1]; //the inference algorithm option
        String selector=args[2]; //the algorithm options to select seed nodes
        double ratio=0.05; //default ratio=5%
		double nuance=0.0; //default 0.0
		int maxIter=100; //default maximum number of iteration
		if(args.length>=4)
			ratio=Double.parseDouble(args[3]);
		if(args.length>=5)
			nuance=Double.parseDouble(args[4]);
		if(args.length>=6)
			maxIter=Integer.parseInt(args[5]);
        Map<String,Function<Collection<Vertex>,Selector>> selectors=new HashMap<>();
        Map<String,Function<Graph,LabelInference>> inferencers=new HashMap<>();
        selectors.put("RND", g->new RandomSelector(g, (int)(g.size()*ratio)));
        selectors.put("DEG", g->new DegreeSelector(g, (int)(g.size()*ratio)));
        selectors.put("SHR", g->new SimpleHeuristicSelector(g, (int)(g.size()*ratio)));
        inferencers.put("MA", g->new Multiplicative(g));
        inferencers.put("BCD", g->new Additive(g));
        inferencers.put("LP", g->new LabelPropagation(g, 0));
        
        System.out.print("Inferencer="+inferencer+"\n");
        System.out.print("Selector="+selector+"\n");

        System.out.print("Reading graph data..."+"\n");
        Graph expResult=new Graph(path);
        Graph result=new Graph(path);

        System.out.print("Selecting train set..."+"\n");
        Selector selected=selectors.get(selector).apply(result.getVertices(v->v.isY0()));
        Map<Vertex.Type,Double> tot=new HashMap<>();
        for(Vertex v:result.getVertices()) {
            if(!selected.contains(v))v.init(v.getType(), v.getLabel(), false);
            else tot.put(v.getType(), tot.getOrDefault(v.getType(),0.0)+1);
        }
        System.out.println("Number of typeA = "+tot.getOrDefault(Vertex.typeA,0.0));
        System.out.println("Number of typeB = "+tot.getOrDefault(Vertex.typeB,0.0));
        System.out.println("Number of typeC = "+tot.getOrDefault(Vertex.typeC,0.0));
        
        System.out.print("Inferencing..."+"\n");
        inferencers.get(inferencer).apply(result).getResult(maxIter,nuance,DISP_ALL^DISP_LABEL);

        System.out.print("Checking result..."+"\n");
        check(expResult,result);
    }
    
    static void check(Graph expResult, Graph result){
        double correct=0;
        double totle=0;
        Map<Vertex.Type,Double> cor=new HashMap<>();
        Map<Vertex.Type,Double> tot=new HashMap<>();
        for(Vertex expV:expResult.getVertices()) {
            Vertex resV=result.findVertexByID(expV.getId());
            if(expV.isY0() && !resV.isY0()) {
                try {
                    totle+=1;
                    tot.put(expV.getType(), tot.getOrDefault(expV.getType(),0.0)+1);
                    int expLabel=0;
                    int resLabel=0;
                    for(int row=0;row<expResult.getNumLabels();row++)
                        if(abs(expV.getLabel().get(row, 0)-1)<1e-9)expLabel=row;
                    for(int row=0;row<result.getNumLabels();row++)
                        if(resV.getLabel().get(row, 0)>resV.getLabel().get(resLabel, 0))resLabel=row;
                    if(expLabel==resLabel) {
                        correct++;
                        cor.put(expV.getType(), cor.getOrDefault(expV.getType(),0.0)+1);
                    }
                } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
            }
        }

        Double acc=correct/totle;
        System.out.println("Accuracy = "+acc);
        System.out.println("Accuracy of typeA = "+cor.get(Vertex.typeA)+"/"+tot.get(Vertex.typeA)+" = "+cor.getOrDefault(Vertex.typeA,0.0)/tot.getOrDefault(Vertex.typeA,0.0));
        System.out.println("Accuracy of typeB = "+cor.get(Vertex.typeB)+"/"+tot.get(Vertex.typeB)+" = "+cor.getOrDefault(Vertex.typeB,0.0)/tot.getOrDefault(Vertex.typeB,0.0));
        System.out.println("Accuracy of typeC = "+cor.get(Vertex.typeC)+"/"+tot.get(Vertex.typeC)+" = "+cor.getOrDefault(Vertex.typeC,0.0)/tot.getOrDefault(Vertex.typeC,0.0));
    }
}

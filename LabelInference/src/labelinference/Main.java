/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import java.io.FileNotFoundException;
import static java.lang.Math.abs;
import static java.lang.Math.random;
import static java.lang.Math.sqrt;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
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
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw
 */
public class Main {
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws labelinference.exceptions.DimensionNotAgreeException
     * @throws labelinference.exceptions.RowOutOfRangeException
     * @throws labelinference.exceptions.ColumnOutOfRangeException
     */
    public static void main(String[] args) throws FileNotFoundException, DimensionNotAgreeException, RowOutOfRangeException, ColumnOutOfRangeException {
        if(args.length<4){
            System.out.println("Usage: Java -jar [graph path] [inference algorithm] [selector algorithm] options");
            System.out.println("graph path (string type)");
            System.out.println("inference algorithm (string type): MA, BCD, LP");
            System.out.println("selector algorithm (string type): RND, DEG, SHR");
            System.out.println("option0: (double type): percentage of labeled data, default 0.05");
            System.out.println("option1: (double type): percentage of increment, default 0");
            System.out.println("option2: (double type): LP parameter");
            System.out.println("option3: (integer type): maximum number of iterations");
            System.out.println("option4: (double type): confidence level");
            System.exit(2);
        }
        final String path=args[0]; //graph data directory
        final double rol=Double.parseDouble(args[1]);
        final String inference=args[2]; //the inference algorithm option
        final String selector=args[3]; //the algorithm options to select seed nodes
        final double roi=args.length>=5?Double.parseDouble(args[4]):0; //default ratio=0
        final double a=args.length>=6?Double.parseDouble(args[5]):0.9;
        final double nuance=args.length>=6?Double.parseDouble(args[6]):0;//default 0.0
        final int maxIter=args.length>=7?Integer.parseInt(args[7]):100;//default maximum number of iteration
        
        
        final Map<String,Function<Collection<Vertex>,Selector>> selectors=new HashMap<>();
        final Map<String,Function<Graph,LabelInference>> inferences=new HashMap<>();
        selectors.put("RND", g->new RandomSelector(g, (int)(g.size()*rol)));
        selectors.put("DEG", g->new DegreeSelector(g, (int)(g.size()*rol)));
        selectors.put("SHR", g->new SimpleHeuristicSelector(g, (int)(g.size()*rol)));
        inferences.put("MRR", g->new Multiplicative(g,LabelInference.randomLabelInit));
        inferences.put("ARR", g->new Additive(g,LabelInference.randomLabelInit));
        inferences.put("MRG", g->new Multiplicative(g,LabelInference.noneInit));
        inferences.put("ARG", g->new Additive(g,LabelInference.noneInit));
        inferences.put("GRF", g->new LabelPropagation(g, 0));
        inferences.put("MRO", g->new Multiplicative(g,LabelInference.randomLabelInit){
            protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS){}
        });
        inferences.put("ARO", g->new Additive(g,LabelInference.randomLabelInit){
            protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS){}
        });
        
        System.out.print("Inferencer="+inference+"\n");
        System.out.print("Selector="+selector+"\n");

        Graph expResult=new Graph(path);
        Graph result=new Graph(path);
        Collection<Vertex> deltaGraph=new HashSet<>();
        
        Selector selected=selectors.get(selector).apply(result.getVertices(v->v.isY0()));
        Map<Vertex.Type,Double> nY0Inf=new HashMap<>();
        Map<Vertex.Type,Double> nY0Inc=new HashMap<>();
        for(Vertex v:result.getVertices()) {
            if(!selected.contains(v))v.init(v.getType(), v.getLabel(), false);
            if(random()<roi) {
                nY0Inc.put(v.getType(), nY0Inc.getOrDefault(v.getType(),0.0)+1);
                deltaGraph.add(v);
            } else {
                nY0Inf.put(v.getType(), nY0Inf.getOrDefault(v.getType(),0.0)+1);
            }
        }
        
        for(Vertex u:deltaGraph) {
            for(Vertex v:u.getNeighbors())
                if(!deltaGraph.contains(v))v.removeEdge(u);
            result.removeVertex(u);
        }
        
        long mTime=System.currentTimeMillis();
        LabelInference lp=new LabelPropagation(result,1);
        if(inference.charAt(inference.length()-1)=='G')lp.getResult(maxIter/10, nuance, DISP_NONE);
        LabelInference li=inferences.get(inference).apply(result);
        li.getResult(maxIter,nuance,DISP_ALL^DISP_LABEL);
        System.out.print(String.format("Processed in %d ms(total)\n",System.currentTimeMillis()-mTime));
        
        mTime=System.currentTimeMillis();
        if(inference.charAt(inference.length()-1)=='G'){
            lp.increase(deltaGraph,maxIter/10, nuance,a, DISP_NONE);
            for(Vertex u:deltaGraph) {
                for(Vertex v:u.getNeighbors())
                    if(!deltaGraph.contains(v))v.removeEdge(u);
                result.removeVertex(u);
            }
        }
        li.increase(deltaGraph, maxIter,nuance, a, DISP_ALL^DISP_LABEL);
        System.out.print(String.format("Processed in %d ms(total)\n",System.currentTimeMillis()-mTime));
        
        System.out.print("Incremental accuracy = "+check(expResult,deltaGraph)+"\n");
        System.out.print("Global accuracy = "+check(expResult,result.getVertices())+"\n");
        System.out.print("Done.\n");
    }
    
    static double check(Graph expResult, Collection<Vertex> result){
        double correct=0;
        double totle=0;
        int k=expResult.getNumLabels();
        double k1=1.0/k;
        for(Vertex resV:result) {
            Vertex expV=expResult.findVertexByID(resV.getId());
            if(expV.isY0() && !resV.isY0()) {
                try {
                    totle+=k;
                    for(int row=0;row<k;row++)
                        if(abs(expV.getLabel().get(row, 0))<1e-9^resV.getLabel().get(row, 0)>k1)correct++;
                } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
            }
        }
        Double acc=correct/totle;
        return acc;
    }
}
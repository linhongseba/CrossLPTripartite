/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import java.io.FileNotFoundException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
        final double roi=Double.parseDouble(args[4]); //default ratio=0
        final double a0=Double.parseDouble(args[5]);
        final double a1=Double.parseDouble(args[6]);
        final double nuance=args.length>=8?Double.parseDouble(args[7]):0;//default 0.0
        final int maxIter=args.length>=9?Integer.parseInt(args[8]):100;//default maximum number of iteration
        
        
        final Map<String,Function<Collection<Vertex>,Selector>> selectors=new HashMap<>();
        final Map<String,Function<Graph,LabelInference>> inferences=new HashMap<>();
        selectors.put("RND", g->new RandomSelector(g, (int)(g.size()*rol)));
        selectors.put("DEG", g->new DegreeSelector(g, (int)(g.size()*rol)));
        selectors.put("SHR", g->new SimpleHeuristicSelector(g, (int)(g.size()*rol)));
        inferences.put("MRR", g->new Multiplicative(g,LabelInference.defaultLabelInit));
        inferences.put("ARR", g->new Additive(g,LabelInference.defaultLabelInit));
        inferences.put("MRG", g->new Multiplicative(g,LabelInference.noneInit));
        inferences.put("ARG", g->new Additive(g,LabelInference.noneInit));
        inferences.put("GRF", g->new LabelPropagation(g, 0));
        inferences.put("MRO", g->new Multiplicative(g,LabelInference.defaultLabelInit){
            protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS){}
        });
        inferences.put("ARO", g->new Additive(g,LabelInference.defaultLabelInit){
            protected void updateB(Collection<Vertex> cand, Collection<Vertex> candS){}
        });
        
        System.out.print("Inferencer="+inference+"\n");
        System.out.print("Selector="+selector+"\n");

        Graph expResult=new Graph(path);
        Graph result=new Graph(path);
        Graph backup=new Graph(path);
        Collection<Vertex> deltaGraph=new HashSet<>();
        List<String> list=new ArrayList<>(expResult.getIDs());
        list.sort(String::compareTo);
        Collections.shuffle(list, new Random(1008611));
        
        Selector selected=selectors.get(selector).apply(result.getVertices(v->v.isY0()));
        Map<Vertex.Type,Double> nY0Inf=new HashMap<>();
        Map<Vertex.Type,Double> nY0Inc=new HashMap<>();
        for(Vertex v:result.getVertices())
            if(!selected.contains(v))v.init(v.getType(), v.getLabel(), false);
        
        for(int i=0;i<roi*result.getVertices().size();i++)deltaGraph.add(result.findVertexByID(list.get(i)));
        
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
        
        for(Vertex v:result.getVertices())
            backup.findVertexByID(v.getId()).setLabel(v.getLabel().copy());
        
        System.out.print("Recompute2\n");
        mTime=System.currentTimeMillis();
        if(inference.charAt(inference.length()-1)=='G')lp.recompute(deltaGraph,maxIter/10, nuance, DISP_NONE);
        for(Vertex u:deltaGraph) {
            for(Vertex v:u.getNeighbors())
                if(!deltaGraph.contains(v))v.removeEdge(u);
            result.removeVertex(u);
        }
        li.recompute(deltaGraph, maxIter,nuance, DISP_ALL^DISP_LABEL);
        System.out.print(String.format("Processed in %d ms(total)\n",System.currentTimeMillis()-mTime));
        System.out.print("Incremental\n");
        check(expResult,deltaGraph);
        System.out.print("Global\n");
        check(expResult,result.getVertices());
        
        for(double a=a0;a<=a1;a+=0.1) {
            System.out.print("confidence="+a+"\n");
            for(Vertex u:deltaGraph) {
                for(Vertex v:u.getNeighbors())
                    if(!deltaGraph.contains(v))v.removeEdge(u);
                result.removeVertex(u);
            }
            for(Vertex v:backup.getVertices())
                if(result.findVertexByID(v.getId())!=null)
                    result.findVertexByID(v.getId()).setLabel(v.getLabel().copy());
            mTime=System.currentTimeMillis();
            if(inference.charAt(inference.length()-1)=='G')
                lp.increase(deltaGraph,maxIter/10, nuance,a, DISP_NONE);
            for(Vertex u:deltaGraph) {
                for(Vertex v:u.getNeighbors())
                    if(!deltaGraph.contains(v))v.removeEdge(u);
                result.removeVertex(u);
            }
            li.increase(deltaGraph, maxIter,nuance, a, DISP_ALL^DISP_LABEL);
            System.out.print(String.format("Processed in %d ms(total)\n",System.currentTimeMillis()-mTime));

            System.out.print("Incremental\n");
            check(expResult,deltaGraph);
            System.out.print("Global\n");
            check(expResult,result.getVertices());
        }
        System.out.print("Done.\n");
    }
    
    static void check(Graph expResult, Collection<Vertex> result){
        Map<Vertex.Type,Integer> totle=new HashMap<>();
        Map<Vertex.Type,Integer> num=new HashMap<>();
        Map<Vertex.Type,Double> correct=new HashMap<>();
        Map<Vertex.Type,Integer> labeled=new HashMap<>();
        int k=expResult.getNumLabels();
        int nol[]=new int[k];
        int nor[]=new int[k];
        int A[][]=new int[k][k];
        try {
            double k1=1.0/k;
            for(Vertex resV:result) {
                Vertex expV=expResult.findVertexByID(resV.getId());
                num.put(expV.getType(), num.getOrDefault(expV.getType(),0)+1);
                if(resV.isY0()) {
                    labeled.put(resV.getType(),labeled.getOrDefault(resV.getType(), 0)+1);
                    for(int row=0;row<k;row++)if(resV.getLabel().get(row, 0)>k1)nor[row]++;
                }
                if(expV.isY0())for(int row=0;row<k;row++)if(expV.getLabel().get(row, 0)>k1)nol[row]++;
                if(expV.isY0() && !resV.isY0()) {
                    totle.put(expV.getType(), totle.getOrDefault(expV.getType(),0)+k);
                    for(int row=0;row<k;row++) {
                        if(abs(expV.getLabel().get(row, 0))<1e-9^resV.getLabel().get(row, 0)>k1)
                            correct.put(expV.getType(), correct.getOrDefault(expV.getType(),0.0)+1);
                        for(int r=0;r<k;r++)if(abs(expV.getLabel().get(row, 0))<1e-9^resV.getLabel().get(r, 0)>k1)A[row][r]++;
                    }
                }
            }
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        System.out.print(String.format("Totle = %d(A), %d(B), %d(C), %d(all)\n",num.getOrDefault(Vertex.typeA,0),
                                                                                num.getOrDefault(Vertex.typeB,0),
                                                                                num.getOrDefault(Vertex.typeC,0),
                                                                                num.getOrDefault(Vertex.typeA,0)+
                                                                                num.getOrDefault(Vertex.typeB,0)+
                                                                                num.getOrDefault(Vertex.typeC,0)));
        System.out.print(String.format("Labeled = %d(A), %d(B), %d(C), %d(all)\n",labeled.getOrDefault(Vertex.typeA,0),
                                                                                labeled.getOrDefault(Vertex.typeB,0),
                                                                                labeled.getOrDefault(Vertex.typeC,0),
                                                                                labeled.getOrDefault(Vertex.typeA,0)+
                                                                                labeled.getOrDefault(Vertex.typeB,0)+
                                                                                labeled.getOrDefault(Vertex.typeC,0)));
        System.out.print("AllLabels : "+Arrays.toString(nol)+"\n");
        System.out.print("TrainLabels : "+Arrays.toString(nor)+"\n");
        System.out.print(String.format("Accuracy = %.6f(A), %.6f(B), %.6f(C), %.6f(all)\n",correct.getOrDefault(Vertex.typeA,0.0)/totle.getOrDefault(Vertex.typeA,0),
                                                                                            correct.getOrDefault(Vertex.typeB,0.0)/totle.getOrDefault(Vertex.typeB,0),
                                                                                            correct.getOrDefault(Vertex.typeC,0.0)/totle.getOrDefault(Vertex.typeC,0),
                                                                                            (correct.getOrDefault(Vertex.typeA,0.0)+
                                                                                            correct.getOrDefault(Vertex.typeB,0.0)+
                                                                                            correct.getOrDefault(Vertex.typeC,0.0))/
                                                                                            (totle.getOrDefault(Vertex.typeA,0)+
                                                                                            totle.getOrDefault(Vertex.typeB,0)+
                                                                                            totle.getOrDefault(Vertex.typeC,0))));
        double ber=1;
        for(int row=0;row<k;row++) {
            double frac=0;
            for(int col=0;col<k;col++)frac+=A[row][col];
            ber-=A[row][row]/frac/k;
        }
        System.out.print("BER : "+ber+"\n");
    }
}
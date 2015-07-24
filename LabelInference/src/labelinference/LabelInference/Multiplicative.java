/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package labelinference.LabelInference;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;
import labelinference.Matrix.Matrix;
import labelinference.Matrix.MatrixFactory;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;


/**
 *
 * @author Tangyiqi
 */

public class Multiplicative implements LabelInference {

    
    private final Graph g;
    private boolean isDone;
    private final double nuance;
    private final int k;
    private final int maxIter;
    private Matrix Y0,Y,lastY;
	//private Matrix Gab,Gbc,Gac;
	private int N;
	Map<Vertex.Type,Integer> Ntype=new HashMap<>();
	Map<Vertex.Type,Matrix> Stype=new HashMap<>();
	Map<Vertex.Type,Matrix> Ytype=new HashMap<>();
	Map<Vertex.Type, Map<Vertex.Type,Matrix>> Gtype=new HashMap<>();
	//Graph local_g;
    Map<Vertex,Integer> map=new HashMap();
    
	
    public Multiplicative(Graph _g, int _k) {	
        g=_g;
        k=_k;
        isDone=false;
        nuance=1e-4;
        maxIter=100;
        labelInit=(Integer x)->LabelInference.defaultLabelInit(x);
    }
    
    public Multiplicative(Graph _g, int _k,double  _nuance, int _maxIter) {
        g=_g;
        k=_k;
        isDone=false;
        nuance=_nuance;
        maxIter=_maxIter;
        labelInit=(Integer x)->LabelInference.defaultLabelInit(x);
    }
    
    public Multiplicative(Graph _g, int _k,double  _nuance, int _maxIter, Function<Integer,Matrix> _labelInit) {
        g=_g;
        k=_k;
        isDone=false;
        nuance=_nuance;
        maxIter=_maxIter;
        labelInit=_labelInit;
    }
    
    private double update() throws DimensionNotAgreeException,ColumnOutOfRangeException, RowOutOfRangeException 
    {
        MatrixFactory F;
        F=MatrixFactory.getInstance();
        lastY=F.creatMatrix(N,k);

    	Map<Vertex.Type,Matrix> Ynew_type=new HashMap<>();
    	
    	
    	Map<Vertex.Type,Matrix> temp_up=new HashMap<>();
    	Map<Vertex.Type,Matrix> temp_down=new HashMap<>();
        
    	
    	for(Vertex.Type type:Ntype.keySet()) 
        	Ynew_type.put(type, Ytype.get(type).copy());
        lastY=Y.copy();

        //initialize Ya Yb Yc
        
        for(Vertex.Type type:Ntype.keySet()) 
        {
			Vertex.Type typeb=null;
			Vertex.Type typec=null;
			for(Vertex.Type subtype:Ntype.keySet()) 
	    		if(type!=subtype)
	    		{
	    			if(typeb==null)
	    				typeb=subtype;
	    			else
	    				typec=subtype;
	        	}
	    	temp_up.put(type,F.creatMatrix(Ntype.get(type),k));
        	temp_down.put(type,F.creatMatrix(Ntype.get(type),k));

        	temp_up.put(type,((Gtype.get(type).get(typeb).times(Ytype.get(typeb))).add(Gtype.get(type).get(typec).times(Ytype.get(typec)))).add(Stype.get(type).times(Y0)));
        	temp_down.put(type,Ytype.get(type).times(Ytype.get(typeb).transpose()).times(Ytype.get(typeb)).add(Ytype.get(type).times(Ytype.get(typec).transpose()).times(Ytype.get(typec))).add(Stype.get(type).times(Y)));

        	//calculate the numerator and denominator
        	Ynew_type.put(type,(Ytype.get(type).cron(temp_up.get(type).divide(temp_down.get(type)).sqrt())).copy());
        	//calculate Ya
        }

    	for(Vertex.Type type:Ntype.keySet()) 
    	{
    		for(int i=0;i<Ntype.get(type);i++)
    		{
    			Matrix temp=F.creatMatrix(1,k);
    			temp=Ynew_type.get(type).getRow(i).orthonormalizeCol();
    			Ynew_type.get(type).setRow(i, temp);
    		}
    	}
    	for(Vertex.Type type:Ntype.keySet()) 
    		Ytype.put(type, (Ynew_type.get(type)).copy());

		Y.setM(0,Ntype.get(Vertex.typeA)-1,0,1,Ynew_type.get(Vertex.typeA));
        Y.setM(Ntype.get(Vertex.typeA),Ntype.get(Vertex.typeA)+Ntype.get(Vertex.typeB)-1,0,1,Ynew_type.get(Vertex.typeB));
        Y.setM(Ntype.get(Vertex.typeA)+Ntype.get(Vertex.typeB),N-1,0,1,Ynew_type.get(Vertex.typeC));
        
        double delta=Ytype.get(Vertex.typeA).subtract(Ynew_type.get(Vertex.typeA)).norm(Matrix.FROBENIUS_NORM)+Ytype.get(Vertex.typeB).subtract(Ynew_type.get(Vertex.typeB)).norm(Matrix.FROBENIUS_NORM)+Ytype.get(Vertex.typeC).subtract(Ynew_type.get(Vertex.typeC)).norm(Matrix.FROBENIUS_NORM);
        
        return delta;
    }

    @Override
    public Graph getResult() {
        MatrixFactory F=MatrixFactory.getInstance();
        try {
        	F=MatrixFactory.getInstance();
        	
        	for (Vertex v : g.getVertices())if(!v.isY0())v.setLabel(labelInit.apply(k));
        	
        	Ntype.put(Vertex.typeA, new Integer(0));
        	Ntype.put(Vertex.typeB, new Integer(0));
        	Ntype.put(Vertex.typeC, new Integer(0));

            N=0;

        	Map<Vertex.Type,Integer> ntype=new HashMap<>();
        	for(Vertex.Type type:Ntype.keySet()) 
        		ntype.put(type, new Integer(0));
        	
        	int n=0;
            
    		for(Vertex u:g.getVertices()) {
    			ntype.put(u.getType(),ntype.get(u.getType())+1);
    			n++;
    	   	}
    		
    		Map<Vertex.Type,int[]> numtype=new HashMap<>();
    		for(Vertex.Type type:Ntype.keySet()) 
    			numtype.put(type,new int[n]);
        	
    		Y0=F.creatMatrix(n,k);
    		Y=F.creatMatrix(n,k);
    		for(Vertex.Type type:Ntype.keySet()) 
    			Stype.put(type, F.creatMatrix(ntype.get(type),n));

    		for(Vertex.Type type:Ntype.keySet()) 
    			Ytype.put(type, F.creatMatrix(ntype.get(type),2));
        	    		
    		Map<Vertex.Type,Matrix> mapa=new HashMap<>();
            mapa.put(Vertex.typeB,F.creatMatrix(ntype.get(Vertex.typeA),ntype.get(Vertex.typeB)));
    		mapa.put(Vertex.typeC,F.creatMatrix(ntype.get(Vertex.typeA),ntype.get(Vertex.typeC)));
    		Gtype.put(Vertex.typeA, mapa);
    		Map<Vertex.Type,Matrix> mapb=new HashMap<Vertex.Type,Matrix>();
    		mapb.put(Vertex.typeA,F.creatMatrix(ntype.get(Vertex.typeB),ntype.get(Vertex.typeA)));
    		mapb.put(Vertex.typeC,F.creatMatrix(ntype.get(Vertex.typeB),ntype.get(Vertex.typeC)));
    		Gtype.put(Vertex.typeB, mapb);
    		Map<Vertex.Type,Matrix> mapc=new HashMap<Vertex.Type,Matrix>();
    		mapc.put(Vertex.typeA,F.creatMatrix(ntype.get(Vertex.typeC),ntype.get(Vertex.typeA)));
    		mapc.put(Vertex.typeB,F.creatMatrix(ntype.get(Vertex.typeC),ntype.get(Vertex.typeB)));
    		Gtype.put(Vertex.typeC, mapc);
    			
            
    		for(Vertex point:g.getVertices()) {
    			//map.put(N, point);
    			Vertex.Type type=point.getType();
    			
    				numtype.get(type)[N]=Ntype.get(type);
                	//System.out.println(map.get(point));
        			int cnt=0;
        			if(type==Vertex.typeA)
        				cnt=Ntype.get(type);
        			if(type==Vertex.typeB)
        				cnt=ntype.get(Vertex.typeA)+Ntype.get(type);
        			if(type==Vertex.typeC)
        				cnt=ntype.get(Vertex.typeA)+ntype.get(Vertex.typeB)+Ntype.get(type);
        			//get the No.N Y node's position in Ya/Yb/Yc matrices 
        			map.put(point,cnt);
                            
            		if(point.isY0())
            		{
            			Y0.setRow(cnt, point.getLabel().transpose());
            			Ytype.get(type).setRow(Ntype.get(type), point.getLabel().transpose());
            			Stype.get(type).set(Ntype.get(type), cnt, 1);//set Sa
            		}
            		else
            		{
            			Y0.set(cnt,0,0.5);
            			Y0.set(cnt,1,0.5);
            			Ytype.get(type).set(Ntype.get(type),0,0.5);
           		    	Ytype.get(type).set(Ntype.get(type),1,0.5);
            		}
            		
            		Ntype.put(type,Ntype.get(type)+1);
        		
        		
            	g.addVertex(point);
            	//set the vertices in the output graph. 
        		N++;
            }//get Y0,Ya,Yb,Yc,Sa,Sb,Sc

    		for(Vertex.Type type:Ntype.keySet()) 
        		ntype.put(type, 0);
    		int tt=0;
    		for(Vertex u:g.getVertices()) {
        		
        		Vertex.Type type=u.getType();
        		
        			int cnt=0;
    		
        			for(Vertex v:g.getVertices())
        			{
        			if(u.getType()!=v.getType())
       	    		{
        	    		//System.out.println(v.getNeighbors());
        	 	           Vertex.Type subtype=v.getType();
        	    			Gtype.get(type).get(subtype).set(ntype.get(type),numtype.get(subtype)[cnt],u.getEdge(v));
        	       			Gtype.get(subtype).get(type).set(numtype.get(subtype)[cnt],ntype.get(type),u.getEdge(v));
        	       }
    		    	cnt++;
        			}
    		    	//set G matrices
     	    		ntype.put(type,ntype.get(type)+1);
     	           tt++;
        	}
    		Y=Y0.copy();    	


            double delta;
            int iter=0;
            do {
                delta=update()/g.getVertices().size();
                iter++;
                System.out.println(delta);
            } while(delta>nuance && iter!=maxIter);

            for(Vertex v:g.getVertices())
                if(!v.isY0())
                	v.setLabel(Y.getRow(map.get(v)).transpose());

        } catch (DimensionNotAgreeException | ColumnOutOfRangeException | RowOutOfRangeException ex) {
            Logger.getLogger(Multiplicative.class.getName()).log(Level.SEVERE, null, ex);
        }
        return g;
    }
}

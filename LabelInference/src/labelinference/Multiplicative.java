/*

 * To change this license header, choose License Headers in Project Properties.

 * To change this template file, choose Tools | Templates

 * and open the template in the editor.

 */

package labelinference;
import java.util.*;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;


/**
 *
 * @author Tangyiqi
 */

public class Multiplicative implements LabelInference {

	private Matrix Y0,Y,lastY;
	private Matrix Ya,Yb,Yc;
	private Matrix Gab,Gbc,Gac;
	private int Na,Nb,Nc,N;
	private Matrix Sa,Sb,Sc;
	int[] node;
	Graph local_g;
	
	private void randomSet(int row,int row1,int type) throws ColumnOutOfRangeException, RowOutOfRangeException
	{
		if(Math.random()<(1/3.0))
		{
			Y0.set(row,0,0);
			Y0.set(row,1,1);
			if(type==1)
			{
				Ya.set(row1,0,0);
				Ya.set(row1,1,1);
			}
			if(type==2)
			{
				Yb.set(row1,0,0);
				Yb.set(row1,1,1);
			}
			if(type==3)
			{
				Yc.set(row1,0,0);
				Yc.set(row1,1,1);
			}
			return;
		}
		if(Math.random()<(2/3.0))
		{
			Y0.set(row,0,0.5);
			Y0.set(row,1,0.5);
			if(type==1)
			{
				Ya.set(row1,0,0.5);
				Ya.set(row1,1,0.5);
			}
			if(type==2)
			{
				Yb.set(row1,0,0.5);
				Yb.set(row1,1,0.5);
			}
			if(type==3)
			{
				Yc.set(row1,0,0.5);
				Yc.set(row1,1,0.5);
			}
			return;
		}
		Y0.set(row,0,1);
		Y0.set(row,1,0);
		if(type==1)
		{
			Ya.set(row1,0,1);
			Ya.set(row1,1,0);
		}
		if(type==2)
		{
			Yb.set(row1,0,1);
			Yb.set(row1,1,0);
		}
		if(type==3)
		{
			Yc.set(row1,0,1);
			Yc.set(row1,1,0);
		}
		return;
	}
	
    public Multiplicative(Graph _g) throws RowOutOfRangeException, DimensionNotAgreeException, ColumnOutOfRangeException {
    	
    	
    	
    	local_g=new Graph();
    	Collection<Vertex> points;
    	points=_g.getVertices();
    	Iterator<Vertex> it = points.iterator();
    	MatrixFactory F;
    	F=MatrixFactory.getInstance();

		Na=0;Nb=0;Nc=0;N=0;
    	int na=0,nb=0,nc=0;
    	
	   	while (it.hasNext()) {
		 	Object element = it.next();
			Vertex point=(Vertex)element;
			if(point.getType()==Vertex.typeA)
    			na++;
			if(point.getType()==Vertex.typeB)
    			nb++;
			if(point.getType()==Vertex.typeC)
    			nc++;
	   	}
    	int[] num_a=new int[na+nb+nc];
    	int[] num_b=new int[na+nb+nc];
    	int[] num_c=new int[na+nb+nc];
		Y0=F.creatMatrix(na+nb+nc,2);
		Y=F.creatMatrix(na+nb+nc,2);
		Sa=F.creatMatrix(na,na+nb+nc);
		Sb=F.creatMatrix(nb,na+nb+nc);
		Sc=F.creatMatrix(nc,na+nb+nc);
    	node=new int[na+nb+nc];
    	Ya=F.creatMatrix(na,2);
    	Yb=F.creatMatrix(nb,2);
    	Yc=F.creatMatrix(nc,2);
    	Gab=F.creatMatrix(na,nb);
    	Gbc=F.creatMatrix(nb,nc);
    	Gac=F.creatMatrix(na,nc);
			
    	it = points.iterator();
    	while (it.hasNext()) {
	   	 	Object element = it.next();
    		Vertex point=(Vertex)element;
    		if(point.getType()==Vertex.typeA)
    		{
    			num_a[N]=Na;
    			node[N]=Na;
        		if(point.isY0())
        		{
        			Y0.setRow(Na, point.getLabel().transpose());
        			Ya.setRow(Na, point.getLabel().transpose());
        			Sa.set(Na, Na, 1);//set Sa
        		}
        		else
        			randomSet(Na,Na,1);
    			Na++;
    		}
            if(point.getType()==Vertex.typeB)
    		{
    			num_b[N]=Nb;
    			node[N]=na+Nb;
        		if(point.isY0())
        		{
        			Y0.setRow(na+Nb, point.getLabel().transpose());
        			Yb.setRow(Nb, point.getLabel().transpose());
        			Sb.set(Nb, na+Nb, 1);
        		}
        		else
        			randomSet(na+Nb,Nb,2);
    			Nb++;
    		}
    		if(point.getType()==Vertex.typeC)
    		{
    			num_c[N]=Nc;
    			node[N]=na+nb+Nc;
        		if(point.isY0())
        		{
        			Y0.setRow(na+nb+Nc, point.getLabel().transpose());
        			Yc.setRow(Nc, point.getLabel().transpose());
        			Sc.set(Nc, na+nb+Nc, 1);
        		}
        		else
        			randomSet(na+nb+Nc,Nc,3);
    			Nc++;
    		}
    		
        	Vertex TempPoint=new Vertex(point.getType(),point.getLabel(),point.isY0());
        	local_g.addVertex(TempPoint);
    		N++;
        }//get Y0,Ya,Yb,Yc,Sa,Sb,Sc
    	na=0;nb=0;nc=0;
    	it = points.iterator();
    	while (it.hasNext()) {
    		Object element = it.next();
    		Vertex point=(Vertex)element;
    		if(point.getType()==Vertex.typeA)
    		{
    			Iterator<Vertex> jt=points.iterator();
    			int cnt=0;
    			while(jt.hasNext()){
    	    		Object _element = jt.next();
    	    		Vertex _point=(Vertex)_element;
    	    		if(_point.getType()==Vertex.typeB)
    	    			Gab.set(na,num_b[cnt],point.getEdge(_point));

    	    		if(_point.getType()==Vertex.typeC)
    	        		Gac.set(na,num_c[cnt],point.getEdge(_point));
    	    		
    	    		local_g.addEdge(point, _point, point.getEdge(_point));
    	    		cnt++;
    			}
    			na++;
    		}
    		if(point.getType()==Vertex.typeB)
    		{
    			Iterator<Vertex> jt=points.iterator();
    			int cnt=0;
    			while(jt.hasNext()){
    	    		Object _element = jt.next();
    	    		Vertex _point=(Vertex)_element;
    	    		if(_point.getType()==Vertex.typeA)
    	        		Gab.set(num_a[cnt],nb,point.getEdge(_point));
    	    		if(_point.getType()==Vertex.typeC)
    	        		Gbc.set(nb,num_c[cnt],point.getEdge(_point));
    	    		local_g.addEdge(point, _point, point.getEdge(_point));
    	    		cnt++;
    			}
    			nb++;
    		}
    		if(point.getType()==Vertex.typeC)
    		{
    			Iterator<Vertex> jt=points.iterator();
    			
    			int cnt=0;
    			while(jt.hasNext()){
    	    		Object _element = jt.next();
    	    		Vertex _point=(Vertex)_element;
    	    		if(_point.getType()==Vertex.typeA)
    	        		Gac.set(num_a[cnt],nc,point.getEdge(_point));
    	    		if(_point.getType()==Vertex.typeB)
    	        		Gbc.set(num_b[cnt],nc,point.getEdge(_point));

    	    		local_g.addEdge(point, _point, point.getEdge(_point));
    	    		cnt++;
    			}
    			nc++;
    		}
    	}
		Y.clone(Y0);    	

    }
    
	private void update() throws DimensionNotAgreeException,ColumnOutOfRangeException, RowOutOfRangeException 

	{
		MatrixFactory F;
    	F=MatrixFactory.getInstance();
		lastY=F.creatMatrix(N,2);

		Matrix Ya_new=F.creatMatrix(Na,2);
		Matrix Yb_new=F.creatMatrix(Nb,2);
		Matrix Yc_new=F.creatMatrix(Nc,2);

		Ya_new.clone(Ya);
		Yb_new.clone(Yb);
		Yc_new.clone(Yc);
		lastY.clone(Y);

		//initialize Ya Yb Yc

    	Matrix temp_a_up=F.creatMatrix(Na,2);
		Matrix temp_a_down=F.creatMatrix(Na,2);
		temp_a_up=((Gab.times(Yb)).add(Gac.times(Yc))).add(Sa.times(Y0));
		temp_a_down=Ya.times(Yb.transpose()).times(Yb).add(Ya.times(Yc.transpose()).times(Yc)).add(Sa.times(Y));

		//calculate the numerator and denominator
		Ya_new.clone(Ya.cron(temp_a_up.divide(temp_a_down).sqrt()));
		//calculate Ya


		Matrix temp_b_up=F.creatMatrix(Nb,2);
		Matrix temp_b_down=F.creatMatrix(Nb,2);
		temp_b_up=((Gab.transpose()).times(Ya)).add(Gbc.times(Yc)).add(Sb.times(Y0));
		temp_b_down=Yb.times(Ya.transpose()).times(Ya).add(Yb.times(Yc.transpose()).times(Yc)).add(Sb.times(Y));

		//calculate the numerator and denominator
		Yb_new.clone(Yb.cron(temp_b_up.divide(temp_b_down).sqrt()));
		//calculate Yb

		

		Matrix temp_c_up=F.creatMatrix(Nc,2);
		Matrix temp_c_down=F.creatMatrix(Nc,2);
		temp_c_up=Gac.transpose().times(Ya).add(Gbc.transpose().times(Yb)).add(Sc.times(Y0));
		temp_c_down=Yc.times(Ya.transpose()).times(Ya).add(Yc.times(Yb.transpose()).times(Yb)).add(Sc.times(Y));

		//calculate the numerator and denominator
		Yc_new.clone(Yc.cron(temp_c_up.divide(temp_c_down).sqrt()));
		//calculate Yc

		
		Ya.clone(Ya_new);
		Yb.clone(Yb_new);
		Yc.clone(Yc_new);
		Y.setM(0,Na-1,0,1,Ya_new);
		Y.setM(Na,Na+Nb-1,0,1,Yb_new);
		Y.setM(Na+Nb,Na+Nb+Nc-1,0,1,Yc_new);

	}

    
	private boolean converge() throws DimensionNotAgreeException
	{
		
        double x=Y.subtract(lastY).norm(Matrix.FROBENIUS_NORM);
	
        System.out.println(x);

		double nuance=0.01;
		return x<nuance;
	}
    @Override

    public Graph getResult() throws DimensionNotAgreeException, ColumnOutOfRangeException, RowOutOfRangeException {
    	
    	Graph TempGraph=new Graph();
    
    	//iteration
    	do
    	{
		 	update();
    	}while(!converge());
	    System.out.println("YY:"+Y);
		
        System.out.println("&&&&&");
    	int n=0;
    	Collection<Vertex> points;
    	points=local_g.getVertices();
    	Iterator<Vertex> it = points.iterator();
    	while (it.hasNext()) {
        	Object element = it.next();
        	Vertex point=(Vertex)element;
        	Vertex TempPoint=new Vertex(point.getType(),Y.getRow(node[n]),point.isY0());
        	TempGraph.addVertex(TempPoint);
        	n++;
    	}
       	
    	it = points.iterator();
    	while (it.hasNext()) {
    		Object element = it.next();
    		Vertex point=(Vertex)element;
    		Iterator<Vertex> jt=points.iterator();

			while(jt.hasNext()){
	    		Object _element = jt.next();
	    		Vertex _point=(Vertex)_element;
	    		TempGraph.addEdge(point, _point, point.getEdge(_point));
			}
    	}
       	return TempGraph;
    }

    

}



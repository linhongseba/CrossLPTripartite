/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package labelinference.LabelInference;
import labelinference.LabelInference.LabelInference;
import labelinference.Matrix.MatrixFactory;
import labelinference.Matrix.Matrix;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import labelinference.Graph.Graph;
import labelinference.Graph.Vertex;

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
	Graph local_g;
    Map<Vertex,Integer> map=new HashMap();
    
    /*The method produces the random elements in matrices:Y0/Ya/Yb/Yc */
	private void randomSet(int row,int row1,int type) throws ColumnOutOfRangeException, RowOutOfRangeException
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
	
    /*The method initializes all matrices:Y0/Ya/Yb/Yc/Sa/Sb/Sc/Gab/Gac/Gbc */
    public Multiplicative(Graph _g) throws RowOutOfRangeException, DimensionNotAgreeException, ColumnOutOfRangeException {

    	local_g=new Graph();
    	MatrixFactory F;
    	F=MatrixFactory.getInstance();
    	//

		Na=0;Nb=0;Nc=0;N=0;
    	int na=0,nb=0,nc=0;
		for(Vertex u:_g.getVertices()) {
			if(u.getType()==Vertex.typeA)
    			na++;
			if(u.getType()==Vertex.typeB)
    			nb++;
			if(u.getType()==Vertex.typeC)
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
    	Ya=F.creatMatrix(na,2);
    	Yb=F.creatMatrix(nb,2);
    	Yc=F.creatMatrix(nc,2);
    	Gab=F.creatMatrix(na,nb);
    	Gbc=F.creatMatrix(nb,nc);
    	Gac=F.creatMatrix(na,nc);
			
		for(Vertex point:_g.getVertices()) {
			//map.put(N, point);
    		if(point.getType()==Vertex.typeA)
    		{
    			num_a[N]=Na;
    			map.put(point,Na);
            	//System.out.println(map.get(point));
            	
    			//get the No.N Y node's position in Ya/Yb/Yc matrices 
                        
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
    			map.put(point,na+Nb);
            	//System.out.println(map.get(point));
    			//get the No.N Y node's position in Ya/Yb/Yc matrices 

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
    			map.put(point,na+nb+Nc);
            	//System.out.println(map.get(point));
    			//get the No.N Y node's position in Ya/Yb/Yc matrices 

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
    		
        	local_g.addVertex(point);
        	//set the vertices in the output graph. 
    		N++;
        }//get Y0,Ya,Yb,Yc,Sa,Sb,Sc
		
    	na=0;nb=0;nc=0;
    	for(Vertex u:_g.getVertices()) {
    		if(u.getType()==Vertex.typeA)
    		{
    			int cnt=0;
    	    	for(Vertex v:_g.getVertices()) {
        			if(v.getType()==Vertex.typeB)
    	    			Gab.set(na,num_b[cnt],u.getEdge(v));
    	    		if(v.getType()==Vertex.typeC)
    	        		Gac.set(na,num_c[cnt],u.getEdge(v));
    	    		//set G matrices
    	    		
    	    		local_g.addEdge(u, v, u.getEdge(v));
    	        	//set the edges in the output graph. 
    	    		cnt++;
    			}
    			na++;
    		}
    		if(u.getType()==Vertex.typeB)
    		{
    			int cnt=0;
    			for(Vertex v:_g.getVertices()) {
            			if(v.getType()==Vertex.typeA)
    	        		Gab.set(num_a[cnt],nb,u.getEdge(v));
    	    		if(v.getType()==Vertex.typeC)
    	        		Gbc.set(nb,num_c[cnt],u.getEdge(v));
    	    		//set G matrices
    	    		
    	    		local_g.addEdge(u, v, u.getEdge(v));
    	        	//set the edges in the output graph. 
    	    		cnt++;
    			}
    			nb++;
    		}
    		if(u.getType()==Vertex.typeC)
    		{
    			int cnt=0;
    			for(Vertex v:_g.getVertices()) {
                	if(v.getType()==Vertex.typeA)
    	        		Gac.set(num_a[cnt],nc,u.getEdge(v));
    	    		if(v.getType()==Vertex.typeB)
    	        		Gbc.set(num_b[cnt],nc,u.getEdge(v));
    	    		//set G matrices

    	    		local_g.addEdge(u, v, u.getEdge(v));
    	        	//set the edges in the output graph. 
    	    		cnt++;
    			}
    			nc++;
    		}
    	}
		Y=Y0.copy();    	
    }
    
    /*The method updates the matrices */
	private void update() throws DimensionNotAgreeException,ColumnOutOfRangeException, RowOutOfRangeException 
	{
            MatrixFactory F;
            F=MatrixFactory.getInstance();
            lastY=F.creatMatrix(N,2);

            Matrix Ya_new=F.creatMatrix(Na,2);
            Matrix Yb_new=F.creatMatrix(Nb,2);
            Matrix Yc_new=F.creatMatrix(Nc,2);

            Ya_new=Ya.copy();
            Yb_new=Yb.copy();
            Yc_new=Yc.copy();
            lastY=Y.copy();

            //initialize Ya Yb Yc

            Matrix temp_a_up=F.creatMatrix(Na,2);
            Matrix temp_a_down=F.creatMatrix(Na,2);
            temp_a_up=((Gab.times(Yb)).add(Gac.times(Yc))).add(Sa.times(Y0));
            temp_a_down=Ya.times(Yb.transpose()).times(Yb).add(Ya.times(Yc.transpose()).times(Yc)).add(Sa.times(Y));

            //calculate the numerator and denominator
            Ya_new=(Ya.cron(temp_a_up.divide(temp_a_down).sqrt())).copy();
            //calculate Ya


            Matrix temp_b_up=F.creatMatrix(Nb,2);
            Matrix temp_b_down=F.creatMatrix(Nb,2);
            temp_b_up=((Gab.transpose()).times(Ya)).add(Gbc.times(Yc)).add(Sb.times(Y0));
            temp_b_down=Yb.times(Ya.transpose()).times(Ya).add(Yb.times(Yc.transpose()).times(Yc)).add(Sb.times(Y));
            
            //calculate the numerator and denominator
            Yb_new=(Yb.cron(temp_b_up.divide(temp_b_down).sqrt())).copy();
            //calculate Yb



            Matrix temp_c_up=F.creatMatrix(Nc,2);
            Matrix temp_c_down=F.creatMatrix(Nc,2);
            temp_c_up=Gac.transpose().times(Ya).add(Gbc.transpose().times(Yb)).add(Sc.times(Y0));
            temp_c_down=Yc.times(Ya.transpose()).times(Ya).add(Yc.times(Yb.transpose()).times(Yb)).add(Sc.times(Y));

            //calculate the numerator and denominator
            Yc_new=(Yc.cron(temp_c_up.divide(temp_c_down).sqrt())).copy();
            //calculate Yc


            for(int i=0;i<Na;i++)
            {
            	Matrix temp=F.creatMatrix(1,2);
            	temp=Ya_new.getRow(i).orthonormalizeCol();
            	Ya_new.setRow(i, temp);
            }
            for(int i=0;i<Nb;i++)
            {
            	Matrix temp=F.creatMatrix(1,2);
            	temp=Yb_new.getRow(i).orthonormalizeCol();
            	Yb_new.setRow(i, temp);
            }
            for(int i=0;i<Nc;i++)
            {
            	Matrix temp=F.creatMatrix(1,2);
            	temp=Yc_new.getRow(i).orthonormalizeCol();
            	Yc_new.setRow(i, temp);
            }
            Ya=(Ya_new).copy();
            Yb=(Yb_new).copy();
            Yc=(Yc_new).copy();
            Y.setM(0,Na-1,0,1,Ya_new);
            Y.setM(Na,Na+Nb-1,0,1,Yb_new);
            Y.setM(Na+Nb,Na+Nb+Nc-1,0,1,Yc_new);
            
	}

    
    /*The method gives the converge condition */
	private boolean converge() throws DimensionNotAgreeException
	{
            double x=Y.subtract(lastY).norm(Matrix.FROBENIUS_NORM);
            double nuance=0.01;
            return x<nuance;
	}
    /*The method gets the converge condition */
    @Override
    public Graph getResult(){

            try {
                for(Vertex v:local_g.getVertices())
                    System.out.println(v.getLabel());
                int iter=0;
                do
                {
                    update();
                    iter++;
                }while(!converge());
                //do the cycle until converge
                
                /*
                System.out.println("&&&");
                //System.out.println("****");
                System.out.println(Y.toString());
                */
                //System.out.println("===");
                for(Vertex v:local_g.getVertices())
                    if(!v.isY0())
                    {
                        v.setLabel(Y.getRow(map.get(v)).transpose());
                        //System.out.println(map.get(v));
                    }/*
                System.out.println("##");
                for(Vertex v:local_g.getVertices())
                {
                System.out.println(v.getLabel().toString());
                }*/
                
            } catch (DimensionNotAgreeException | ColumnOutOfRangeException | RowOutOfRangeException ex) {
                Logger.getLogger(Multiplicative.class.getName()).log(Level.SEVERE, null, ex);
            }
            return local_g;
    }

    

}

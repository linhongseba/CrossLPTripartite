/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import static java.lang.Math.abs;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.IrreversibleException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw
 */
public class BlockCoordinateDescent implements LabelInference{
        Map<Vertex,Matrix> y0Backup=new HashMap<>();
	private Matrix Y0,Y,lastY;
	private int N;
	int[] node;
	Graph local_g;
	
	private void randomSet(int row) throws ColumnOutOfRangeException, RowOutOfRangeException
	{
        if(Math.random()<(1/3.0))
		{
			Y.set(row,0,0);
			Y.set(row,1,1);
			return;
		}
		if(Math.random()<(2/3.0))
		{
			Y.set(row,0,0.5);
			Y.set(row,1,0.5);
			return;
		}
		Y.set(row,0,1);
		Y.set(row,1,0);
		return;
	}
	
	
    public BlockCoordinateDescent(Graph _g) throws ColumnOutOfRangeException, RowOutOfRangeException, DimensionNotAgreeException {
    	
    	local_g=new Graph();
    	Collection<Vertex> points;
    	points=_g.getVertices();
    	Iterator<Vertex> it = points.iterator();
    	MatrixFactory F;
    	F=MatrixFactory.getInstance();

        for(Vertex v:_g.getVertices())
            if(v.isY0())try {
                y0Backup.put(v, v.getLabel().subMatrix(0, 1, 0, 0));
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        
    	N=0;
    	
	   	while (it.hasNext()) {
		 	Object element = it.next();
			N++;
	   	}

		Y0=F.creatMatrix(N,2);
		Y=F.creatMatrix(N,2);
		
    	N=0;
		for(Vertex u:_g.getVertices()) {

        	if(u.isY0())
        	{
        		Y0.setRow(N, u.getLabel().transpose());
        		Y.setRow(N, u.getLabel().transpose());
    		}
        	else
        	{
        		randomSet(N);
        	}
        	local_g.addVertex(u);
        		
    		N++;
        }//get Y0
		
		int cnt=0;
		for(Vertex u:local_g.getVertices()) {
			u.setLabel(Y.getRow(cnt).transpose());
			cnt++;
		}
		for(Vertex u:_g.getVertices()) {
			for(Vertex v:_g.getVertices()) {
				local_g.addEdge(u, v, u.getEdge(v));
			}
		}
		//System.out.println(Y0.toString());
    	
    }
    
	private void update() throws DimensionNotAgreeException,ColumnOutOfRangeException, RowOutOfRangeException, IrreversibleException 
	{
		MatrixFactory F;
    	F=MatrixFactory.getInstance();
    	Matrix I=F.creatMatrix(2,2);
    	I.set(0,0,1);I.set(1,1,1);
    	lastY=F.creatMatrix(N,2);
		lastY.clone(Y);

		Matrix A;
    	
        Matrix A0=F.creatMatrix(2,2);
    	Matrix A1=F.creatMatrix(2,2);
    	Matrix B0=F.creatMatrix(2,2);
    	Matrix B1=F.creatMatrix(2,2);
    	Matrix C0=F.creatMatrix(2,2);
    	Matrix C1=F.creatMatrix(2,2);
    	
        final double deltaData[][]={{1e-5,0},{0,1e-5}};
        Matrix delta=F.creatMatrix(deltaData);
        Matrix temp;
        
		A=F.creatMatrix(2,2);
		for(Vertex v:local_g.getVertices()) 
		if(v.getType()!=Vertex.typeA)
		{
			A=A.add(v.getLabel().times(v.getLabel().transpose()));
		}
                
                if(abs(A.determinant())<1e-9) A=A.add(delta);
                temp=A.add(I);
                if(abs(temp.determinant())<1e-9)temp=temp.add(delta);
                //System.out.println(A);	
		A0=A.inverse();
		A1=(temp).inverse();
		
		A=F.creatMatrix(2,2);
		for(Vertex v:local_g.getVertices()) 
		if(v.getType()!=Vertex.typeB)
			A=A.add(v.getLabel().times(v.getLabel().transpose()));
                
                if(abs(A.determinant())<1e-9)A=A.add(delta);
                temp=A.add(I);
                if(abs(temp.determinant())<1e-9)temp=temp.add(delta);
                //System.out.println(A);	
		B0=A.inverse();
		B1=(temp).inverse();
    	
                A=F.creatMatrix(2,2);
		for(Vertex v:local_g.getVertices()) 
		if(v.getType()!=Vertex.typeC)
			A=A.add(v.getLabel().times(v.getLabel().transpose()));
                
                if(abs(A.determinant())<1e-9)A=A.add(delta);
                temp=A.add(I);
                if(abs(temp.determinant())<1e-9)temp=temp.add(delta);
                //System.out.println(A);	
		C0=A.inverse();
		C1=(temp).inverse();
    		//System.out.println(A0);	
                //System.out.println(A1);	
                //System.out.println(B0);	
                //System.out.println(B1);	
                //System.out.println(C0);	
                //System.out.println(C1);	
                
		int cnt=0;
		for(Vertex u:local_g.getVertices()) 
		{
			Matrix tempY=F.creatMatrix(2,1);

			for(Vertex v:u.getNeighbors())
			{
				tempY=tempY.add(v.getLabel().timesNum(u.getEdge(v)));
			}
	    	Matrix B=F.creatMatrix(2,2);
			if(u.getType()==Vertex.typeA)
			{
				if(u.isY0())
				{
					B.clone(A1);
					u.setLabel(B.times(tempY.add(Y0.getRow(cnt).transpose().orthonormalize())));
				}
				else
				{
					B.clone(A0);
					u.setLabel(B.times(tempY).orthonormalize());
				}
			}
			else
			if(u.getType()==Vertex.typeB)
			{
				if(u.isY0())
				{
					B.clone(B1);
					u.setLabel(B.times(tempY.add(Y0.getRow(cnt).transpose().orthonormalize())));
				}
				else
				{
					B.clone(B0);
					u.setLabel(B.times(tempY).orthonormalize());
				}
			}
			else
			if(u.getType()==Vertex.typeC)
			{
				if(u.isY0())
				{
					B.clone(C1);
					u.setLabel(B.times(tempY.add(Y0.getRow(cnt).transpose().orthonormalize())));
				}
				else
				{
					B.clone(C0);
					u.setLabel(B.times(tempY).orthonormalize());
				}
			}
			Y.setRow(cnt,u.getLabel().orthonormalize().transpose());
			
			cnt++;
		}
		//System.out.println("******"+cnt);

	}

    
	private boolean converge() throws DimensionNotAgreeException
	{
		
        double x=Y.subtract(lastY).norm(Matrix.FROBENIUS_NORM)/local_g.getVertices().size();
	
        //System.out.println(Y.toString());
        //System.out.println(lastY.toString());
        System.out.println(x);

		double nuance=0.0001;
		return x<nuance;
	}
    @Override
    public Graph getResult(){
        int c=0;
        try {
            do
            {
                update();
                c++;
            }
            while(c<5 && !converge());
        } catch (DimensionNotAgreeException | ColumnOutOfRangeException | RowOutOfRangeException | IrreversibleException ex) {
            Logger.getLogger(BlockCoordinateDescent.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(Vertex v:local_g.getVertices())
            if(v.isY0())try {
                v.setLabel(y0Backup.get(v).subMatrix(0, 1, 0, 0));
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {
            Logger.getLogger(BlockCoordinateDescent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return local_g;
    }
}
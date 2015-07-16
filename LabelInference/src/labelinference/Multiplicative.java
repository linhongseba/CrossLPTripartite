package labelinference;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.DimensionNotAgreeException;
import labelinference.exceptions.RowOutOfRangeException;

public class Multiplicative {
	Matrix Y0;
	Matrix Y;
	Matrix Gab,Gbc,Gac;
	Matrix Sa,Sb,Sc;
	Matrix tempY;
	int na,nb,nc;
	//M is the matrix transfered from the tripartite graph. Yl is the ground matrix. 
	Multiplicative(Matrix M,Matrix Yl,int Na,int Nb,int Nc) throws DimensionNotAgreeException,ColumnOutOfRangeException, RowOutOfRangeException
	{
		Y0=new NaiveMatrix(Na+Nb+Nc,2);
		Y=new NaiveMatrix(Na+Nb+Nc,2);
		tempY=new NaiveMatrix(Na+Nb+Nc,2);
		Sa=new NaiveMatrix(Na,Na+Nb+Nc);
		na=Na;nb=Nb;nc=Nc;
		Gab=M.subMatrix(0,na-1,na,na+nb-1);
		Gbc=M.subMatrix(na,na+nb-1,na+nb,na+nb+nc-1);
		Gac=M.subMatrix(0,na-1,na+nb,na+nb+nc-1);
		for(int i=0;i<na;i++)
			for(int j=0;j<2;j++)
				if(Yl.get(i,j)>0)
				{
					Y0.set(i, j, 1);
					Sa.set(i, i, 1);
				}
		for(int i=0;i<nb;i++)
			for(int j=0;j<2;j++)
				if(Yl.get(i,j)>0)
				{
					Y0.set(na+i, j, 1);
					Sb.set(i, i, 1);
				}
		for(int i=0;i<nc;i++)
			for(int j=0;j<2;j++)
				if(Yl.get(i,j)>0)
				{
					Y0.set(na+nb+i, j, 1);
					Sc.set(i, i, 1);
				}
		Y.clone(Y0);
	}
	public void update() throws DimensionNotAgreeException,ColumnOutOfRangeException, RowOutOfRangeException 
	{
		Matrix Ya=Y.subMatrix(0,na-1,0,1);
		Matrix Yb=Y.subMatrix(na,na+nb-1,0,1);
		Matrix Yc=Y.subMatrix(na+nb,na+nb+nc-1,0,1);
		Matrix Ya_new=Y.subMatrix(0,na-1,0,1);
		Matrix Yb_new=Y.subMatrix(na,na+nb-1,0,1);
		Matrix Yc_new=Y.subMatrix(na+nb,na+nb+nc-1,0,1);
		tempY.clone(Y);
		//initialize Ya Yb Yc
		
		Matrix temp_a_up=new NaiveMatrix(na,2);
		Matrix temp_a_down=new NaiveMatrix(na,2);
		temp_a_up=Gab.times(Yb).add(Gac.times(Yc)).add(Sa.times(Y0));
		temp_a_down=Ya.times(Yb.transpose()).times(Yb).add(Ya.times(Yc.transpose()).times(Yc)).add(Sa.times(Y));
		//calculate the numerator and denominator
		Ya_new.clone(Ya.cron(temp_a_up.divide(temp_a_down).sqrt()));
		//calculate Ya
		
		Matrix temp_b_up=new NaiveMatrix(nb,2);
		Matrix temp_b_down=new NaiveMatrix(nb,2);
		temp_b_up=Gab.transpose().times(Ya).add(Gbc.times(Yc)).add(Sb.times(Y0));
		temp_b_down=Yb.times(Ya.transpose()).times(Ya).add(Yb.times(Yc.transpose()).times(Yc)).add(Sb.times(Y));
		//calculate the numerator and denominator
		Yb_new.clone(Yb.cron(temp_b_up.divide(temp_b_down).sqrt()));
		//calculate Yb
		
		Matrix temp_c_up=new NaiveMatrix(nc,2);
		Matrix temp_c_down=new NaiveMatrix(nc,2);
		temp_c_up=Gac.transpose().times(Ya).add(Gbc.transpose().times(Yb)).add(Sc.times(Y0));
		temp_c_down=Yc.times(Ya.transpose()).times(Ya).add(Yc.times(Yb.transpose()).times(Yb)).add(Sc.times(Y));
		//calculate the numerator and denominator
		Yc_new.clone(Yc.cron(temp_c_up.divide(temp_c_down).sqrt()));
		//calculate Yc
		
		Y.setM(0,na-1,0,1,Ya_new);
		Y.setM(na,na+nb-1,0,1,Yb_new);
		Y.setM(na+nb,na+nb+nc-1,0,1,Yc_new);
	}
	public boolean converge() throws DimensionNotAgreeException
	{
		double x=Y.subtract(tempY).norm("F");
		double nuance=0.0001;
		return x<nuance;
	}
}

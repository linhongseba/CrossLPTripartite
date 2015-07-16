package labelinference;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.RowOutOfRangeException;

public class Multiplicative {
	Matrix Y0;
	Matrix Y;
	Matrix Gab,Gbc,Gac;
	Matrix Sa,Sb,Sc;
	int na,nb,nc;
	//M is the matrix transfered from the tripartite graph. Yl is the ground matrix. 
	Multiplicative(Matrix M,Matrix Yl,int Na,int Nb,int Nc) throws ColumnOutOfRangeException, RowOutOfRangeException
	{
		Y0=new NaiveMatrix(Na+Nb+Nc,2);
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
		Y=Y0;
	}
	public void update() throws ColumnOutOfRangeException, RowOutOfRangeException 
	{
		Matrix Ya=Y.subMatrix(0,na-1,0,2);
		Matrix Yb=Y.subMatrix(na,na+nb-1,0,2);
		Matrix Yc=Y.subMatrix(na+nb,na+nb+nc-1,0,2);
		
	}
	public boolean converge()
	{
		return true;
	}
}

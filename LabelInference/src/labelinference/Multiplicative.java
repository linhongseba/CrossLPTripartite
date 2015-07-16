/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author Tangyiqi
 */
public class Multiplicative {
	Matrix Y0;
	Matrix Y;
	Matrix Gab,Gbc,Gac;
	Multiplicative(Matrix M,Graph Yl,int na,int nb,int nc) throws ColumnOutOfRangeException, RowOutOfRangeException
	{
		//Gab=M.subMatrix(0,na-1,na,na+nb-1);
		//Gbc=M.subMatrix(na,na+nb-1,na+nb,na+nb+nc-1);
		//Gac=M.subMatrix(0,na-1,na+nb,na+nb+nc-1);
		
		
	}
	public void update()
	{
	}
	public boolean converge()
	{
		return true;
	}
}

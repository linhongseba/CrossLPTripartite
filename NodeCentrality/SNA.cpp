#include "SNA.h"
//#include "stdafx.h"
//#include <stdio.h>
//#include <stdlib.h>
#include <time.h>
#include <map>


int main(int argc, char *argv[])
{
	if(argc<3)
	{
		printf("Usage: programname [graphfilename] [isUnDirected]\n");
		exit(0);
	}
	FILE *rfile=fopen(argv[1],"r");

	long int t_start =clock();
		
	SNA runtest;

	runtest.setDirected(argv[2],argv[1]);

	runtest.Readgraph(rfile);

	//compute the statistics 
	//runtest.Connectedcomponent();
	runtest.SP_statistics();
	
	//////////////////////////////////
	//runtest.Computemaxclique();   //
	//runtest.Cliqueness();         // 
	//////////////////////////////////
	
	//runtest.SSSP(2);
	//testing part
//	runtest.ComputeInDegree();
//	runtest.ComputeIntegration(runtest.diameter);
//	runtest.ComputeRadiality(runtest.diameter);

	if(runtest.isUnDirected)
	{
		runtest.ComputeIntegration(runtest.diameter);
		runtest.ComputeLocalClusteringCoefficient();
	}
	else
	{
		runtest.ComputeIntegration(runtest.diameter);
		runtest.ComputeRadiality(runtest.diameter);
		runtest.ComputeLocalClusteringCoefficient();
	}

		
	char *outfilename=(char*)malloc(sizeof(char)*200);
	strcpy(outfilename,argv[1]);
	//strcpy(outfilename,"F:\\KDD Graph\\und_data.txt");
	strcat(outfilename,".statistics.txt");
	FILE *wfile=fopen(outfilename,"w");
	if(wfile==NULL){
		printf("could not open the file to write\n");
		exit(0);
	}
	if (runtest.isUnDirected){
		fprintf(wfile,"nodeID\t Degree\t Stress\t Betweenness\t LCC\n");
	}
	else
	{
		fprintf(wfile,"nodeID\t Stress\t Betweenness\t \n");
	}

	for(int i=0;i<runtest.g.nodenum;i++){
		//start compute the statistics related to node i
		fprintf(wfile,"%d\t",i);
		
		if (runtest.isUnDirected)
		{		
			fprintf(wfile,"%d\t",runtest.g.edges[i][0]);
		}
		else
		{
			//fprintf(wfile,"%d\t",runtest.directed_degreeNbrs[i]);
		}
				
		
		if (runtest.isUnDirected){
			fprintf(wfile,"%f\t",runtest.stress[i]);
			fprintf(wfile,"%f\t",runtest.CB[i]);
		}
		else{
			fprintf(wfile,"%f\t",runtest.stress[i]);
			fprintf(wfile,"%f\t",runtest.CB[i]);
		}		
		
		//**********Dec 23*******************
		if(runtest.maxdist[i]>0)		{
			fprintf(wfile,"%f\t",1/runtest.maxdist[i]);
		}
		else//PROBLEM:: default number of gce ? 0 or 1
		{
			fprintf(wfile,"%f\t",0.0f);
		}		
		
		//fprintf(wfile,"%d\n",runtest.cliquenums[i]);
		fprintf(wfile,"%f\t",runtest.closeness[i]);
		//fprintf(wfile,"%f\t",runtest.Integration[i]);
		//fprintf(wfile,"%f\t",runtest.Radiality[i]);

		if (runtest.isUnDirected){
			fprintf(wfile,"%f\t",runtest.Integration[i]);
			fprintf(wfile,"%f\n",runtest.LCC[i]);
		}
		else{
			fprintf(wfile,"%f\t",runtest.Integration[i]);
			fprintf(wfile,"%f\t",runtest.Radiality[i]);
			fprintf(wfile,"\n");
		}		
	}
	long int t_finish =clock();
	printf("Time comsuming is %f\n",(double(t_finish-t_start)/CLOCKS_PER_SEC));
}
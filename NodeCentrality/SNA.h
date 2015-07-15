#ifndef SNA_h_
#define SNA_h_
//#include<stdlib.h>
//#include<stdio.h>
#include<string>
#include<memory.h>
#include<stack>
#include<queue>
#include<math.h>
#include<map>
#include"labelpair.h"
#include "fheap.h"
#include <vector>
#include <iostream>
 

//#include "stdafx.h"
/*
@author: linhong
@last update time: 26/May/2010
@usage: provide a set of social network evaluation metric for real-life 
networks
*/
using namespace std;
typedef labelpair<int,int> Nodeweight;

//define a simple graph structure using array
//this is faster than using link-lists
struct Graph{
	int **edges;
	int nodenum;
	int maxdeg;
};

//define the social network analysis class
class SNA{
public:
	Graph g;
	int *dist;  //dist[j] store the shortest path from source to node j in SSSP
	float *delta; //store the dependence score in betweeness computation
	int *sigma;    //store the sigma value in betweeness computation
	int **P;      //store the preancestor in SSSP
	int *pre;     //store the preancestor in SSSP
	float *CB;    //store the value of betweeness centrality
	int *ccid;    //store the connected component id for each node
	int *ccsize;  //store the size of each connected component
	int ccnum;    //number of connected component
	int *isvisit; //a marker to indentify whether a node is visited or not
	float *maxdist; //maxdist[i] keeps the maximum distance from i to all the other nodes
	int diameter; //the diameter of graph G
	int radius;   //the radius of graph G
	int *cliquenums;
	float GE;

	// Author: Kun Qian
	// Purpose: testing
	int *directed_degreeNbrs;
	float *closeness;    // store the closeness values of each vertice
	//int *stress;     // store the stress values of each vertice
	int NbrOfSSSP;
	float *stressSigma;  //store the value of sigma in stress computation centrality
	float *stress;       //store the value of betweeness centrality
	float *dist2vertex;  //store the value of dist(s,v) in Integration compuatation
	float *Integration;  //store the value of Integration(v)
	float *Radiality;    //store the value of Radiality(v)
	float *vertex2dist;  //store the value of dist(v,s) in Radiatlity computation
	float *LCC;          //store the value of Local Clustering Coefficient
//	int** neighbors;     //store the neighbors of directed graph
	const char* filename;
	//fheap
	bool *isVisited ;     //store the visit information in fibonacci heap

public:
	SNA();
	~SNA();
	void Readgraph(FILE *rfile);  //read the graph file in disk to memory
	void SP_statistics();  
	////////////////////////////////////////////////////////////////
	//compute the statistics that are related to shortest path:
	//i.e.: the betweenness of each node for graph G
	//the closeness of each node for graph G
	//the radius and diameter of graph G
	//the globleefficience of graph G
	//the cliqueshness of node v for graph G: the probability of v in a clique
	/////////////////////////////////////////////////////////////////////////////
	void SSSP(int v);             //compute the single source shortest path when taking v as source
	float Clusteringcoeffience(int v); //compute the clusteringcoeffience of node v
	int DFS(int v);           //perform the dfs traversal starting from v
	float Cliqueshness(int v); //compute the probability of v in a clique
	void Computemaxclique();   //enumerating all the cliques in the graph
	void Cliqueness();//compute the frequence of each node v that appears in maximum cliques
	int computechoosenk(int n, int k);
	float PearsonCorre(int *&a, int *&b, int length);
	float PearsonCorre(float *&a, float *&b, int length);
	void Connectedcomponent();

	////Author:Kun Qian
	////Purpose: testing
	void ComputeInDegree();
	int Directed_GraphDegree(int vid);
	bool isUnDirected;
	void setDirected(string s,const char *graphname);
	void ComputeIntegration(int diameter);
	void ComputeRadiality(int diameter);
	void ComputeLocalClusteringCoefficient();
};


void SNA::ComputeLocalClusteringCoefficient()
{
	this->LCC=(float*)malloc(sizeof(float)*this->g.nodenum);
	memset(LCC,-1,sizeof(float)*g.nodenum);

	if(isUnDirected)
	{
		//if is directed graph
		for(int i = 0 ; i < g.nodenum; i++)
		{
			map <int, char> nodelist;

			// store the number of Triangles for computation in Local Clustering Coefficient of vertex v
			int NbrOfTriangles = 0;

			// store the degree of vertex
			float Degree= g.edges[i][0];
			if(Degree>1)
			{
				for(int j = 1 ; j<= Degree; j++)
				{
					nodelist[g.edges[i][j]]='1';
				}
			
				//int maxNodeID= g.edges[i][DegreeOfVertex]
				for(int k=1; k<=Degree; k++)
				{
					for(int f=1; f<=g.edges[g.edges[i][k]][0];f++)
					{
						if(nodelist.find(g.edges[g.edges[i][k]][f])!=nodelist.end())
						{
							NbrOfTriangles++;
						}
					}
				}
				LCC[i]= NbrOfTriangles/(Degree*(Degree-1));
			}
			else
			{
				LCC[i]=0;
			}
		}
	}
}
void SNA::ComputeRadiality(int diameter)
{
	this->Radiality=(float*)malloc(sizeof(float)*this->g.nodenum);
	memset(Radiality,0,sizeof(float)*g.nodenum);

	int num = g.nodenum-1;
	for (int i=0; i<g.nodenum; i++)
	{
		Radiality[i] = (num*(float)diameter + num - vertex2dist[i])/num;
	}
}
void SNA::ComputeIntegration(int diameter)
{
	this->Integration=(float*)malloc(sizeof(float)*this->g.nodenum);
	memset(Integration,0,sizeof(float)*g.nodenum);

	int num = g.nodenum-1;
	for (int i = 0 ; i < g.nodenum; i++)
	{
		Integration[i]= (num*(float)diameter + num - dist2vertex[i])/num;
	}

}

void SNA::setDirected(string s, const char *graphname)
{
	filename = (char*)malloc(sizeof(char)*200);
	filename = graphname;
	if(s=="directed")
	{
		isUnDirected = false;
	}
	else
	{
		isUnDirected = true;
	}
}

SNA::SNA(){
	directed_degreeNbrs=NULL;
	dist=NULL;  
	delta=NULL;
	sigma=NULL;
	P=NULL;
	pre=NULL;  
	CB=NULL;   
	ccid=NULL;    
	ccsize=NULL;  
	isvisit=NULL; 
	isVisited=NULL;
	maxdist=NULL;
	stressSigma=NULL;  
	stress=NULL; 
	LCC=NULL;
	Integration=NULL;
	Radiality=NULL;
	dist2vertex=NULL;
	closeness=NULL;
	filename = NULL;
}
SNA::~SNA(){
	free(pre);
	free(CB);
	free(isvisit);
	free(ccsize);
	free(ccid);
	for(int i=0;i<g.nodenum;i++)
		free(g.edges[i]);
	free(g.edges);
	g.nodenum=0;
	g.maxdeg=0;
	free(directed_degreeNbrs);
	free(stress);
	free(maxdist);
	free(closeness);
	free(dist2vertex);
	//if Radiality, integration and LCC is computed, not safe way
	free(Integration);
	free(Radiality);
	free(LCC);
	free(isVisited);

}
void SNA::Readgraph(FILE *rfile){
	if(rfile==NULL){
		printf("could not open the graph file in disk to read\n");
		exit(0);
	}
	// Load the number of nodes in a graph
	fscanf(rfile,"%d",&this->g.nodenum);
	if(g.nodenum<=0){
		printf("the graph file format is not correct\n");
		exit(0);
	}
	// malloc memories to edges
	// edges array is allocated
	this->g.edges=(int**)malloc(sizeof(int*)*g.nodenum);
//	this->neighbors=(int*)malloc(sizeof(int)*g.nodenum);

	this->g.maxdeg=0;
	// dv--degree of vertex
	// vid--vertex ID
	int dv,vid;
	int i,j;
	for(j=0;j<g.nodenum;j++){
		fscanf(rfile,"%d,%d",&vid,&dv);
		if(this->g.maxdeg<dv)
		{
			this->g.maxdeg=dv;
		}
		this->g.edges[vid]=(int*)malloc(sizeof(int)*(dv+1));

		this->g.edges[vid][0]=dv;
		for(i=1;i<=dv;i++){
			fscanf(rfile,":%d",&this->g.edges[vid][i]);
			
		}
	}
}
void SNA::SP_statistics(){
	this->dist=(int*)malloc(sizeof(int)*this->g.nodenum);
	this->delta=(float*)malloc(sizeof(float)*this->g.nodenum);
	this->P=(int**)malloc(sizeof(int*)*this->g.nodenum);
	this->CB=(float*)malloc(sizeof(float)*this->g.nodenum);
	this->sigma=(int*)malloc(sizeof(int)*this->g.nodenum);
	this->maxdist=(float*)malloc(sizeof(float)*this->g.nodenum);
	this->closeness=(float*)malloc(sizeof(float)*g.nodenum);

	this->stressSigma=(float*)malloc(sizeof(float)*this->g.nodenum);
	this->stress=(float*)malloc(sizeof(float)*this->g.nodenum);
	this->dist2vertex=(float*)malloc(sizeof(float)*this->g.nodenum);
	this->vertex2dist=(float*)malloc(sizeof(float)*this->g.nodenum);

	//fheap visited
	this->isVisited=(bool*)malloc(sizeof(bool)*this->g.nodenum);
	
	this->NbrOfSSSP = 0 ;
	//int scalefactor=2;
	int* scalefactor = (int*)malloc(sizeof(int)*this->g.nodenum);
	memset(scalefactor,2,sizeof(int)*g.nodenum);
	int number = g.nodenum;
	float count_scale = 10 ;
	while(number/10 >1)
	{
		number = number/10;
		count_scale*=100;
	}

	//allocate memories to maxdistance list, each value is for a node
	memset(maxdist,0,sizeof(float)*g.nodenum);
	memset(closeness,0,sizeof(float)*g.nodenum);
	memset(dist2vertex,0,sizeof(float)*g.nodenum);
	memset(vertex2dist,0,sizeof(float)*g.nodenum);
	memset(stress,0,sizeof(int)*g.nodenum);	
	
	int i,j;
	int pos;
	// create a statistics file for writing
	FILE *wfile=fopen("statistics.txt","w");
	if(wfile==NULL){
		printf("could not open the file to write\n");
		exit(0);
	}

	// write hearder into the statistics file
	fprintf(wfile,"node id\t closeness \t cliqueshness\t Clusteringcoefficience\n");
	
	// CB stands for betweenness centrality
	// P stands for the preancestor in SSSP
	// initial output
	for(i=0;i<g.nodenum;i++){
		//*********************************************************************************************
		stress[i]=0;
		CB[i]=0;
		//this->P[i]=(int*)malloc(sizeof(int)*(this->g.maxdeg*2+1));
		this->P[i]=(int*)malloc(sizeof(int)*(this->g.maxdeg*2+2));
		this->P[i][0]=g.maxdeg*2;
		this->P[i][1]=0;

	}
	float avgdist=0;
	stack<int> S;
	FHeap h(g.nodenum);

	for(i=0;i<g.nodenum;i++)
	{
		if(i%500==0)
			printf("%s now it is in %d's node\n",filename,i);
		fprintf(wfile,"%d\t",i);
		/////////////////////////////////////////////////
		/////perform single source shortest path (start)//
		////////////////////////////////////////////////////
		memset(sigma,0,sizeof(int)*g.nodenum);
		memset(dist,-1,sizeof(int)*g.nodenum);
		//for fheap
		//memset(isVisited,false,sizeof(bool)*g.nodenum);

		for(j=0;j<g.nodenum;j++){
			isVisited[j]=false;
			if(this->P[j][1]!=0)
				this->P[j][1]=0;
		}//P[w] is empty list for w in V
		this->sigma[i]=1;
		this->dist[i]=0;

		h.insert(i,dist[i]);
		isVisited[i]=true;

		//fheap
		while(h.nItems()!=0){

			//fheap
			int v=h.deleteMin();
			S.push(v);
			isVisited[v]=false;
			for(j=1;j<=g.edges[v][0];j++)
			{
				int w=g.edges[v][j];
				// if node with id==w never shows then add into queue
				if(dist[w]<0)
				{
					//Q.push(w);
					h.insert(w,dist[w]);
					this->dist[w]=this->dist[v]+1;
					P[w][1]++;
					//printf("the maxdeg is %d and P[%d][0]=%d\n",g.maxdeg,w,P[w][0]);
					pos=P[w][1]+1;
					P[w][pos]=v;
					this->sigma[w]=this->sigma[v];
				}
				else
				{
					if(this->dist[w]>(this->dist[v]+1))
					{
						//edge relaxing
						this->dist[w]=this->dist[v]+1;
						if(isVisited[w])
						{
							h.decreaseKey(w,dist[w]);
						}
						else
						{
							h.insert(w,dist[w]);
							isVisited[w]=true;
						}
						this->sigma[w]=this->sigma[v];
						P[w][1]=1;
						P[w][2]=v;
					}
					else
					{
						// if the distance from source to current is 1, closely contact to previous procedure
						if(this->dist[w]==(this->dist[v]+1))
						{
							this->sigma[w]=this->sigma[w]+this->sigma[v];
							//if(P[w][0]>=g.maxdeg*2){
							if(P[w][1]>=P[w][0])
							{
								//printf("out of index of array, please check\n");
								//printf("P[%d][0]=%d\n",w,P[w][0]);
								//exit(0);
								//scalefactor[w]++;
								int currentlength = P[w][0] ;
								P[w]=(int*)realloc(P[w],sizeof(int)*(currentlength*2+2));
								P[w][0]=currentlength*2;
							}
							P[w][1]++;
							//printf("the maxdeg is %d and P[%d][0]=%d\n",g.maxdeg,w,P[w][0]);
							pos=P[w][1]+1;
							P[w][pos]=v;
							if(isVisited[w])
							{
								h.decreaseKey(w,dist[w]);
							}
							else
							{
								h.insert(w,dist[w]);
								isVisited[w]=true;
							}
						}
					}
				}
			}
		}//end of while

		/////////////////////////////////////////////////
		/////perform single source shortest path (end)//
		////////////////////////////////////////////////////

		///////////////////////////////////////////////
		//compute closeness for node i (start)////////
		//////////////////////////////////////////////
		float distsum=0;
 		for(int k=0;k<g.nodenum;k++)
		{
			// initialize the stressSigma and delta here to save loop times
			stressSigma[k]=0;
			delta[k]=0.0f;
			if(dist[k]>=0)
			{
				distsum+=dist[k];
				dist2vertex[k]+=(float)dist[k];
				if(dist[k]>maxdist[i])
				{
					maxdist[i]=(float)dist[k];
				}
			}
			else
			{
				distsum+=g.nodenum;
				dist2vertex[k]+=(float)g.nodenum;
			}
		}
		//computing the closeness of vertex i
		//Author: Kun Qian
		//December 23, 2010.
		//printf("distsum is %f\n", distsum);
		
		
		//****************************CLOSENESS Computation***************************
		//when is directed, distsum may eqauls 0, what to handle this
		
		closeness[i]=1/distsum;
		closeness[i]=count_scale/distsum;
		vertex2dist[i]=distsum;

		//************************Radiality Computation**********************************


		/* comment out at Dec 17 because this part is for cliqueness, it is not implemented
		// in this phase
		int id=ccid[i];
		if(ccsize[id]==-1){
			int reachsize=this->DFS(i);
			ccsize[id]=reachsize;
		}
		
		fprintf(wfile,"%f\t",(float)distsum/ccsize[id]);
		*/

		///////////////////////////////////////////////
		//compute closeness for node i (end)////////
		//////////////////////////////////////////////
		
		/*///// Dec 18,2010
		
		fprintf(wfile,"%f\t",this->Cliqueshness(i));//compute the probablity of node i in a clique
		fprintf(wfile,"%f\n",this->Clusteringcoeffience(i));

		//////////////////////////////*/
		//compute the clustering coefficience of node i

		////////////////////////////////////////////////////////////
		///////update avgdist related to global efficience (start)
		///////undirected
		////////////////////////////////////////////////////////////
		//for(j=i+1;j<g.nodenum;j++){
		



		//******************************************************
		//comment out date: Dec 29,2010
		//****************************************************
		/*for(j=1;j<g.nodenum;j++){
			
			if(dist[j]<0)
				avgdist+=g.nodenum;//for those unreachable nodes, we set distance to nodenum 
			else{
				avgdist+=dist[j];
				// mark the positions where distance is larger than 1, 
				// it indicates the path has passed through more than 2 nodes
				// so a stress value exists
			}
		}
		if(g.nodenum-i>0)
			avgdist/=(g.nodenum-i);
		*/
		/*******************************************************/


		//////////////////////////////////////////////////////////
		///////update avgdist related to global efficience (end)
		////////////////////////////////////////////////////////////

		/////////////////////////////////////////////////////////////////////
		///////update maxdist related to radus and diameter of graph (start)
		////////////////////////////////////////////////////////////////////
		/*****************Dec 23 2010*****************************
		for(j=0;j<g.nodenum;j++)
		{
			if(dist[j]>maxdist[i]){
				maxdist[i]=dist[j];
			}
				
		}*/	
				/////////////////////////////////////////////////////////////////////
		///////update maxdist related to radus and diameter of graph (end)
		////////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////////
		///////update delta (dependence score) related to betweeness (start)
		//////////////////////////////////////////////////////////////////////
		//
		// comment out at Dec 23, 2010
		//for(j=0;j<g.nodenum;j++)
		//{
		//	stressSigma[j]=delta[j]=0;
		//}
			
		while(S.empty()==false)
		{
			int w=S.top();
			S.pop();
			for(j=2;(j-1)<=P[w][1];j++){
			//for(j=1;j<P[w][0];j++){
				int u=P[w][j];
				this->delta[u]=this->delta[u]+(float)((float)this->sigma[u]/(float)this->sigma[w])*(1+this->delta[w]);
				this->stressSigma[u]=this->stressSigma[u]+ this->sigma[u]*(1+this->stressSigma[w]/sigma[w]);
			}
			if(w!=i)
			{
				CB[w]=CB[w]+this->delta[w];
				stress[w]=stress[w]+this->stressSigma[w];
			}
		}
				//////////////////////////////////////////////////////////////////////
		///////update delta (dependence score) related to betweeness (end)
		//////////////////////////////////////////////////////////////////////
	}
	//printf("The global efficience of graph is %f\n",1/avgdist);
	this->diameter=0;
	this->radius=-1;
	for(i=0;i<g.nodenum;i++){
		//printf("max distance of vertex %d is %f\n", i, maxdist[i]);
		if(maxdist[i]>diameter)
			diameter=maxdist[i];
	//	if(maxdist[i]<radius)
	//		radius=maxdist[i];
	}
	//printf("The radius of graph is %d\n",radius);
	printf("The diameter of graph is %d\n",diameter);
	//printf("The Numbers of Shortest Path of graph is %d\n",NbrOfSSSP);
	
	//testing for stress

	free(delta);
	free(sigma);
	//***********************
	free(stressSigma);
	for(i=0;i<g.nodenum;i++){
		if(P[i]!=NULL)
			free(P[i]);
	}
	
	while (h.nItems()!=0) 
	{
		h.deleteMin();
    }
	//h.~FHeap();
	
	if(P!=NULL)
		free(P);
	fclose(wfile);
	free(dist);
	free(scalefactor);

}
float SNA::Clusteringcoeffience(int v){
	float ccv=0;
	for(int i=1;i<=g.edges[v][0];i++){
		int w=g.edges[v][i];
		ccv+=g.edges[w][0];
	}
	if(g.edges[v][0]==1||g.edges[v][0]==0)
		return 0;
	else
		return ccv/g.edges[v][0]/(g.edges[v][0]-1);
}


//assume that SSSP(v) has been performed before cliqueshness computation
float SNA::Cliqueshness(int v){
	//this->SSSP(v);
	int maximumdist=0;
	for(int i=0;i<g.nodenum;i++){
		if(dist[i]>maximumdist)
			maximumdist=dist[i];
	}
	int *gamma=(int*)malloc(sizeof(int)*(maximumdist+1));
	memset(gamma,0,sizeof(int)*(maximumdist+1));
	for(int i=0;i<g.nodenum;i++){
		int pos=dist[i];
		if(pos>=0)
			gamma[pos]++;
	}
	//compare gamma with the sequence distribution of ideal clique and non-ideal clique
	//the sequence distribution of ideal clique is [1,k,0,0,0...]
	//and the sequence distribution k-regular graph is bionormal distribuion
	int *gammaideal=(int*)malloc(sizeof(int)*(maximumdist+1));
	memset(gammaideal,0,sizeof(int)*(maximumdist+1));
	gammaideal[0]=1;
	if(maximumdist>=1){
		gammaideal[1]=g.edges[v][0];
	}
	int *gammaregular=(int*)malloc(sizeof(int)*(maximumdist+1));
	memset(gammaregular,0,sizeof(int)*(maximumdist+1));
	gammaregular[0]=1;
	for(int i=1;i<=maximumdist;i++){
		//C(n,k)*p^k*(1-p)^(n-k)
		gammaregular[i]=this->computechoosenk(maximumdist,i)*pow(0.45,i)*pow(0.55,maximumdist-i);
	}
	//compute the similarity between gamma and gammaideal; gamma and gammaregular
	float temp=this->PearsonCorre(gamma,gammaregular,maximumdist+1);
	float tempn=this->PearsonCorre(gamma,gammaideal,maximumdist+1);
	free(gamma);
	free(gammaregular);
	free(gammaideal);
	if(temp!=0)
		return tempn/temp;
	else
		return tempn;
}
//assume that SSSP(v) has been performed before radiality computation
//assume that diameter of graph has been computed also

/*float SNA::Radiality(int v){
	float radia=0;
	for(int i=0;i<g.nodenum;i++){
		radia+=((float)(this->diameter+1+this->dist[i])/(g.nodenum-1));
	}
	return radia;
}*/
int SNA::DFS(int v){
	stack<int> S;
	S.push(v);
	isvisit[v]=0;
	bool flag=false;
	int count=0;
	while(S.empty()==false){
		int u=S.top();
		if(isvisit[u]==1)
			S.pop();
		flag=true;
		for(int i=1;i<=this->g.edges[u][0];i++){
			int w=this->g.edges[u][i];
			if(isvisit[w]==-1){
				S.push(w);
				isvisit[w]=0;
				flag=false;
			}
		}
		if(flag==true){
			count++;
			isvisit[u]=1;
			ccid[u]=ccnum;
		}
	}
	return count;
}
void SNA::SSSP(int v){
	if(pre==NULL){
		this->pre=(int*)malloc(sizeof(int)*g.nodenum);
	}
	queue<int> visit_queue;
	visit_queue.push(v);

	// set all the nodes is not visited
	for(int i=0;i<g.nodenum;i++)
		isvisit[i]=-1;

	while(visit_queue.empty()==false){
		// start from the source( at begining u = v = source)
		int u=visit_queue.front();
		visit_queue.pop();
		isvisit[u]=1;
		priority_queue <Nodeweight,vector < Nodeweight >, std::less< Nodeweight > > Q;
		for(int j=1;j<=g.edges[u][0];j++){
			int w=g.edges[u][j];
			if(isvisit[w]!=1&&pre[w]!=u&&pre[u]!=w){
				if(this->dist[u]+1<this->dist[w]){
					this->dist[w]=dist[u]+1;
					pre[w]=u;
					Nodeweight np(w,dist[w]);
					Q.push(np);
				}
			}
		}
		while(Q.empty()==false){
			Nodeweight np=Q.top();
			Q.pop();
			visit_queue.push(np.getkey());
		}
	}
}
int SNA::computechoosenk(int n, int k){
	int fn=1;
	for(int i=n;i>=n-k;i--)
		fn*=i;
	int fk=1;
	for(int j=k;j>=1;j--)
		fk*=j;
	return fn/fk;
}
float SNA::PearsonCorre(int *&a, int *&b, int length){
	float aavg=0;
	float bavg=0;
	for(int i=0;i<length;i++){
		aavg+=a[i];
		bavg+=b[i];
	}
	aavg/=length;
	bavg/=length;
	float nominator=0;
	float asd=0;
	float bsd=0;
	for(int i=0;i<length;i++){
		nominator+=(a[i]-aavg)*(b[i]-bavg);
		asd+=(a[i]-aavg)*(a[i]-aavg);
		bsd+=(b[i]-bavg)*(b[i]-bavg);
	}
	if(asd==0||bsd==0)
		return 0;
	else
		return nominator/sqrt(asd)/sqrt(bsd);
}
float SNA::PearsonCorre(float *&a, float *&b, int length){
	float aavg=0;
	float bavg=0;
	for(int i=0;i<length;i++){
		aavg+=a[i];
		bavg+=b[i];
	}
	aavg/=length;
	bavg/=length;
	float nominator=0;
	float asd=0;
	float bsd=0;
	for(int i=0;i<length;i++){
		nominator+=(a[i]-aavg)*(b[i]-bavg);
		asd+=(a[i]-aavg)*(a[i]-aavg);
		bsd+=(b[i]-bavg)*(b[i]-bavg);
	}
	if(asd==0||bsd==0)
		return 0;
	else
		return nominator/sqrt(asd)/sqrt(bsd);
}
void SNA::Computemaxclique(){
	//output graph to a file with a specific format
	//call mace program by system command
	FILE *outfile=fopen("temp.txt","w");
	if(outfile==NULL){
		printf("could not open the temp file to write\n");
		exit(0);
	}
	int count=0;
	for(int i=0;i<g.nodenum;i++){
		count=0;
		for(int j=1;j<=g.edges[i][0];j++){
			if(g.edges[i][j]>i){
				if(count==0)
					fprintf(outfile,"%d",g.edges[i][j]);
				else
					fprintf(outfile,",%d",g.edges[i][j]);
				count++;
			}
		}
		fprintf(outfile,"\n");
	}
	fclose(outfile);
	system("mace M temp.txt maceout.txt");
	system("del temp.txt");
}

void SNA::ComputeInDegree()
{

	this->directed_degreeNbrs=(int*)malloc(sizeof(int)*g.nodenum);
	memset(directed_degreeNbrs,0,sizeof(int)*g.nodenum);
	for (int i=0; i<g.nodenum;i++)
	{
		if(i%5000==0)
		{
			printf("Processing vertext: %d\n",i);
		}
		directed_degreeNbrs[i]=Directed_GraphDegree(i);
	}
}

// Compute the in-degree of directed graph of Vertex vid
int SNA::Directed_GraphDegree(int vid)
{
	int i = 0 ;
	int degree=0;
	while(i<g.nodenum)
	{
		if(g.edges[i][0]==0)
		{
			i++;
		}
		else{
			for(int j = 1 ; /*j<vid+2 &&*/ j<= g.edges[i][0]; j++){
				if(g.edges[i][j]==vid)
				{
					degree++;
				}

			}
			i++;
		}
	}
	return degree;
}


void SNA::Cliqueness(){
	//read the output of max clique enumeration from file maceout.txt
	//count the frequence of v in each line
	this->cliquenums=(int*)malloc(sizeof(int)*g.nodenum);
	memset(cliquenums,0,sizeof(int)*g.nodenum);
	char *pch;
	FILE *readfile=fopen("maceout.txt","r");
	if(readfile==NULL){
		printf("could not open the maceout.txt file to read\n");
		exit(0);
	}
	char *buffer=(char*)malloc(sizeof(char)*5000);
	while(!feof(readfile)){
		fscanf(readfile,"%[^\n]\n",buffer);
		pch=strtok(buffer," ");
		while(pch!=NULL){
			int w=atoi(pch);
			cliquenums[w]++;
			pch=strtok(NULL," ");
		}
	}
	fclose(readfile);
}
void SNA::Connectedcomponent(){
	this->isvisit=(int*)malloc(sizeof(int)*g.nodenum);
	memset(isvisit,-1,sizeof(int)*g.nodenum);
	this->ccsize=(int*)malloc(sizeof(int)*g.nodenum);
	this->ccid=(int*)malloc(sizeof(int)*g.nodenum);
	this->ccnum=0;
	/////////////////////////////////////////////
	///////////                    //////////////
	for(int i=0;i<g.nodenum;i++){
		if(isvisit[i]==-1){
			int tempsize=this->DFS(i);
			this->ccsize[ccnum]=tempsize;
			this->ccnum++;
		}
	}
	memset(isvisit,-1,sizeof(int)*g.nodenum);
}
#endif
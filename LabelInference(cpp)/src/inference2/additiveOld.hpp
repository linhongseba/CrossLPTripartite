#ifndef AO_HPP
#define AO_HPP
#include"inference.hpp"
#include<cstring>
class additiveOld:public inference {
protected:
    double alphaY,alphaYNext,etac_L[3];
    matrix A[3];
	std::vector<std::array<matrix,3> > dBright;
    bool flagA;//whether A's cache (a.k.a. dBright) is updated, since increase will not call updateB forwardly
public:
    additiveOld(graph* g, const std::function<void(matrix&)>& labelInit):inference(g,labelInit),alphaY(1),flagA(false) {
    	dBright.resize(thrNum);
    	for(int t0:TYPES) {
            fore(t,thrNum)dBright[t][t0]=empty;
            A[t0]=empty;
        }
    }
	
	additiveOld(graph* g):additiveOld(g,inference::defaultLabelInit) {
    }

    void updateB() {
    	for(int t0:TYPES)fore(t,thrNum)-dBright[t][t0];
        multiRun(cand, [&](const vertex* u, int thrID){
            dBright[thrID][u->t]+=u->label**u->label;
        });
        for(int t0:TYPES)for(int t=1;t<thrNum;t++)
        	dBright[0][t0]+=dBright[t][t0];
        flagA=true;
    }

    void updateY() {
    	if(!flagA)additiveOld::updateB();
    	alphaYNext=(1+sqrt(4*alphaY*alphaY+1))/2;
    	double etac=(alphaY-1)/alphaYNext,eta[3];
    	for(int t0:TYPES){
    		-A[t0];
    		for(int t1:TYPES)if(t0!=t1)A[t0]+=B[t0][t1]*dBright[0][t1]**B[t0][t1];
    		eta[t0]=1.0/(A[t0]||matrix::NORMF);
		}
		
        multiRun(cand, [&](vertex* u, int thrID){
            matrix label(k,1);
            for(auto& e:u->edges) {
                vertex* v=e.neighbor;
                label+=((B[u->t][v->t]*v->label)*=e.weight);
            }
            (label-=(A[u->t]*u->tempLabel))*=eta[u->t];
            u->tempLabel+=label;
            (u->newLabel=u->tempLabel);
            for(auto x=u->newLabel.data.begin();x!=u->newLabel.data.end();x++)*x=std::max(*x,EPSILON);
            u->tempLabel=u->newLabel+((u->newLabel-u->label)*=etac);
        });
        alphaY=alphaYNext;
        flagA=false;
    }
};
#endif


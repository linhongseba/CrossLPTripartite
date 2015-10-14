#ifndef MO_HPP
#define MO_HPP
#include"inference.hpp"
#include<cstring>
class multiplicativeOld:public inference {
protected:
	matrix A[3];
	std::vector<std::array<matrix,3> > dBdown;
	bool flagA;//whether A's cache (a.k.a. dBdown) is updated, since increase will not call updateB forwardly
public:
    multiplicativeOld(graph* g,const std::function<void(matrix&)>& labelInit):inference(g,labelInit),flagA(false) {
    	dBdown.resize(thrNum);
    	for(int t0:TYPES) {
            fore(t,thrNum)dBdown[t][t0]=empty;
            A[t0]=empty;
        }
    }
    
    multiplicativeOld(graph* g):multiplicativeOld(g,inference::defaultLabelInit) {
    }

    void updateB() {
    	for(int t0:TYPES)fore(t,thrNum)-dBdown[t][t0];
        multiRun(cand, [&](const vertex* u, int thrID){
            dBdown[thrID][u->t]+=u->label**u->label;
        });
        for(int t0:TYPES)for(int t=1;t<thrNum;t++)
        	dBdown[0][t0]+=dBdown[t][t0];
        flagA=true;
    }

    void updateY() {
    	if(!flagA)multiplicativeOld::updateB();
    	for(int t0:TYPES){
    		-A[t0];
    		for(int t1:TYPES)if(t0!=t1)A[t0]+=B[t0][t1]*dBdown[0][t1]**B[t0][t1];
		}
        
        multiRun(cand, [&](vertex* u, int thrID){
            matrix label(k,1);
            for(const auto& e:u->edges) {
                const auto& v=e.neighbor;
                label+=((B[u->t][v->t]*v->label)*=e.weight);
            }
            if(u->isY0)(label+=u->truth)/=((A[u->t]*u->label)+=u->label);
            else label/=A[u->t]*u->label;
            u->newLabel=std::move(((!label)^=u->label)());
        });
        flagA=false;
    }
};
#endif


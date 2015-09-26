#ifndef AO_HPP
#define AO_HPP
#include"inference.hpp"
#include<cstring>
class additiveOld:public inference {
protected:
    double alphaY,alphaYNext,etac_L[3];
    std::vector<std::array<matrix,3> > A;
public:
    additiveOld(graph* g, const std::function<void(matrix&)>& labelInit):inference(g,labelInit),alphaY(1) {
    	A.resize(thrNum);
    	for(auto t0:TYPES)fore(t,thrNum)A[t][t0]=empty;
    }

	additiveOld(graph* g):additiveOld(g,inference::defaultLabelInit) {
    }

    void updateB() {
    }

    void updateY() {
    	for(int t0:TYPES)fore(t,thrNum)A[t][t0]=empty;
        multiRun(cand, [&](vertex* u, int thrID){
            for(const auto& e:u->edges) {
                const auto& v=e.neighbor;
                A[thrID][u->t]+=(B[u->t][v->t]*v->label**v->label**B[u->t][v->t])*=e.weight;
            }
        });
    	alphaYNext=(1+sqrt(4*alphaY*alphaY+1))/2;
    	double etac=(alphaYNext+alphaY-1)/alphaYNext;
    	for(int t0:TYPES) {
    		for(int t=1;t<thrNum;t++)A[0][t0]+=A[t][t0];
			etac_L[t0]=etac/(A[0][t0]||matrix::NORMF);
    	}
		
        multiRun(cand, [&](vertex* u, int thrID){
            matrix label(k,1);
            for(auto& e:u->edges) {
                vertex* v=e.neighbor;
                label+=(B[u->t][v->t]*v->label)*=e.weight;
            }
            label-=(A[0][u->t]*u->label);
            if(u->isY0)(label+=u->truth)-=u->label;
            u->newLabel=std::move(((label*=etac_L[u->t])+=u->label)());
        });
        alphaY=alphaYNext;
    }
};
#endif


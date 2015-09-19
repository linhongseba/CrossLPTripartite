#ifndef MO_HPP
#define MO_HPP
#include"inference.hpp"
#include<cstring>
class multiplicativeOld:public inference {
protected:
	matrix A[MAX_THR][3];
public:
    multiplicativeOld(graph* g):inference(g) {
    }

    void updateB() {
    }

    void updateY() {
        for(int t0:TYPES)fore(t,MAX_THR)A[t][t0]=empty;
        multiRun(cand, [&](const vertex* u, int thrID){
            for(const auto& e:u->edges) {
                const auto& v=e.neighbor;
                A[thrID][u->t]+=(B[u->t][v->t]*v->label**v->label**B[u->t][v->t]);
            }
        });
        for(int t0:TYPES)for(int t=1;t<MAX_THR;t++)A[0][t0]+=A[t][t0];
        
        multiRun(cand, [&](vertex* u, int thrID){
            matrix& label=u->newLabel;
            label=u->label;
            for(const auto& e:u->edges) {
                const auto& v=e.neighbor;
                label+=(B[u->t][v->t]*v->label)*=e.weight;
            }
            if(u->isY0)(label+=u->truth)/=((A[0][u->t]*u->label)+=u->label);
            else label/=A[0][u->t]*u->label;
            ((!label)^=u->label)();
        });   
    }
};
#endif


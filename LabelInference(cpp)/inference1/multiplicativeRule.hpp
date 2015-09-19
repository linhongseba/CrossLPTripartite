#ifndef MR_HPP
#define MR_HPP    
#include"multiplicativeOld.hpp"
class multiplicativeRule:public multiplicativeOld {
private:
    matrix dBup[MAX_THR][3][3],dBdown[MAX_THR][3][3];
public:
    multiplicativeRule(graph* g):multiplicativeOld(g) {
    }
    
    void updateB() {
        for(int t0:TYPES)for(int t1:TYPES)if(t0!=t1) {
            newB[t0][t1]=empty;
            fore(t,MAX_THR) {
                dBup[t][t0][t1]=empty;
                dBdown[t][t0][t1]=empty;
            }
        }
        multiRun(cand, [&](const vertex* u, int thrID){
            matrix uB[3];
            for(int t:TYPES)if(t!=u->t)uB[t]=*u->label*B[u->t][t];
            for(const auto& e:u->edges) {
                const vertex* v=e.neighbor;
                dBup[thrID][u->t][v->t]+=(u->label**v->label)*=e.weight;
                dBdown[thrID][u->t][v->t]+=u->label*((uB[v->t]*v->label**v->label)*=e.weight);
            }
        });
        for(int t0:TYPES)for(int t1:TYPES)if(t0!=t1) {
            for(int t=1;t<MAX_THR;t++) {
                dBup[0][t0][t1]+=dBup[t][t0][t1];
                dBdown[0][t0][t1]+=dBdown[t][t0][t1];
            }
            newB[t0][t1]=(B[t0][t1]^!(dBup[0][t0][t1]/=dBdown[0][t0][t1]));
        }     
    }
};
#endif

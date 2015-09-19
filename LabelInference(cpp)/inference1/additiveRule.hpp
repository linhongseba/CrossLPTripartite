#ifndef AR_HPP
#define AR_HPP    
#include"additiveOld.hpp"
class additiveRule:public additiveOld {
private:
    matrix dBleft[MAX_THR][3][3],dBright[MAX_THR][3][3],L[MAX_THR][3][3];
    double alphaB,alphaBNext;
public:
    additiveRule(graph* g):additiveOld(g),alphaB(0) {
    }
    
    void updateB() {
        alphaYNext=(1+sqrt(4*alphaY*alphaY+1))/2;
        double etac=(alphaYNext+alphaY-1)/alphaYNext;
        for(int t0:TYPES)for(int t1:TYPES)if(t0!=t1) {
            newB[t0][t1]=empty;
            fore(t,MAX_THR) {
                dBleft[t][t0][t1]=empty;
                dBright[t][t0][t1]=empty;
                L[t][t0][t1]=empty;
            }
        }
        multiRun(cand, [&](const vertex* u, int thrID){
            matrix uB[3];
            for(int t:TYPES)if(t!=u->t)uB[t]=*u->label*B[u->t][t];
            for(const auto& e:u->edges) {
                const vertex* v=e.neighbor;
                dBleft[thrID][u->t][v->t]+=(u->label**v->label)*=e.weight;
                dBright[thrID][u->t][v->t]+=(u->label*(uB[v->t]*v->label**v->label))*=e.weight;
                L[thrID][u->t][v->t]+=((v->label**u->label)*=(*v->label*u->label)[0][0])*=e.weight;
            }
        });
        for(int t0:TYPES)for(int t1:TYPES)if(t0!=t1) {
            for(int t=1;t<MAX_THR;t++) {
                dBleft[0][t0][t1]+=dBleft[t][t0][t1];
                dBright[0][t0][t1]+=dBright[t][t0][t1];
                L[0][t0][t1]+=L[t][t0][t1];
            }
            newB[t0][t1]=(B[t0][t1]+((dBleft[0][t0][t1]-=dBright[0][t0][t1])*=etac/(L[0][t0][t1]||matrix::NORMF)))();
        }
        alphaB=alphaBNext;
    }
};
#endif

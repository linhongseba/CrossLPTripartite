#ifndef AR_HPP
#define AR_HPP    
#include"additiveOld.hpp"
class additiveRule:public additiveOld {
private:
    matrix dBleft[MAX_THR][3][3],dBright[MAX_THR][3];
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
                dBright[t][t0]=empty;
            }
        }
        multiRun(cand, [&](const vertex* u, int thrID){
            matrix uB[3];
            for(const auto& e:u->edges) {
                const vertex* v=e.neighbor;
                dBleft[thrID][u->t][v->t]+=(u->label**v->label)*=e.weight;
            }
            dBright[thrID][u->t]+=u->label**u->label;
        });
        for(int t0:TYPES)for(int t=1;t<MAX_THR;t++)
        	dBright[0][t0]+=dBright[t][t0];
        for(int t0:TYPES)for(int t1:TYPES)if(t0!=t1) {
            for(int t=1;t<MAX_THR;t++)
                dBleft[0][t0][t1]+=dBleft[t][t0][t1];
            newB[t0][t1]=(B[t0][t1]+((dBleft[0][t0][t1]-=dBright[0][t0]*B[t0][t1]*dBright[0][t1])*=etac/((dBright[0][t1]*dBright[0][t0])||matrix::NORMF)))();
        }
        alphaB=alphaBNext;
    }
};
#endif

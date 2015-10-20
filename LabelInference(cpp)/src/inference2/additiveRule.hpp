#ifndef AR_HPP
#define AR_HPP    
#include"additiveOld.hpp"
class additiveRule:public additiveOld {
private:
    std::vector<std::array<std::array<matrix,3>,3> > dBleft;
    double alphaB,alphaBNext;
public:
    additiveRule(graph* g,const std::function<void(matrix&)>& labelInit):additiveOld(g,labelInit),alphaB(0) {
    	dBleft.resize(thrNum);
    	for(int t0:TYPES)for(int t1:TYPES)fore(t,thrNum)
			if(t0!=t1)dBleft[t][t0][t1]=empty;
    }
    
    additiveRule(graph* g):additiveRule(g,inference::defaultLabelInit) {
    }
    
    void updateB() {
        alphaYNext=(1+sqrt(4*alphaY*alphaY+1))/2;
        double etac=(alphaY-1)/alphaYNext;
        for(int t0:TYPES)fore(t,thrNum) {
            for(int t1:TYPES)if(t0!=t1) {
            	-dBleft[t][t0][t1];
            	-newB[t0][t1];
            }
            -dBright[t][t0];
        }
        multiRun(cand, [&](const vertex* u, int thrID){
            for(const auto& e:u->edges) {
                const vertex* v=e.neighbor;
                dBleft[thrID][u->t][v->t]+=(u->label**v->label)*=e.weight;
            }
            dBright[thrID][u->t]+=u->label**u->label;
        });
        for(int t0:TYPES)for(int t=1;t<thrNum;t++)
        	dBright[0][t0]+=dBright[t][t0];
        for(int t0:TYPES)for(int t1:TYPES)if(t0!=t1) {
            for(int t=1;t<thrNum;t++)
                dBleft[0][t0][t1]+=dBleft[t][t0][t1];
            tempB[t0][t1]+=((dBleft[0][t0][t1]-=dBright[0][t0]*tempB[t0][t1]*dBright[0][t1])*=1.0/((dBright[0][t1]*dBright[0][t0])||matrix::NORMF));
            (newB[t0][t1]=tempB[t0][t1])();
            tempB[t0][t1]=newB[t0][t1]+(newB[t0][t1]-B[t0][t1])*=etac;
        }
        alphaB=alphaBNext;
        flagA=true;
    }
};
#endif

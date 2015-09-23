#ifndef MR_HPP
#define MR_HPP    
#include"multiplicativeOld.hpp"
class multiplicativeRule:public multiplicativeOld {
private:
    std::vector<std::array<std::array<matrix,3>,3> > dBup;
public:
    multiplicativeRule(graph* g, const std::function<void(matrix&)>& labelInit):multiplicativeOld(g,labelInit) {
    	dBup.resize(thrNum);
    	for(int t0:TYPES)for(int t1:TYPES)fore(t,thrNum)
			if(t0!=t1)dBup[t][t0][t1]=empty;
    }
    
    multiplicativeRule(graph* g):multiplicativeRule(g,inference::defaultLabelInit) {
    }
    
    void updateB() {
        for(int t0:TYPES)for(int t1:TYPES)if(t0!=t1) {
            newB[t0][t1]=empty;
            fore(t,thrNum) {
                -dBup[t][t0][t1];
                -dBdown[t][t0];
            }
        }
        multiRun(cand, [&](const vertex* u, int thrID){
            for(const auto& e:u->edges) {
                const vertex* v=e.neighbor;
                dBup[thrID][u->t][v->t]+=(u->label**v->label)*=e.weight;
            }
            dBdown[thrID][u->t]+=u->label**u->label;
        });

        for(int t0:TYPES)for(int t=1;t<thrNum;t++)
        	dBdown[0][t0]+=dBdown[t][t0];
        for(int t0:TYPES)for(int t1:TYPES)if(t0!=t1) {
            for(int t=1;t<thrNum;t++)
                dBup[0][t0][t1]+=dBup[t][t0][t1];
            newB[t0][t1]=(B[t0][t1]^!(dBup[0][t0][t1]/=(dBdown[0][t0]*B[t0][t1]*dBdown[0][t1])));
        }
        flagA=true;
    }
};
#endif

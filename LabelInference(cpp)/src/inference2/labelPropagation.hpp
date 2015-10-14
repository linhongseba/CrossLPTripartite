#ifndef LP_HPP
#define LP_HPP
#include"inference.hpp"
#include<cstring>
class labelPropagation:public inference {
private:
    std::vector<vertex*> candS;
public:
    double alpha;
    labelPropagation(graph* g, const std::function<void(matrix&)>& labelInit):inference(g,labelInit),alpha(0.5) {
    }
    
    labelPropagation(graph* g):labelPropagation(g,inference::defaultLabelInit) {
    }
    
    labelPropagation(graph* g, double alpha):inference(g,inference::defaultLabelInit),alpha(alpha) {
    }

    void updateB() {
    }
    
    void updateY() {
        multiRun(g->verts,[&](vertex* u, int thrID){
            for(int t:TYPES)-u->cache[t];
            for(const auto e:u->edges) {
                const vertex* v=e.neighbor;
                u->cache[v->t]+=v->label*(e.weight/v->sumE);
			}
        });
        multiRun(cand,[&](vertex* u, int thrID){
            if(u->isY0) {
                u->newLabel=u->label;
                return;
            }
            matrix a(k, 1),b(k, 1);
            for(auto e:u->edges) {
                const vertex* v=e.neighbor;
                a+=v->label*e.weight;
                b+=((v->cache[u->t]-(u->label*(e.weight/u->sumE)))*=(e.weight/v->sumE));
            }
            u->newLabel=std::move((a()*=(1-alpha))+=(b()*=alpha));
        });
    }
};
#endif

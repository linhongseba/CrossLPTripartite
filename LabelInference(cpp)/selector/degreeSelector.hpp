#ifndef DEG_SLT_CPP
#define DEG_SLT_CPP
#include"../MARCO.hpp"
#include"../graph/graph.hpp"
#include<algorithm>
class degreeSelector:public selector {
public:
    degreeSelector(graph& g, int threshold) {
        vector<vertex*> H;
        for(auto& v:g.verts)if(v->isTruth)H.push_back(v);
        auto cpr=[](auto x,auto y){
            return x->deg()==y->deg()?x->id>y->id:x->deg()<y->deg();
        };
        std::make_heap(H.begin(),H.end(),cpr);
        for(int i=0;i<threshold && !H.empty();i++) {
            push_back(H.front());
            pop_heap(H.begin(),H.end(),cpr);
            H.pop_back();
        }
    }
};
#endif

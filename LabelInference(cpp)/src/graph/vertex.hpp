#ifndef VERTEX_HPP
#define VERTEX_HPP
#include"../MARCO.hpp"
#include<string>
#include<iostream>
#include"../matrix/matrix.hpp"
typedef int type;

class edge;
class vertex{    
public:
    double sumE;
    std::string id;
    matrix label;
    matrix truth;
    matrix newLabel;
    matrix cache[3];//for LP
    matrix tempLabel;//for AR
    type t;
    bool isY0;
    bool isTruth;
    std::vector<edge> edges;
	std::unordered_map<std::string,int> id2v;
	bool isCand;//for increase
	
    vertex(std::string vid, int k);
    vertex(const vertex& b);
    void addEdge(vertex* b, double w);
    void removeEdge(vertex* b, double w);
    int deg();
    int correct();
};

class edge {
public:
    double weight;
    vertex* neighbor;
    edge(vertex* n,double w):weight(w),neighbor(n){}
};

vertex::vertex(std::string vid, int k):	sumE(0),
										id(vid),
										label(k,1),
										truth(k,1),
										newLabel(k,1),
										tempLabel(k,1),
										t(vid[0]-'A'),
										isY0(false),
										isTruth(false),
										isCand(false){
	cache[0]=cache[1]=cache[2]=label;
}

vertex::vertex(const vertex& b):sumE(b.sumE),
								id(b.id),
								label(b.label),
								truth(b.truth),
								t(b.t),
								isY0(b.isY0),
								isTruth(b.isTruth),
								edges(b.edges){
}

inline void vertex::addEdge(vertex* b, double w) {
    edges.push_back(edge(b,w));
    id2v[b->id]=id2v.size()-1;
    sumE+=w;
}

inline void vertex::removeEdge(vertex* b, double w) {
	sumE-=w;
    int index=id2v[b->id];
    std::swap(edges[index],*edges.rbegin());
    id2v[edges[index].neighbor->id]=index;
    id2v.erase(b->id);
    edges.pop_back();
}

inline int vertex::deg() {
    return edges.size();
}

inline int vertex::correct() {
    int ret=0;
    double *l=label[0],*t=truth[0];
    double k=1.0/label.rowD;
    for(int i=0;i<label.rowD;i++) {
        if((*(l++)<k)^(*(t++)>k))ret++;
    }
    return ret;
}

inline std::ostream& operator<<(std::ostream& out, const vertex& b) {
    out<<b.id<<b.label;
    return out;
}
#endif


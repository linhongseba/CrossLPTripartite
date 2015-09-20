#ifndef GRAPH_HPP
#define GRAPH_HPP
#include"../MARCO.hpp"
#include<string>
#include<vector>
#include<unordered_map>
#include<fstream>
#include<sstream>
#include<algorithm>
#include"vertex.hpp"
class graph {
public:
    std::vector<vertex*> verts;
    std::unordered_map<std::string,int> id2v;
    int k;
    int NoE;
    
    graph(std::string path);
    void addVertex(vertex* v);
    void removeVertex(unsigned int index);
    void dispAccuracy();
    template<class Slt>
	void select(double ratio);
};

graph::graph(std::string path) {
	std::ifstream ifs (path, std::ifstream::binary);
	std::filebuf* pbuf = ifs.rdbuf();
	std::size_t size = pbuf->pubseekoff (0,ifs.end,ifs.in);
	pbuf->pubseekpos (0,ifs.in);
	char* buffer=new char[size];
	pbuf->sgetn (buffer,size);
	ifs.close();

    std::istringstream fin(buffer);
    fin>>k;
    NoE=0;
    std::string vid,nid;
    int nNei;
    double maxE=0,w;
    while(!fin.eof()) {
        fin>>vid>>nNei;
        NoE+=nNei;
        if(id2v.find(vid)==id2v.end())addVertex(new vertex(vid,k));
        vertex &v=*verts[id2v[vid]];
        fin>>v.truth;
        if((v.truth||matrix::NORM1)>0.5) {
        	v.isTruth=true;
        	v.truth();
        }
        for(int i=0;i<nNei;i++) {
            fin>>nid>>w;
            if(id2v.find(nid)==id2v.end())addVertex(new vertex(nid,k));
            v.addEdge(verts[id2v[nid]], w);
            if(w>maxE)maxE=w;
        }
    }
    for(auto& v:verts) { 
    	v->sumE/=maxE; 
        for(auto& e:v->edges)
            e.weight/=maxE;
	}
	delete[] buffer;
}

inline void graph::addVertex(vertex* v) {
    verts.push_back(v);
    id2v[v->id]=verts.size()-1;
}

inline void graph::removeVertex(unsigned int index) {
    std::swap(verts[index],*verts.rbegin());
    delete *verts.rbegin();
    id2v.erase(verts[index]->id);
    id2v[verts[index]->id]=index;
    verts.pop_back();
}

inline void graph::dispAccuracy() {
    int tot=0,crt=0;
    for(auto v:verts)if(v->isTruth && !v->isY0) {
        crt+=v->correct();
        tot+=k;
    }
    std::cout<<"Accuracy = "<<crt<<'/'<<tot<<std::endl;
}

template<class Slt>
inline void graph::select(double ratio) {
    std::vector<vertex*> vts;
    for(auto& v:verts)if(v->isTruth)vts.push_back(v);
    Slt slt(*this,vts.size()*ratio);
    for(auto& v:slt) {
        v->isY0=true;
        v->label=v->truth;
    }
}
#endif


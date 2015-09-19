#ifndef INFERENCE_HPP
#define INFERENCE_HPP
#include"../MARCO.hpp"
#include<string>
#include<vector>
#include<unordered_map>
#include<fstream>
#include<algorithm>
#include<functional>
#include<chrono>
#include<cmath>
#include<thread>
#include<mutex>
#include"../graph/graph.hpp"
class inference {
public:
    static const unsigned int DISP_ITER=1;
    static const unsigned int DISP_DELTA=2;
    static const unsigned int DISP_OBJ=4;
    static const unsigned int DISP_TIME=8;
    static const unsigned int DISP_LABEL=16;
    static const unsigned int DISP_SIZE=32;
    static const unsigned int DISP_B=64;
    static const unsigned int DISP_ALL=255;
    static const unsigned int DISP_NONE=0;
    
    static void defaultLabelInit(matrix& label);
    static void noneInit(matrix& label);
    
    graph* g;
    std::function<void(matrix&)> labelInit;    
    int k;
    matrix idm,empty;
    matrix B[3][3],newB[3][3];
    std::vector<vertex*> cand;

    inference(graph* g);
    inference(graph* g,const std::function<void(matrix&)>& labelInit);
    double objective();
    void infoDisplay(unsigned int disp, int iter, double delta, double time, double obj);
    template <class Fn, class... Args>
    void multiRun(std::vector<vertex*>& verts, Fn fn, Args&&... args);
    virtual void updateB()=0;
    virtual void updateY()=0;
    virtual void getResult(int maxIter, double nuance, unsigned int disp);
    virtual void increase(std::vector<vertex*>& deltaGraph, int maxIter, double nuance, double a, int disp);
    virtual void recompute(std::vector<vertex*>& deltaGraph, int maxIter, double nuance, int disp);
};

void inference::defaultLabelInit(matrix& label) {
    double v=1.0/label.rowD;
    for(auto& x:label.data)x=v;
}

void inference::noneInit(matrix& label) {
}

inference::inference(graph* g):inference(g,defaultLabelInit){
}

inference::inference(graph* g,const std::function<void(matrix&)>& labelInit):g(g),labelInit(labelInit),k(g->k),idm(k,k),empty(k,k) {
    fore(i,k)idm[i][i]=1;
    for(type t0:TYPES)for(type t1:TYPES)if(t0!=t1) {
        B[t0][t1]=idm;
        newB[t0][t1]=idm;
    }
}

double inference::objective() {
	double obj[MAX_THR]={0};
    multiRun(cand, [&](vertex* u, int thrID){
        for(auto& e:u->edges) {
            auto& v=e.neighbor;
            obj[thrID]+=pow((e.weight-((*u->label)*B[u->t][v->t]*=v->label).data.front()),2);
        }
        if(u->isY0)obj[thrID]+=pow(u->truth-u->label||matrix::NORMF,2);
    });
    for(int i=1;i<MAX_THR;i++)obj[0]+=obj[i];
    return *obj;
}

void inference::infoDisplay(unsigned int disp, int iter, double delta,double time,double obj) {
    using std::cout;
    using std::endl;
    if((disp&DISP_ITER)!=0)cout<<"Iter = "<<iter<<endl;
    if((disp&DISP_SIZE)!=0)cout<<"Size = "<<cand.size()<<endl;
    if((disp&DISP_DELTA)!=0)cout<<"Delta = "<<delta<<endl;
    if((disp&DISP_OBJ)!=0)cout<<"ObjValue = "<<obj<<endl;
    if((disp&DISP_LABEL)!=0)for(auto v:cand)cout<<*v<<endl;
    if((disp&DISP_B)!=0) {
        cout<<"B ="<<endl;
        for(type t0:TYPES)for(type t1:TYPES)if(t0!=t1)cout<<B[t0][t1]<<endl;
    }
    if((disp&DISP_TIME)!=0)cout<<"Processed in "<<time<<" s(update only)"<<endl;
}

template <class Fn, class... Args>
void inference::multiRun(std::vector<vertex*>& verts, Fn fn, Args&&... args) {
    std::thread thrs[MAX_THR];
    auto sth=[&](int thrID,vertex** begin, vertex** end, Args&&...args){
        for(auto pu=begin;pu<end;pu++)fn(*pu,thrID,args...);
    };
    int thrsize=verts.size()/MAX_THR;
    thrs[0]=std::thread(sth,0,(vertex**)verts.data()+(MAX_THR-1)*thrsize,(vertex**)verts.data()+verts.size(), args...);
    for(int i=1;i<MAX_THR;i++)thrs[i]=std::thread(sth,i,(vertex**)verts.data()+(i-1)*thrsize,(vertex**)verts.data()+i*thrsize, args...);
    for(auto& thr:thrs)thr.join();
}

void inference::getResult(int maxIter, double nuance, unsigned int disp) {
    using namespace std::chrono;
    cand=g->verts;
    for(auto v:cand)if(!v->isY0)labelInit(v->label);
    
    double timeUsed=0;
    double delta[MAX_THR];
    double oldObj=0;
    double obj;
    double deltaObj;
    int iter;
    steady_clock::time_point time;

    for(iter=0;iter<maxIter;iter++) {
    	//std::cin.get();
        time=steady_clock::now();
        
        for(int t0:TYPES)for(int t1:TYPES)if(t0!=t1)B[t0][t1]=newB[t0][t1];
        std::thread uB([&]{updateB();});
        std::thread uO([&]{
			obj = objective();
        	infoDisplay(disp&~DISP_TIME&~DISP_B&~DISP_LABEL, iter, delta[0], timeUsed, obj);
		});
        uB.join();
        std::thread uY([&]{updateY();});
        uO.join();
        uY.join();

        std::memset(delta,0,sizeof(delta));
        multiRun(cand, [&](vertex* u, int thrID){
            delta[thrID]+=((u->label)-(u->newLabel))||matrix::NORM1;
            u->label=std::move(u->newLabel);
        });
        for(int i=1;i<MAX_THR;i++)delta[0]+=delta[i];
        deltaObj = std::abs(oldObj-obj)/g->verts.size();
        oldObj = obj;

        timeUsed+=duration_cast<duration<double>>(steady_clock::now()-time).count();
        if (iter>0 && (*delta<=nuance || deltaObj<=nuance)){iter++;break;}
    }
    infoDisplay(disp, iter, delta[0], timeUsed, oldObj);
}

void inference::increase(std::vector<vertex*>& deltaGraph, int maxIter, double nuance, double a, int disp) {
}

void inference::recompute(std::vector<vertex*>& deltaGraph, int maxIter, double nuance, int disp) {
}
#endif


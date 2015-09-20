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
#include<array>
#include"../graph/graph.hpp"
class inference {
private:
	std::vector<std::array<matrix,3> > sum;
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
    int thrNum;

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
	SET_THR_NUM(g->NoE*g->k*g->k);
    fore(i,k)idm[i][i]=1;
    for(type t0:TYPES)for(type t1:TYPES)if(t0!=t1) {
        B[t0][t1]=idm;
        newB[t0][t1]=idm;
    }
    sum.resize(thrNum);
	fore(t,thrNum)for(auto t0:TYPES)sum[t][t0]=empty;
}

double inference::objective() {
	std::vector<double> obj(thrNum,0);
	fore(t,thrNum)for(auto t0:TYPES)-sum[t][t0];
    multiRun(cand, [&](const vertex* u, int thrID){
    	sum[thrID][u->t]+=u->label**u->label;
        for(const auto& e:u->edges) {
            const auto& v=e.neighbor;
            double YuT_Btutv_Yv=((*u->label)*B[u->t][v->t]*=v->label).data.front();
            obj[thrID]+=pow((e.weight-YuT_Btutv_Yv),2)-YuT_Btutv_Yv*YuT_Btutv_Yv;
        }
        if(u->isY0)obj[thrID]+=pow(u->truth-u->label||matrix::NORMF,2);
    });
    for(int t=1;t<thrNum;t++) {
    	obj[0]+=obj[t];
    	for(auto t0:TYPES)sum[0][t0]+=sum[t][t0];
    }
    for(auto t0:TYPES)for(auto t1:TYPES)if(t0!=t1)
    	fore(a,g->k)fore(b,g->k)fore(c,g->k)fore(d,g->k)
    		obj[0]+=B[t0][t1][a][b]*B[t0][t1][c][d]*sum[0][t0][a][c]*sum[0][t1][b][d];
    return obj[0];
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
    if((disp&DISP_TIME)!=0)cout<<time/iter<<"sec per iteration"<<endl;
}

template <class Fn, class... Args>
void inference::multiRun(std::vector<vertex*>& verts, Fn fn, Args&&... args) {
    std::vector<std::thread> thrs(thrNum);
    auto sth=[&](int thrID,vertex** begin, vertex** end, Args&&...args){
        for(auto pu=begin;pu<end;pu++)fn(*pu,thrID,args...);
    };
    int thrsize=verts.size()/thrNum;
    thrs[0]=std::thread(sth,0,(vertex**)verts.data()+(thrNum-1)*thrsize,(vertex**)verts.data()+verts.size(), args...);
    for(int i=1;i<thrNum;i++)thrs[i]=std::thread(sth,i,(vertex**)verts.data()+(i-1)*thrsize,(vertex**)verts.data()+i*thrsize, args...);
    for(auto& thr:thrs)thr.join();
}

void inference::getResult(int maxIter, double nuance, unsigned int disp) {
    using namespace std::chrono;
    cand=g->verts;
    for(auto v:cand)if(!v->isY0)labelInit(v->label);
    
    double timeUsed=0;
    std::vector<double> delta(thrNum);
    double oldObj=0;
    double obj;
    double deltaObj;
    int iter;
    steady_clock::time_point time;

    for(iter=0;iter<maxIter;iter++) {
    	//std::cin.get();
        time=steady_clock::now();

        std::thread uB([&]{updateB();});
        std::thread uO([&]{obj=objective();});
        uB.join();
        uO.join();
        for(int t0:TYPES)for(int t1:TYPES)if(t0!=t1)B[t0][t1]=newB[t0][t1];
        std::thread uY([&]{updateY();});
        std::thread uD([&]{
			infoDisplay(disp&~DISP_TIME&~DISP_B&~DISP_LABEL, iter, delta[0], timeUsed, obj);
		});
        uY.join();
        uD.join();

        fore(i,thrNum)delta[i]=0;
        multiRun(cand, [&](vertex* u, int thrID){
            delta[thrID]+=((u->label)-(u->newLabel))||matrix::NORM1;
            u->label=std::move(u->newLabel);
        });
        for(int i=1;i<thrNum;i++)delta[0]+=delta[i];
        deltaObj = std::abs(oldObj-obj)/g->verts.size();
        oldObj = obj;

        timeUsed+=duration_cast<duration<double>>(steady_clock::now()-time).count();
        if (iter>0 && (delta[0]<=nuance || deltaObj<=nuance)){iter++;break;}
    }
    infoDisplay(disp, iter, delta[0], timeUsed, oldObj);
}

void inference::increase(std::vector<vertex*>& deltaGraph, int maxIter, double nuance, double a, int disp) {
}

void inference::recompute(std::vector<vertex*>& deltaGraph, int maxIter, double nuance, int disp) {
}
#include"labelPropagation.hpp"
#include"multiplicativeRule.hpp"
#include"additiveRule.hpp"
#endif


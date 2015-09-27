#include"matrix/matrix.hpp"
#include"graph/graph.hpp"
#include"selector/degreeSelector.hpp"
#include"inference2/inference.hpp"
#include<chrono>
#include<cstdlib>
#include<unordered_map> 
using std::cout;
using std::endl;

void dispHelp() {	
	cout<<"Usage : blabla"<<endl;
	exit(1);
}

std::unordered_map<std::string,std::function<inference*(graph* g)> > algo({
	{"MO",[&](graph* g){return new MO(g);}},
	{"MR",[&](graph* g){return new MR(g);}},
	{"MG",[&](graph* g){return new MR(g,inference::noneInit);}},
	{"AO",[&](graph* g){return new AO(g);}},
	{"AR",[&](graph* g){return new AR(g);}},
	{"AG",[&](graph* g){return new AR(g,inference::noneInit);}},
	{"LP",[&](graph* g){return new LP(g);}}
});

inference* compute(std::string algoName, double labeledRatio, graph* g, graph* deltaGraph, double incrementalRatio, double nuance, int maxIter) {
	cout<<"----------Computing----------"<<endl;
	g->select<degreeSelector>(0.05);
	double cnt=0;
	for(vertex **u=g->verts.data(),**end=g->verts.data()+g->verts.size()-1;u<=end;u++)
		if((cnt+=incrementalRatio)>=1) {
			cnt--;
			deltaGraph->addVertex(*u);
			g->removeVertex(*u);
			end--;u--;
		}
	if(algoName[1]=='G')LP(g).getResult(maxIter/10,nuance,inference::DISP_NONE);
	for(auto u:deltaGraph->verts)for(auto e:u->edges) {
		auto v=e.neighbor;
		if(deltaGraph->id2v.find(v->id)==deltaGraph->id2v.end())
			v->removeEdge(u,e.weight);
	}
    inference* ifr=algo[algoName](g);
    ifr->getResult(maxIter,nuance,inference::DISP_ALL^inference::DISP_LABEL);
    cout<<"Old ";
	g->dispAccuracy();
    cout<<endl;
    return ifr;
}

void recompute(inference* ifr, graph* deltaGraph, double nuance, int maxIter) {
	cout<<"---------Recomputing---------"<<endl;
	ifr->recompute(deltaGraph, maxIter, nuance, inference::DISP_ALL^inference::DISP_LABEL);
	cout<<"Global ";
	ifr->g->dispAccuracy();
    cout<<"Incremental ";
	deltaGraph->dispAccuracy();
	cout<<endl;
}

void increase(inference* ifr, graph* deltaGraph, double confidenceLevel, double nuance, int maxIter) {
	cout<<"---------Increasing----------"<<endl;
	ifr->increase(deltaGraph, maxIter, nuance, confidenceLevel, inference::DISP_ALL^inference::DISP_LABEL);
	cout<<"Global ";
	ifr->g->dispAccuracy();
    cout<<"Incremental ";
	deltaGraph->dispAccuracy();
	cout<<endl;
}

int main(int argc, char* argv[]) {
    if(argc!=7 && argc !=8)dispHelp();
    std::string graphPath(argv[1]);
    double labeledRatio=std::strtod(argv[2],NULL);
    std::string algoName(argv[3]);
    double incrementalRatio=std::strtod(argv[4],NULL);
    double nuance=std::strtod(argv[5],NULL);
    int maxIter=std::atoi(argv[6]);
	double confidenceLevel=argc==7?-1:std::strtod(argv[7],NULL);
	
	using namespace std::chrono;
    double IOtime,TOTtime;
    steady_clock::time_point time=steady_clock::now();
    
    cout.sync_with_stdio(false);
    cout.setf(std::ios::fixed); 
    graph g(graphPath);
    graph deltaGraph(g.k);
    IOtime=duration_cast<duration<double>>(steady_clock::now()-time).count();
    
    inference* ifr=compute(algoName, labeledRatio, &g, &deltaGraph, incrementalRatio, nuance, maxIter);
	if(deltaGraph.verts.size()>0) {
		if(confidenceLevel<0)recompute(ifr, &deltaGraph, nuance, maxIter);
		else increase(ifr, &deltaGraph, confidenceLevel, nuance, maxIter);
    }
	
    TOTtime=duration_cast<duration<double>>(steady_clock::now()-time).count();
    cout<<"Processed in "<<TOTtime<<"sec ("<<IOtime<<"sec for IO)"<<endl;
    cout<<"Done."<<endl;
    return 0;
}

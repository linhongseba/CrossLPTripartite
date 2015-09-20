#include"matrix/matrix.hpp"
#include"graph/graph.hpp"
#include"selector/degreeSelector.hpp"
#include"inference2/inference.hpp"
#include<chrono>
int main() {
    using std::cout;
    using std::endl;
    using namespace std::chrono;
    double IOtime,TOTtime;
    steady_clock::time_point time=steady_clock::now();
    
    std::cout.sync_with_stdio(false);
    graph g("data/graph-30.txt");
    IOtime=duration_cast<duration<double>>(steady_clock::now()-time).count();
    
    g.select<degreeSelector>(0.05);
    inference* ifr=new MO(&g);
    ifr->getResult(100,-1,inference::DISP_ALL^inference::DISP_LABEL);
    g.dispAccuracy();
    TOTtime=duration_cast<duration<double>>(steady_clock::now()-time).count();
    
    std::cout<<"Processed in "<<TOTtime<<"sec ("<<IOtime<<"sec for IO)"<<std::endl;
    std::cout<<"In "<<ifr->thrNum<<" threads"<<std::endl;
    return 0;
}

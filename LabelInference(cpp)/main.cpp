#include"matrix/matrix.hpp"
#include"graph/graph.hpp"
#include"selector/degreeSelector.hpp"
#include"inference1/labelPropagation.hpp"
#include"inference1/multiplicativeRule.hpp"
#include"inference1/additiveRule.hpp"
#include<chrono>
int main() {
    using std::cout;
    using std::endl;
    std::cout.sync_with_stdio(false);
    graph g("data/graph-30.txt");
    g.select<degreeSelector>(0.05);
    inference* ifr=new MR(&g);
    ifr->getResult(100,-1,inference::DISP_ALL^inference::DISP_LABEL);
    g.dispAccuracy();
    return 0;
}

#ifndef MARCO_CPP
#define MARCO_CPP
#define fore(i,e) for(int i=0;i<e;i++)
#define EPSILON 1e-9
#define TYPE_A 0;
#define TYPE_B 1;
#define TYPE_C 2;
#define TYPES {0,1,2}
#define selector std::vector<vertex*>
#define MR multiplicativeRule
#define MO multiplicativeOld
#define AR additiveRule
#define AO additiveOld
#define LP labelPropagation
#define MAX_THR 500
#define MIN_THR 1
#define THR_CTRL 30
#define SET_THR_NUM(Ek) thrNum=std::max(MIN_THR,std::min(MAX_THR,(int)std::sqrt(Ek)/THR_CTRL))
#endif

#ifndef MATRIX_HPP
#define MATRIX_HPP
#include"../MARCO.hpp"
#include<cstring>
#include<vector>
#include<algorithm>
#include<cmath>
#include<iostream>
class matrix {  
public:
    std::vector<double> data;     
    int rowD,colD;
    static const int NORMF=-1;
    static const int NORM1=1;
    
    matrix();
    matrix(int row, int col);
    matrix(int row, int col, std::initializer_list<double> _data);
    matrix(const matrix&);
    matrix(const matrix&&);
    matrix& operator=(const matrix& b);
    matrix& operator=(matrix&& b);
    matrix operator+(const matrix& b)const;
    matrix& operator+=(const matrix& b);
    matrix operator+(matrix&& b)const;
    matrix operator-(const matrix& b)const;
    matrix& operator-=(const matrix& b);
    matrix operator-(matrix&& b)const;
    matrix operator^(const matrix& b)const;//.*
    matrix& operator^=(const matrix& b);//.*=
    matrix operator^(matrix&& b)const;//.*
    matrix operator*(double lambda)const;
    matrix& operator*=(double lambda);
    matrix operator*(const matrix& b)const;
    matrix& operator*=(const matrix& b);
    matrix operator/(const matrix& b)const;
    matrix operator/(matrix&& b)const;
    matrix& operator/=(const matrix& b);
    double* operator[](int row);
    const double* operator[](int row)const;
    double operator||(const int normName)const;//norm
    matrix operator*()const;//transpose
    matrix& operator!();//sqrt
    matrix& operator()();//normolize
    matrix& operator-();//clear
};

inline std::ostream& operator<<(std::ostream& out, const matrix& b) {
    out<<"{";
    fore(i,b.rowD) {
        out<<"{";
        fore(j,b.colD)out<<b[i][j]<<" ";
        out<<"}";
    }
    return out<<"}";
}

inline std::ostream& operator<<(std::ostream& out, matrix&& b) {
    return out<<b;
}

inline std::istream& operator>>(std::istream& in, matrix& b) {
    for(auto& x:b.data)in>>x;
    return in;
}

matrix::matrix():rowD(0),colD(0){
}

matrix::matrix(int row, int col):data(row*col,0),rowD(row),colD(col) {
}

matrix::matrix(int row, int col, std::initializer_list<double> _data):data(_data),rowD(row),colD(col) {
    data.resize(row*col);
}

matrix::matrix(const matrix& b):data(b.data),rowD(b.rowD),colD(b.colD) {
}

matrix::matrix(const matrix&& b):data(std::move(b.data)),rowD(b.rowD),colD(b.colD) {
}

inline matrix& matrix::operator=(const matrix& b) {
    rowD=b.rowD;
    colD=b.colD;
    data.resize(rowD*colD);
    std::memcpy(data.data(),b.data.data(),rowD*colD*sizeof(double));
    return *this;
}

inline matrix& matrix::operator=(matrix&& b) {
    rowD=b.rowD;
    colD=b.colD;
    data=std::move(b.data);
    return *this;
}

inline matrix matrix::operator+(const matrix& b)const {
    matrix ret(*this);
    return ret+=b;
}

inline matrix matrix::operator+(matrix&& b)const {
    auto y=b.data.begin();
    for(auto x=data.begin();x!=data.end();x++,y++)*y+=*x;
    return b;
}

inline matrix& matrix::operator+=(const matrix& b) {
    auto y=b.data.begin();
    for(auto x=data.begin();x!=data.end();x++,y++)*x+=*y;
    return *this;
}

inline matrix matrix::operator-(const matrix& b)const {
    matrix ret(*this);
    return ret-=b;
}

inline matrix matrix::operator-(matrix&& b)const {
    auto y=b.data.begin();
    for(auto x=data.begin();x!=data.end();x++,y++)*y=*x-*y;
    return b;
}

inline matrix& matrix::operator-=(const matrix& b) {
    auto y=b.data.begin();
    for(auto x=data.begin();x!=data.end();x++,y++)*x-=*y;
    return *this;
}

inline matrix matrix::operator*(double lambda)const {
    matrix ret(*this);
    for(auto x=ret.data.begin();x!=ret.data.end();x++)*x*=lambda;
    return ret;
}

inline matrix& matrix::operator*=(double lambda) {
    for(auto x=data.begin();x!=data.end();x++)*x*=lambda;
    return *this;
}

inline matrix matrix::operator^(const matrix& b)const {
    matrix ret(*this);
    return ret^=b;
}

inline matrix matrix::operator^(matrix&& b)const {
    auto y=b.data.begin();
    for(auto x=data.begin();x!=data.end();x++,y++)*y=*x**y;
    return b;
}

inline matrix& matrix::operator^=(const matrix& b) {
    auto y=b.data.begin();
    for(auto x=data.begin();x!=data.end();x++,y++)*x*=*y;
    return *this;
}

inline matrix matrix::operator*(const matrix& b)const {
    matrix ret(rowD,b.colD);
    double temp,*x;
    const double *y;
    fore(k,colD)fore(i,rowD) {
        temp=(*this)[i][k];
        x=ret[i];
        y=b[k];
        fore(j,b.colD)*x+++=temp**y++;
    }
    return ret;
}

inline matrix& matrix::operator*=(const matrix& b){
    return (*this)=(*this)*b;
}

inline matrix matrix::operator/(const matrix& b)const {
    matrix ret(*this);
    return ret/=b;
}

inline matrix matrix::operator/(matrix&& b)const {
    auto y=b.data.begin();
    for(auto x=data.begin();x!=data.end();x++,y++)*y=*x/std::max(*y,EPSILON);
    return b;
}

inline matrix& matrix::operator/=(const matrix& b) {
    auto y=b.data.begin();
    for(auto x=data.begin();x!=data.end();x++,y++)*x/=std::max(*y,EPSILON);
    return *this;
}

inline matrix& matrix::operator!() {
    for(auto x=data.begin();x!=data.end();x++)*x=sqrt(std::max(*x,EPSILON));
    return *this;
}

inline matrix matrix::operator*()const {
    matrix ret(colD,rowD);
    std::memcpy(ret.data.data(),data.data(),rowD*colD*sizeof(double));
    return ret;
}

inline matrix& matrix::operator()() {
    if(colD==1) {
        double minV=0, sum=0;
        for(auto x=data.begin();x!=data.end();x++) {
            if(minV>*x)minV=*x;
            sum+=*x;
        }
        if(minV<0) {
            minV*=2;
            sum-=minV*rowD;
            for(auto x=data.begin();x!=data.end();x++)*x=(*x-minV)/sum;
            return *this;
        }
        if(sum<EPSILON) {
        	double _k=1.0/rowD;
        	for(auto x=data.begin();x!=data.end();x++)*x=_k;
        	return *this;
        }
        for(auto x=data.begin();x!=data.end();x++)*x/=sum;
        return *this;
    }
    
    double minV=0,maxV=0;
    for(auto x=data.begin();x!=data.end();x++) {
        minV<=*x?0:(minV=*x);
        maxV>=*x?0:(maxV=*x);
    }
    if(minV<0) {
        minV*=2;
        maxV-=minV;
        for(auto x=data.begin();x!=data.end();x++)*x=(*x-minV)/maxV;
        return *this;
    }
    for(auto x=data.begin();x!=data.end();x++)*x/=maxV;
    return *this;
}

inline double* matrix::operator[](int row){
    return data.data()+row*colD;
}

inline const double* matrix::operator[](int row)const {
    return data.data()+row*colD;
}

inline double matrix::operator||(const int normName)const {
    double ret=0;
    if(normName==NORM1) {
        for(auto x=data.begin();x!=data.end();x++)ret+=*x>=0?*x:-*x;
        return ret;
    }
    if(normName==NORMF) {
        for(auto x=data.begin();x!=data.end();x++)ret+=*x**x;
        return sqrt(ret);
    }
    return 0;
}

inline matrix& matrix::operator-() {
	memset(data.data(),0,rowD*colD*sizeof(double));
	return *this;	
}
#endif


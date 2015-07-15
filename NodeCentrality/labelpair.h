#ifndef labelpair_h_
#define labelpair_h_
#include <iostream>
#include <stdlib.h>
#include <stdio.h>
#include <fstream>
#include <vector>
#include <string>
#include <assert.h>
/**
@author: Linhong (Seba)
@email: smileofice@gmail.com
@DATA: 12/03/08
@last update time: 19/02/2009
class labelpair:
implemented a pair template which is an extention to 
the pair template in standard library:
it support function:
isequal
valuecomparable
keycomparable
<
>=
the pair template is useful in sorting node match
and the ordered breadth-first or depth-first search
**/
template <class T1, class T2>
class labelpair{
private:
	T1 mykey;
	T2 value;
public:
	labelpair(T1 label, T2 val);
	~labelpair();
	void setkey(T1 label);
	void setvalue(T2 val);
	T2 getvalue() const;
	T1 getkey() const;
	bool isequal(labelpair<T1, T2> &p);
	bool valuecomparable(labelpair<T1, T2> &p);
	bool keycomparable(labelpair<T1, T2> &p);
	bool operator<( const labelpair<T1, T2> &p) const{
		if(this->getvalue()<p.getvalue())
			return true;
		else return false;
	}
	bool operator>( const labelpair<T1, T2> &p) const{
		if(this->getvalue()>p.getvalue())
			return true;
		else return false;
	}
	bool operator>=( const labelpair<T1, T2> &p) const{
		if(this->getvalue()>=p.getvalue())
			return true;
		else return false;
	}
	bool operator<=( const labelpair<T1, T2> &p) const{
		if(this->getvalue()<=p.getvalue())
			return true;
		else return false;
	}
};

template <class T1, class T2>
labelpair<T1, T2>::labelpair(T1 label, T2 val){
	this->mykey=label;
	this->value=val;

}

template <class T1, class T2>
void labelpair<T1, T2>::setkey(T1 label){
	this->mykey=label;
}

template <class T1, class T2>
void labelpair<T1, T2>::setvalue(T2 val){
	this->value=val;
}
template <class T1, class T2>
T2 labelpair<T1, T2>::getvalue() const{
	return this->value;
}
template <class T1, class T2>
T1 labelpair<T1, T2>::getkey() const{
	return this->mykey;
}

template <class T1, class T2>
bool labelpair<T1, T2>::isequal(labelpair<T1, T2> &p){
	if(p.getkey()==this->mykey&&p.getvalue()==this->value)
		return true;
	else
		return false;
}

template <class T1, class T2>
bool labelpair<T1, T2>::keycomparable(labelpair<T1, T2> &p){
	if(p.getkey()==this->mykey)
		return true;
	else
		return false;
}

template <class T1, class T2>
bool labelpair<T1, T2>::valuecomparable(labelpair<T1, T2> &p){
	if(p.getvalue()==this->value)
		return true;
	else 
		return false;
}
template <class T1, class T2>
labelpair<T1, T2>::~labelpair(){
}
#endif

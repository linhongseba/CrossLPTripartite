/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sailw
 */
public class Vertex {

    double getEdge(Vertex vertex) {
        if(neighbors.containsKey(vertex))return neighbors.get(vertex);
        return 0;
    }

    Iterable<Vertex> getNeighbors() {
        return neighbors.keySet();
    }
    
    public static class Type {}

    private Type type;
    public final static Type typeA=new Type();
    public final static Type typeB=new Type();
    public final static Type typeC=new Type();
    private boolean ISY0;
    private double sume;
    private Matrix label;

    private final Map<Vertex,Double> neighbors;
    
    /**
     * 
     * @param _type initialize what the type is of this vertex: typeA(user),typeB(tweet),typeC(word)
     * @param _label label vector of this vertex
     * @param _isY0 whether the vertex is prelabeled
     */
    
    public Vertex(Type _type, Matrix _label,boolean  _isY0){
        type=_type;                              //initialize what the type is of this vertex: typeA(user),typeB(tweet),typeC(word)
        label=_label;                            //?
        neighbors=new HashMap<>();               //the edges denote the relationship between the vertex and other vertices
        ISY0=_isY0;                              //whether the graph is n*k or n*n
    }

    public Vertex(){
        neighbors=new HashMap<>();
    }
    
    /**
     * 
     * @param _type initialize what the type is of this vertex: typeA(user),typeB(tweet),typeC(word)
     * @param _label label vector of this vertex
     * @param _isY0 whether the vertex is prelabeled
     */
    public void init(Type _type, Matrix _label,boolean  _isY0){
        type=_type;
        label=_label;
        ISY0=_isY0;
    }
    
    public boolean isY0() {
        return ISY0;
    }
    
    public void setLabel(Matrix _label) {
        label=_label;
    }

    public Matrix getLabel() {
        return label;
    }
    
    public Type getType() { 
        return type;
    }

    public void addEdge(Vertex _neighbor, double _weight) {
        removeEdge(_neighbor);
        sume+=_weight;
        neighbors.put(_neighbor,_weight);
    }

    public void removeEdge(Vertex _neighbor) {
        if(neighbors.containsKey(_neighbor))sume-=neighbors.get(_neighbor);
        neighbors.remove(_neighbor);
    }

    public int degree() {
        return neighbors.size();
    }
    
    public double sumE() {
        return sume;
    }
    
}
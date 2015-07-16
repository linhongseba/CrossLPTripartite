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
    public static class Type {}

    final Type type;
    public final static Type typeA=new Type();
    public final static Type typeB=new Type();
    public final static Type typeC=new Type();
    final boolean ISY0;

    Matrix label;

    private final Map<Vertex,Double> neighbors;
    public Vertex(Type _type, Matrix _label,boolean  _isY0){
        type=_type;
        label=_label;
        neighbors=new HashMap<>();
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
        neighbors.put(_neighbor,_weight);
    }

    public void removeEdge(Vertex _neighbor) {
        neighbors.remove(_neighbor);
    }

    public int degree() {
        return neighbors.size();
    }
}
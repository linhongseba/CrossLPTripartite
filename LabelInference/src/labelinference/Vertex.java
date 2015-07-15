/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import java.util.Collection;
import java.util.HashSet;

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

    Matrix label;

    private final Collection<Vertex> neighbors;
    public Vertex(Type _type, Matrix _label){
        type=_type;
        label=_label;
        neighbors=new HashSet<>();
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

    public void addEdge(Vertex _neighbor) {
        neighbors.add(_neighbor);
    }

    public void removeEdge(Vertex _neighbor) {
        neighbors.remove(_neighbor);
    }

    public int degree() {
        return neighbors.size();
    }
}
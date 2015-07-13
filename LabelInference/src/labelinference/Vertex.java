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
    private Object info;
    private final Collection<Vertex> neighbors;
    public Vertex(Object _info){
        info=_info;
        neighbors=new HashSet<>();
    }

    public Vertex(){
        info=null;
        neighbors=new HashSet<>();
    }

    public void setInfo(Object _info) {
        info=_info;
    }

    public Object getInfo() {
        return info;
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference;

import java.util.Collection;

/**
 *
 * @author sailw
 */
public class Vertex {
    private Object info;
    private Collection<Vertex> neighbors;
    public Vertex(Object _info){

    }

    public Vertex(){

    }

    public void setInfo(Object _info) {

    }

    public Object getInfo() {
        return null;
    }

    public void newEdge(Vertex _neighbor) {

    }

    public void deleteEdge(Vertex _neighbor) {

    }

    public int degree() {
        return 0;
    }
}
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
public class Graph {
    private final Collection<Vertex> vertices;

    public Graph() {
        vertices=new HashSet<>();
    }

    public Graph(Collection<Vertex> _vertices) {
        vertices=_vertices;
    }

    public void addVertex(Vertex _vertex) {
        vertices.add(_vertex);
    }

    public void addEdge(Vertex a, Vertex b) {
        a.addEdge(b);
        b.addEdge(a);
    }

    public void removeVertex(Vertex _vertex) {
        vertices.remove(_vertex);
    }

    public void removeEdge(Vertex a, Vertex b) {
        a.removeEdge(b);
        b.removeEdge(a);
    }

    public Collection<Vertex> getVertices() {
        return vertices;
    }

    public Matrix toMatrix() {
        double data[][]=new double[vertices.size()][vertices.size()];
        MatrixFactory matrixFactory=MatrixFactory.getInstance();
        return matrixFactory.creatMatrix(data);
    }
}
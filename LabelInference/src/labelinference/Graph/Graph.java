/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labelinference.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import labelinference.Matrix.MatrixFactory;
import labelinference.Matrix.Matrix;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw
 */
public class Graph {
    private final Map<String, Vertex> vertices;
    private final int numLabels;
    
    public Graph(String path) throws FileNotFoundException {
        vertices=new HashMap<>();
        MatrixFactory matrixFactory=MatrixFactory.getInstance();
        Scanner in=new Scanner(new FileReader(new File(path)));
        numLabels=in.nextInt();
        Map<Integer,Vertex.Type> c2type=new HashMap<>();
        c2type.put((int)'A', Vertex.typeA);
        c2type.put((int)'B', Vertex.typeB);
        c2type.put((int)'C', Vertex.typeC);
        
        while(in.hasNext()) {
            try {
                String vid=in.next();
                int nNei=in.nextInt();
                Matrix label=matrixFactory.creatMatrix(numLabels, 1);
                for(int row=0;row<numLabels;row++)label.set(row, 0, in.nextDouble());
                if(findVertexByID(vid)==null)addVertex(new Vertex(vid));
                Vertex vertex=findVertexByID(vid);
                vertex.init(c2type.get((int)vid.charAt(0)),label,label.norm(Matrix.FIRST_NORM)>0.5);
                for(int j=0;j<nNei;j++) {
                    String nid=in.next();
                    double weight=in.nextDouble();
                    if(findVertexByID(nid)==null)addVertex(new Vertex(nid));
                    vertex.addEdge(findVertexByID(nid), weight);
                }
            } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
        }
    }
    
    public Graph(int _numLabels) {
        vertices=new HashMap<>();
        numLabels=_numLabels;
    }
    
    public Graph(int _numLabels, Collection<Vertex> _vertices) {
        vertices=new HashMap<>();
        numLabels=_numLabels;
        _vertices.forEach(this::addVertex);
    }
    
    public int getNumLabels() {
        return numLabels;
    }

    public final void addVertex(Vertex _vertex) {
        ((HashMap<String,Vertex>)vertices).put(_vertex.getId(), _vertex);
    }

    public void addEdge(Vertex a, Vertex b, double weight) {
        a.addEdge(b,weight);
        b.addEdge(a,weight);
    }

    public void removeVertex(Vertex _vertex) {
        vertices.remove(_vertex.getId());
    }

    public void removeEdge(Vertex a, Vertex b) {
        a.removeEdge(b);
        b.removeEdge(a);
    }

    public Collection<Vertex> getVertices() {
        return vertices.values();
    }

    public Collection<Vertex> getVertices(Predicate<Vertex> p) {
        Collection<Vertex> y0=new HashSet<>();
        for(Vertex v:vertices.values())if(p.test(v))y0.add(v);
        return y0;
    }
    
    public Matrix toMatrix() {
        double data[][]=new double[vertices.size()][vertices.size()];
        MatrixFactory matrixFactory=MatrixFactory.getInstance();
        return matrixFactory.creatMatrix(data);
    }
    
    public final Vertex findVertexByID(String vid) {
        return vertices.get(vid);
    }
}
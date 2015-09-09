package labelinference.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.function.Predicate;

import labelinference.Matrix.Matrix;
import labelinference.Matrix.MatrixFactory;
import labelinference.exceptions.ColumnOutOfRangeException;
import labelinference.exceptions.RowOutOfRangeException;

/**
 *
 * @author sailw
 */
public class Graph {
    private final Map<String, Vertex> vertices;
    private final int numLabels;
    private final Map<Vertex, Matrix> RINIT=new HashMap<>();
    private final Map<Vertex, Matrix> GINIT=new HashMap<>();
    
    public Graph(String path) throws FileNotFoundException {
        vertices=new HashMap<>();
        MatrixFactory matrixFactory=MatrixFactory.getInstance();
        Scanner in=new Scanner(new FileReader(new File(path)));
        numLabels=in.nextInt();
        Map<Integer,Vertex.Type> c2type=new HashMap<>();
        c2type.put((int)'A', Vertex.typeA);
        c2type.put((int)'B', Vertex.typeB);
        c2type.put((int)'C', Vertex.typeC);
        try {
            while(in.hasNext()) {
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
            }
        } catch (ColumnOutOfRangeException | RowOutOfRangeException ex) {}
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
    
    public Collection<String> getIDs() {
        return vertices.keySet();
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
    
    // used to calculate Objective for all the edge entries, only used it for small graph (graph-0, graph-1)
    public int cntA, cntB, cntC;
    public Map <Vertex, Integer> aMap, bMap, cMap;
    public Matrix mAB, mAC, mBC;

    // generate Bimatrix AB, AC and BC
    public void generateSubMatrix() throws ColumnOutOfRangeException, RowOutOfRangeException {
        aMap = new TreeMap<Vertex, Integer>();
        bMap = new TreeMap<Vertex, Integer>();
        cMap = new TreeMap<Vertex, Integer>();
        cntA = cntB = cntC = 0;
        
        for (Vertex v: vertices.values()) {
            if (v.getType() == Vertex.typeA) {
                aMap.put(v, cntA);
                cntA++;
            } else if (v.getType() == Vertex.typeB) {
                bMap.put(v, cntB);
                cntB++;
            } else {
                cMap.put(v, cntC);
                cntC++;
            }
        }
        
        MatrixFactory matrixFactory = MatrixFactory.getInstance();
        mAB = matrixFactory.creatMatrix(cntA, cntB); // init to 0
        mAC = matrixFactory.creatMatrix(cntA, cntC); // init to 0
        mBC = matrixFactory.creatMatrix(cntB, cntC); // init to 0
        
        for (Vertex a: aMap.keySet()) {
           for (Vertex aneighbor: a.getNeighbors()) {
               if (aneighbor.getType() == Vertex.typeB) {
                   mAB.set(aMap.get(a), bMap.get(aneighbor), a.getEdge(aneighbor));
               }
           }
        }
        
        for (Vertex a: aMap.keySet()) {
            for (Vertex aneighbor: a.getNeighbors()) {
                if (aneighbor.getType() == Vertex.typeC) {
                    mAC.set(aMap.get(a), cMap.get(aneighbor), a.getEdge(aneighbor));
                }
            }
         }
        
        for (Vertex b: bMap.keySet()) {
            for (Vertex bneighbor: b.getNeighbors()) {
                if (bneighbor.getType() == Vertex.typeC) {
                    mBC.set(bMap.get(b), cMap.get(bneighbor), b.getEdge(bneighbor));
                }
            }
         }
    }
    
    ///////////new code ends here /////////
    ///////////////////////////////////////
}
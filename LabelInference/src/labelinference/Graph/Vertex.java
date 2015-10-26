package labelinference.Graph;

import java.util.Collection;
import labelinference.Matrix.Matrix;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author sailw
 */
public class Vertex implements Comparable<Vertex> {
    public static class Type {}

    private Type type;
    public final static Type typeA=new Type();
    public final static Type typeB=new Type();
    public final static Type typeC=new Type();
    public static final Collection<Type> types=new HashSet<>();
    
    static {
        types.add(typeA);
        types.add(typeB);
        types.add(typeC);
    }
    
    private boolean ISY0;
    private double sume;
    private Matrix label;
    private Matrix templabel;
    private final String id;
    private final Map<Vertex,Double> neighbors;
    
    /**
     * 
     * @param _id
     * @param _type initialize what the type is of this vertex: typeA(user),typeB(tweet),typeC(word)
     * @param _label label vector of this vertex
     * @param _isY0 whether the vertex is prelabeled
     */
    public Vertex(String _id, Type _type, Matrix _label,boolean  _isY0){
        type=_type;
        label=_label;
        neighbors=new HashMap<>();
        ISY0=_isY0;
        id=_id;
    }

    public Vertex(String _id){
        neighbors=new HashMap<>();
        id=_id;
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
    
    public void setLabelRef(Matrix _label) {
        label=_label;
    }
    public void setLabel(Matrix _label){
        _label.copyto(label);
    }

    public Matrix getLabel() {
        return label;
    }
    public Matrix getTempLabel(){
        return templabel;
    }
    public void setTempLabelRef(Matrix _label){
        templabel=_label;
    }
    public void setTempLabel(Matrix _label){
        _label.copyto(templabel);
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
    
    public double getEdge(Vertex vertex) {
        if(neighbors.containsKey(vertex))return neighbors.get(vertex);
        return 0;
    }

    public Iterable<Vertex> getNeighbors() {
        return neighbors.keySet();
    }
    
    public String getId() {
        return id;
    }
    
    @Override
    public int hashCode(){
        return (int) id.hashCode();
    }
    
    @Override
    public int compareTo(Vertex other){
        return this.id.compareTo(other.id);
    }
    
    @Override
    public String toString(){
        return id;
    }
}
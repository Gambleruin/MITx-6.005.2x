/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/**
 * TODO specification
 * Immutable.
 * This class is internal to the rep of ConcreteEdgesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Edge<L> {
	private final L source;
    private final L target;
    private final int weight;
    
    // TODO fields
    
    // Abstraction function:
    //   TODO
    // Representation invariant:
    //   TODO
    // Safety from rep exposure:
    //   TODO
    
    // TODO constructor
    public Edge(final L source, final L target, final int weight) {
    	assert weight >0;
    	
    	this.source =source;
    	this.target =target;
    	this.weight =weight;
    	checkRep();
    }
    
    // TODO checkRep
	private void checkRep() {
		assert source !=null;
		assert target !=null;
		assert weight >0;
	}
    
	
    // TODO methods
    
    // TODO toString()
	
	//observers
    /** Returns this Edge's source*/   
    public L getSource(){
        return source;
    }
    /**Returns this Edge's target*/
    public L getTarget(){
        return target;
    }
    /**Returns this Edge's weight*/
    public int getWeight(){
        return weight;
    }
    
    //producers
    /**
     * Changes the weight of this Edge
     * 
     * @param newWeight an int, requires newWeight > 0
     * @return a new Edge with newWeight 
     */
    public Edge<L> setWeight(int newWeight){
        checkRep();
        return new Edge<>(source, target, newWeight);
    }
}

/**
 * An implementation of Graph.
 * 
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteEdgesGraph<L> implements Graph<L> {
    
    private final Set<L> vertices = new HashSet<>();
    private final List<Edge<L>> edges = new ArrayList<>();
    
    // Abstraction function:
    //   TODO
    // Representation invariant:
    //   TODO
    // Safety from rep exposure:
    //   TODO
    private int indexOfInEdges(L source, L target) {
    	for(int i =0; i<edges.size(); i++) {
    		Edge<L> edge =edges.get(i);
    		if(edge.getSource().equals(source)&&
    				edge.getTarget().equals(target)) {
    			return i;
    		}
    	}
    	return -1;
    }
    
    // TODO constructor
    public ConcreteEdgesGraph(){
    }
    
    // TODO checkRep
    private void checkRep(){
        final int sizeOfEdges = edges.size();
        final int sizeOfVertices = vertices.size();
        int minNumberOfVertices = 
                sizeOfEdges == 0 ? 0 : (int)Math.ceil(Math.sqrt(2 * sizeOfEdges) + 0.5);
        
        assert sizeOfVertices >= minNumberOfVertices;  
    }
    
 // Representation invariant:
    //   vertices is a set of objects of type L
    //   edges is a list of distinct weighted Edges made by 
    //      distinct pairs of vertices(no pair of vertices exists more than once).   
    //   An edge must be connected to at least v number of vertices, for example,
    //     2 edges require at least 3 vertices, 5 edges require at least 4 vertices
    //     vertices.size() >= Math.ceil(Math.sqrt(2*edges.size) + 0.5)
    //     (source) https://math.stackexchange.com/a/1954272
    //
    // Safety from rep exposure:
    //   All fields are private and final
    //   vertices and edges are mutable types, so operations use defensive copies and
    //   immutable wrappers to avoid sharing the rep's objects to clients
    
    @Override public boolean add(L vertex) {
        return vertices.add(vertex);
    }
    
    @Override public int set(L source, L target, int weight) {
        assert weight >=0;
        
        int indexOfEdge =indexOfInEdges(source, target);
        //previous weight
        int previousWeight =0;
        final Edge<L> previousEdge;
        
        if(weight >0) {
        	Edge<L>newEdge =new Edge<>(source, target, weight);
        	if(indexOfEdge <0) {
        		add(source);
        		add(target);
        		edges.add(newEdge);
        	} else {
        		previousEdge =edges.set(indexOfEdge, newEdge);
        		previousWeight =previousEdge.getWeight();
        	}
        } else if(weight ==0&&indexOfEdge >=0) {
        	previousEdge =edges.remove(indexOfEdge);
        	previousWeight =previousEdge.getWeight();
        }
        checkRep();
        return previousWeight;
    }
    
    
    @Override public boolean remove(L vertex) {
        final int initialSizeVertices =vertices.size();
        final int initialSizeEdges =edges.size();
        
       
        Predicate<Edge<L>> vertexInEdge =(Edge<L> edge)->
        	((edge.getSource().equals(vertex))||
        			(edge.getTarget().equals(vertex)));
                	
        Predicate<L> vertexInVertices =v->v.equals(vertex);
        
        boolean removedEdge =edges.removeIf(vertexInEdge);
        boolean removedVertice =vertices.removeIf(vertexInVertices);
        
        if(removedVertice) {
        	assert initialSizeVertices !=vertices.size();
        	assert initialSizeVertices -1 ==vertices.size();
        	
        }
        if(removedEdge) {
        	assert initialSizeEdges !=edges.size();
        	assert removedVertice;
        }
        checkRep();
        return initialSizeVertices -1 ==vertices.size();
    }
    
    /* returns an read-only view of this concreteEdgesGraph's vertices*/
    @Override public Set<L> vertices() {
        return Collections.unmodifiableSet(vertices);
    }
    
    /* returns a map of a target's sources */
    @Override public Map<L, Integer> sources(L target) {
        return edges.stream()
        		.filter(edge->edge.getTarget().equals(target))
        		.collect(Collectors.toMap(Edge::getSource, Edge::getWeight));
    }
    
    /* Returns a map of a source's targets */
    @Override public Map<L, Integer> targets(L source) {
        return edges.stream()
        		.filter(edge->edge.getSource().equals(source))
        		.collect(Collectors.toMap(Edge::getTarget, Edge::getWeight));
    }
    
    // TODO toString()
    @Override public String toString() {
    	if(edges.isEmpty() ) {
    		return "Empty Graph";
    	}
    	return edges.stream()
    			.map(edge->edge.toString())
    			.collect(Collectors.joining("\n"));
    }
    
}



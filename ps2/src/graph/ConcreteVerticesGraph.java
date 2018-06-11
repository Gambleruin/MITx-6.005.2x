/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * TODO specification
 * Mutable.
 * This class is internal to the rep of ConcreteVerticesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Vertex<L> {
    
    // TODO fields
	private final L label;
	private final Map<L, Integer> sources =new HashMap<>();
	private final Map<L, Integer> targets = new HashMap<>();
    

    // Representation invariant:
    //   TODO
    // Safety from rep exposure:
    //   TODO
    
    // TODO constructor
	public Vertex(final L label) {
		this.label =label;
	}
    
    // TODO checkRep
	private void checkRep() {
		final Set<L> sourceLabels =sources.keySet();
		final Set<L> targetLabels =targets.keySet();
		
		assert !sourceLabels.contains(this.label);
		assert !targetLabels.contains(this.label);
	}
    
    // TODO methods
	private void checkInputLabel(final L inputLabel) {
		assert inputLabel !=null;
		assert inputLabel !=this.label;
	}
	
	/* returns the label of this vertex */
	public L getLabel() {
		return this.label;
		
	}
	
    // Abstraction function: add a source connection to the vertex
    //   TODO
	public boolean addSource(final L source, final int weight) {
		checkInputLabel(source);
		assert weight >0;
		
		if(sources.putIfAbsent(source, weight) ==null) {
			checkRep();
			return true;
		}
		return false;
	}
    
	// Abstraction function: add a target connection to the vertex
	public boolean addTarget(final L target, final int weight) {
		checkInputLabel(target);
		assert weight >0;
		
		if( targets.putIfAbsent(target, weight) == null ) {
			checkRep();
			return true;
		}
		return false;
	}
	
	
	/*
	 * return the previous weight of the vertex connection to this vertex
	 * zero if no such connection exists
	 * */
	public int removeSource(final L source) {
		checkInputLabel(source);		
		Integer previousWeight =sources.remove(source);
		
		checkRep();
		return previousWeight ==null ? 0:previousWeight;
		
	}
	
	public int removeTarget(final L target) {
		checkInputLabel(target);
		Integer previousWeight =targets.remove(target);
		
		checkRep();
		return previousWeight == null?0:previousWeight;
	}
	
	public int remove(final L vertex) {
		checkInputLabel(vertex);
		int sourcePreWeight =removeSource(vertex);
		int targetPreWeight =removeTarget(vertex);
		
		if(sourcePreWeight >0 && targetPreWeight >0) {
			assert sourcePreWeight ==targetPreWeight;
		}
		return sourcePreWeight == 0? targetPreWeight : sourcePreWeight;
		
	}
	
	// if weight >0, then add the source if it does not exist yet, otherwise do nothing
	public int setSource(final L source, final int weight ) {
		checkInputLabel(source);
		assert weight >=0;
		final int previousWeight;
		
		if(weight ==0) {
			previousWeight =removeSource(source);
		} else if ( addSource(source, weight) || sources.get(source) ==(Integer)weight) {
			previousWeight =0;
		} else {
			previousWeight =sources.replace(source, weight);
		}
		checkRep();
		return previousWeight;
	}
	
	// if weight >0, then add the target if it does not exist yet, otherwise do nothing
	public int setTarget(final L target, final int weight) {
		checkInputLabel(target);
		assert weight >=0;
		final int previousWeight;
		
		if(weight == 0) {
			previousWeight =removeTarget(target);
		} else if (addTarget(target, weight)||targets.get(target) ==(Integer)weight) {
			previousWeight =0;
		} else {
			previousWeight =targets.replace(target,weight);
		}
		checkRep();
		return previousWeight;
	}
	
	/* Returns an immutable view of this vertex's sources */
	public Map<L, Integer> getSources(){
		return Collections.unmodifiableMap(sources);
	}
	
	/* Return an immutable view of this vertex's targets */
	public Map<L, Integer> getTargets(){
		return Collections.unmodifiableMap(targets);
	}
	
	/*
	 * Checks if a vertex is a target from this vertex
	 * */
	public boolean isTarget(final L vertex) {
		return targets.containsKey(vertex);
	}
	
	/*
	 * Checks if a vertex is a source from this vertex
	 * */
	public boolean isSource(final L vertex) {
		return sources.containsKey(vertex);
	}
		
    // TODO toString()
	@Override public String toString(){
        return String.format(
                "%s -> %s \n" +
                "%s <- %s",
                this.label.toString(), this.targets,
                this.label.toString(), this.sources);
    }
}

/**
 * An implementation of Graph.
 * 
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteVerticesGraph<L> implements Graph<L> {
    
    private final List<Vertex<L>> vertices = new ArrayList<>();
    
    // Abstraction function:
    //   TODO
    private int indexInVertices(L label){
        for(int i = 0; i < vertices.size(); i++){
            if ( vertices.get(i).getLabel().equals(label) ) {
                return i;
            }
        }
        return -1;
    }
    // Representation invariant:
    //   TODO
    // Safety from rep exposure:
    //   TODO
    
    // TODO constructor
    public ConcreteVerticesGraph() {
    }
    
    // TODO checkRep
    private void checkRep(){        
        assert vertices().size() == vertices.size();
    }
    
    @Override public Set<L> vertices() {
        return vertices.stream()
                .map(Vertex::getLabel)
                .collect(Collectors.toSet());
    }
    
    @Override public boolean add(L vertex) {
    	if ( vertices().contains(vertex) ) {
            return false;
        }
        Vertex<L> vertexObj = new Vertex<>(vertex);    
        final boolean vertexAdded = vertices.add(vertexObj);
        checkRep();
        return vertexAdded;
    }
    
    @Override public int set(L source, L target, int weight) {
        assert source != target;
        assert weight >= 0;
        
        final Vertex<L> sourceVertex;
        final Vertex<L> targetVertex;
        
        Set<L> verticeLabels =vertices();
        if ( verticeLabels.contains(source) ) {
            int sourceIndex = indexInVertices(source);
            sourceVertex = vertices.get(sourceIndex);
        } else {
            sourceVertex = new Vertex<>(source);
            vertices.add(sourceVertex);
        }
        
        if ( verticeLabels.contains(target) ) {
            int targetIndex = indexInVertices(target);
            targetVertex = vertices.get(targetIndex);
        } else {
            targetVertex = new Vertex<>(target);
            vertices.add(targetVertex);
        }
        
        int sourcePrevWeight = sourceVertex.setTarget(target, weight);
        int targetPrevWeight = targetVertex.setSource(source, weight);
        assert sourcePrevWeight == targetPrevWeight;
        
        checkRep();
        return sourcePrevWeight;
        
    }
    
    
    @Override public boolean remove(L vertex) {
        //sanity check
    	if(! (vertices().contains(vertex)) ) {
    		return false;
    	}
    	int vertexIndex =indexInVertices(vertex);
    	assert vertexIndex != -1;
    	// remove both from vertex class and current class
    	final Vertex<L> removedVertex =vertices.remove(vertexIndex);
    	assert removedVertex.getLabel() ==vertex;
    	
    	for(Vertex<L> v: vertices) {
    		v.remove(vertex);
    	}
    	return removedVertex !=null;
    	
    }
    
    
    /* Returns an immutable view of source vertices to a target*/
    @Override public Map<L, Integer> sources(L target) {
        final int targetIndex =indexInVertices(target);
        if(targetIndex <0) {
        	return Collections.emptyMap();
        }
        Vertex<L>targetVertex =vertices.get(targetIndex);
        return Collections.unmodifiableMap(targetVertex.getSources());
    }
    
    /* Returns an immutable view of target vertices from a source */
    @Override public Map<L, Integer> targets(L source) {
    	final int sourceIndex = indexInVertices(source);
        if ( sourceIndex < 0 ) {
            return Collections.emptyMap();
        }
        Vertex<L> sourceVertex = vertices.get(sourceIndex);
        
        return Collections.unmodifiableMap(sourceVertex.getTargets());
    }
    
    // TODO toString()
    
}



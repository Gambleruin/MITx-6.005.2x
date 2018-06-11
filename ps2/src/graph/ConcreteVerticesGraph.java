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
    
    // TODO checkRep
    private void checkRep(){        
        assert vertices().size() == vertices.size();
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
    
    @Override public int set(String source, String target, int weight) {
        throw new RuntimeException("not implemented");
    }
    
    @Override public boolean remove(String vertex) {
        throw new RuntimeException("not implemented");
    }
    
    @Override public Set<String> vertices() {
        throw new RuntimeException("not implemented");
    }
    
    @Override public Map<String, Integer> sources(String target) {
        throw new RuntimeException("not implemented");
    }
    
    @Override public Map<String, Integer> targets(String source) {
        throw new RuntimeException("not implemented");
    }
    
    // TODO toString()
    
}



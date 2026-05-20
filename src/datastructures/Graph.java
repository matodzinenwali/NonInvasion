package datastructures;

import interfaces.IGraph;
import interfaces.IList;

public class Graph<T> implements IGraph<T> {

	@Override
	public void addNode(T node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEdge(T from, T to, double weight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IList<T> getNeighbours(T node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getWeight(T from, T to) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IList<T> getAllNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int nodeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int edgeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean containsNode(T node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsEdge(T from, T to) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeNode(T node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeEdge(T from, T to) {
		// TODO Auto-generated method stub
		
	}

}

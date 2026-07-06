package datastructures;

import java.util.Iterator;

import interfaces.IHeap;
import interfaces.IIterator;
import interfaces.IList;
import interfaces.ITree;

public class Tree<V> implements ITree<double[], V> {
	
	private static class Node<V>{
		double[] key;
		V value;
		Node<V> left;
		Node<V> right;
		
		Node(double[] key, V value){
			this.key = key;
			this.value = value;
			this.left = null;
			this.right = null;
		}
	}
	
	//class instance variables
	private Node<V> root;
	private int size;
	private final int dimensions;
	
	public Tree(int dimensions) {
		if(dimensions <= 0) {
			throw new IllegalArgumentException("Dimension must be positive");
		}
		this.dimensions = dimensions;
		this.root = null;
		this.size = 0;
	}

	@Override
	public void insert(double[] key, V value) {
		if(key == null) {
			throw new IllegalArgumentException("Key cannot be null");
		}
		if(key.length != dimensions) {
			throw new IllegalArgumentException("Key dimension mismatch. Expected" + dimensions + ", got:" + key.length);
		}
		root = insertRec(root, key, value, 0);
		size++;
		
	}


	private Node<V> insertRec(Node<V> node, double[] key, V value, int depth) {
		if(root == null) {
			return new Node<>(key, value);
		}
		
		int axis = depth % dimensions;
		if(key[axis] < node.key[axis]) {
			node.left = insertRec(node.left, key, value, depth + 1);
		}else {
			node.right = insertRec(node.right, key, value, depth + 1);
		}
		return node;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public void clear() {
		root = null;
		size = 0;
	}


	@Override
	public V search(double[] key) {
		if(key == null || key.length != dimensions) {
			return null;
		}
		return searchRec(root, key, 0);
	}

	private V searchRec(Node<V> node, double[] key, int depth) {
		if(node == null) {
			return null;
		}
		
		//check if exact match
		if(vectorEqual(node.key, key)) {
			return node.value;
		}
		
		int axis = depth % dimensions;
		if(key[axis] < node.key[axis]) {
			return searchRec(node.left, key, depth + 1);
		}else {
			return searchRec(node.right, key, depth + 1);
		}
	}

	@Override
	public IList<V> nearestSearch(double[] queryKey, int k) {
		if(queryKey == null || queryKey.length != dimensions) {
			throw new IllegalArgumentException("Invalid query key");
		}
		if(k <= 0) {
			return new LinkedList<>();
		}
		/*
		 * Use a max-heap to keep the k best matches
		 * I`ll use the wrapper class to hold the distance + value
		 */
		IHeap<DistanceValue<V>> heap = new MinHeap<>();
		
		IList<DistanceValue<V>> allCandidates = new LinkedList<>();
		collectAll(root, queryKey, allCandidates);
		
		//use min-heap to sort by distance
		IHeap<DistanceValue<V>> minHeap = new MinHeap<>();
		IIterator<DistanceValue<V>> it = allCandidates.iterator();
		while(it.hasNext()) {
			minHeap.insert(it.next());
		}
		
		IList<V> results = new LinkedList<>();
		int count = Math.min(k,  minHeap.size());
		for(int i = 0; i < count; i++) {
			results.add(minHeap.extractMin().value);
		}
		
		return results;
	}
	
	//helper: collect all nodes in the tree with their distance to query
	private void collectAll(Node<V> node, double[] query, IList<DistanceValue<V>> list) {
		if(node == null) return;
		
		double dist = euclideanDistance(node.key, query);
		list.add(new DistanceValue<>(dist, node.value));
		
		collectAll(node.left, query, list);
		collectAll(node.right, query, list);
	}
	
	//wrapper for heap
	private static class DistanceValue<V> implements Comparable<DistanceValue<V>>{
		
		double distance;
		V value;
		
		public DistanceValue(Double distance, V value) {
			this.distance = distance;
			this.value = value;
		}

		@Override
		public int compareTo(DistanceValue<V> other) {
			return Double.compare(this.distance, other.distance);
		}
		
	}
	
	private double euclideanDistance(double[] a, double[] b) {
		double sum = 0;
		for(int i = 0; i < a.length; i++){
			double diff = a[i] - b[i];
			sum += diff * diff;
		}
		return Math.sqrt(sum);
	}
	
	private boolean vectorEqual(double[] a, double[] b) {
		if(a.length != b.length) return false;
		for(int i = 0; i < a.length; i++) {
			if(a[i] != b[i]) return false;
		}
		return true;
	}

}

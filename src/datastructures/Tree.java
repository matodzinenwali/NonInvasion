package datastructures;

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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public V search(double[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IList<V> nearestSearch(double[] queryKey, int k) {
		// TODO Auto-generated method stub
		return null;
	}

}

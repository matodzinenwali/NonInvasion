package datastructures;

import interfaces.IHeap;

public class MinHeap<T extends Comparable<T>> implements IHeap<T> {
	
	private static final int DEFAULT_CAPACITY = 16;
	private Object[] array;
	private int size;
	
	//parameterless constructor
	public MinHeap() {
		this.array = new Object[DEFAULT_CAPACITY];
		this.size = 0;
	}
	
	//overloaded constructor
	public MinHeap(int initialCapacity) {
		this.array = new Object[initialCapacity];
		this.size = 0;
	}

	@Override
	public void insert(T element) {
		if(element == null) {
			throw new IllegalArgumentException("cannot insert null");
		}
		//resize if required
		if(size == array.length) {
			resize();
		}
		array[size] = element;
		size++;
		bubbleUp(size - 1);
		
	}
	
	@SuppressWarnings("unchecked")
	private void bubbleUp(int index) {
		while(index > 0) {
			int parentIndex = (index - 1)/2;
			T child = (T) array[index];
			T parent = (T) array[parentIndex];
			if(child.compareTo(parent) >= 0) {
				break; //heap property is satisfied here
			}
			//now swap
			array[index] = parent;
			array[parentIndex] = child;
			index = parentIndex;
		}
	}

	@Override
	public T extractMin() {
		if(isEmpty()) {
			throw new IllegalStateException("Heap is empty");
		}
		@SuppressWarnings("unchecked")
		T min = (T) array[0];
		//move last element to root
		array[0] = array[size - 1];
		array[size - 1] = null;
		size--;
		if(size > 0) {
			bubbleDown(0);
		}
		return min;
	}
	
	@SuppressWarnings("unchecked")
	private void bubbleDown(int index) {
		while(true) {
			int leftChild = 2 * index + 1;
			int rightChild = 2 * index + 2;
			int smallest = index;
			
			if(leftChild < size) {
				T leftValue = (T) array[leftChild];
				T currValue = (T) array[smallest];
				if(leftValue.compareTo(currValue) < 0) {
					smallest = leftChild;
				}
			}
			if(rightChild < size) {
				T rightValue = (T) array[rightChild];
				T currValue = (T) array[smallest];
				if(rightValue.compareTo(currValue) < 0) {
					smallest = rightChild;
				}
			}
			if(smallest == index) {
				break;//heap property satisfied
			}
			//swap
			T temp = (T) array[index];
			array[index] = array[smallest];
			array[smallest] = temp;
			index = smallest;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T peekMin() {
		if(isEmpty()) {
			throw new IllegalStateException("Heap is empty");
		}
		return (T) array[0];
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
		for(int i = 0; i < size; i++) {
			array[i] = null;
		}
		size = 0;
	}
	
	private void resize() {
		Object[] newArray = new Object[array.length * 2];
		System.arraycopy(array, 0, newArray, size, size);
		array = newArray;
	}

}

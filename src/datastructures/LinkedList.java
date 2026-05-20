package datastructures;

import interfaces.IIterator;
import interfaces.IList;

public class LinkedList<T> implements IList<T>{
	
	//Inner node class
	private static class Node<T>{
		T data;
		Node<T> next;
		
		//inner class constructor
		Node(T data){
			this.data = data;
			this.next = null;
		}
	}
	
	//Main class instance variables
	private Node<T> head;
	private int size;
	
	//main class constructor
	LinkedList(){
		head = null;
		size = 0;
	}

	@Override
	public void add(T element) {
		Node<T> newNode =  new Node<>(element);
		if(head == null) {
			head = newNode;
		}else {
			Node<T> current = head;
			while(current.next != null) {
				current = current.next;
			}
			current.next = newNode;
		}
		size++;
		
	}

	@Override
	public T get(int index) {
		if(index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index " + index + ", size "+ size);
		}
		Node<T> current = head;
		for(int i = 0; i < index; i++) {
			current = current.next;
		}
		return current.data;
	}

	@Override
	public T remove(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index " + index + ", size " + size);
		}
		T removedData;
		if (index == 0) {
			removedData = head.data;
			head = head.next;
		} else {
			Node<T> prev = head;
			for (int i = 0; i < index - 1; i++) {
				prev = prev.next;
			}
			removedData = prev.next.data;
			prev.next = prev.next.next;
		}
		size--;
		return removedData;
	}

	@Override
	public boolean remove(T element) {
		if(head == null) return false;
		
		//in case element to be removed = head
		if(head.data.equals(element)) {
			head = head.next;
			size--;
			return true;
		}
		
		Node<T> current = head;
		while(current.next != null) {
			if(current.next.data.equals(element)) {
				current.next = current.next.next;
				size--;
				return true;
			}
			current = current.next;
		}
		return false;
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
		head = null;
		size = 0;
	}

	@Override
	public T set(int index, T element) {
		if(index  < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index " + index + ", size " + size);
		}
		Node<T> current = head;
		for(int i = 0; i < index; i++) {
			current = current.next;
		}
		T oldData = current.data;
		current.data = element;
		return oldData;
	}

	@Override
	public boolean contains(T element) {
		Node<T> current = head;
		while(current != null) {
			if(current.data.equals(element)) {
				return true;
			}
			current = current.next;
		}
		return false;
	}

	@Override
	public IIterator<T> iterator() {
		return new LinkedListIterator();
	}
	
	//inner iterator class
	private class LinkedListIterator implements IIterator<T>{
		
		//inner class instance variables
		private Node<T> current = head;
		private Node<T> lastReturned = null;

		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public T next() {
			if(current == null) {
				throw new java.util.NoSuchElementException();
			}
			lastReturned = current;
			T data = current.data;
			current = current.next;
			return data;
		}

		@Override
		public void remove() {
			if(lastReturned == null) {
				throw new IllegalStateException("next() has not been called yet");
			}
			LinkedList.this.remove(lastReturned.data);
			lastReturned = null;
		}
		
	}

}

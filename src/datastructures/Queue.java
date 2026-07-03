package datastructures;

import interfaces.IList;
import interfaces.IQueue;

public class Queue<T> implements IQueue<T> {
	
	//using my custom LinkedList - linked list based implementation of a queue
	private final IList<T> list;
	
	public Queue() {
		this.list = new LinkedList<>();
	}

	@Override
	public void enqueue(T element) {
		list.add(element);
	}

	@Override
	public T dequeue() {
		if(isEmpty()) {
			throw new IllegalStateException("Queue is empty");
		}
		return list.remove(0); // remove from the front(head)
	}

	@Override
	public T peek() {
		if(isEmpty()) {
			throw new IllegalStateException("Queue is empty");
		}
		return list.get(0);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public void clear() {
		list.clear();
	}

}

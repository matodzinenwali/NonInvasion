package interfaces;

public interface IQueue<T> {
	void enqueue(T element);
	T dequeue();
	T peek();
	boolean isEmpty();
	int size();
	void clear();
}

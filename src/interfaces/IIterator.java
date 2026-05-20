package interfaces;

public interface IIterator<T> {
	boolean hasNext();
	T next();
	void remove();
}

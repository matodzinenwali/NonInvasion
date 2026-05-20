package interfaces;


/**
 * A custom list interface (replaces java.util.List).
 */
public interface IList<T> {
    void add(T element);
    T get(int index);
    T remove(int index);
    boolean remove(T element);
    int size();
    boolean isEmpty();
    void clear();
    T set(int index, T element);
    boolean contains(T element);
    IIterator<T> iterator();  // custom iterator interface
}

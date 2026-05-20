package interfaces;


public interface IHeap<T extends Comparable<T>> {
    void insert(T element);
    T extractMin();   
    T peekMin();
    int size();
    boolean isEmpty();
    void clear();
}

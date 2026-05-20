package interfaces;

public interface ITree<K, V> {
    void insert(K key, V value);   // key = feature vector, value = animal ID / metadata
    V search(K key);               // exact match if needed
    IList<V> nearestSearch(K queryKey, int k);   // k nearest neighbours
    int size();
    boolean isEmpty();
    void clear();
}

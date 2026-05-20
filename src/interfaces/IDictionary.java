package interfaces;


public interface IDictionary<K, V> {
    void put(K key, V value);
    V get(K key);
    boolean containsKey(K key);
    V remove(K key);
    int size();
    boolean isEmpty();
    void clear();
    IList<K> keys();      // return list of all keys
    IList<V> values();    // return list of all values
}

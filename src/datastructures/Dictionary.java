package datastructures;

import interfaces.IDictionary;
import interfaces.IIterator;
import interfaces.IList;



public class Dictionary<K, V> implements IDictionary<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;
    
    private IList<Entry<K, V>>[] buckets;
    private int size;
    
    // Inner class for key-value pairs
    private static class Entry<K, V> {
        K key;
        V value;
        
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    @SuppressWarnings("unchecked")
    public Dictionary() {
        buckets = (IList<Entry<K, V>>[]) new IList[DEFAULT_CAPACITY];
        size = 0;
    }
    
    @SuppressWarnings("unchecked")
    public Dictionary(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        buckets = (IList<Entry<K, V>>[]) new IList[initialCapacity];
        size = 0;
    }
    
    private int getBucketIndex(K key) {
        if (key == null) {
            return 0;
        }
        return Math.abs(key.hashCode()) % buckets.length;
    }
    
    @Override
    public void put(K key, V value) {
        int index = getBucketIndex(key);
        if (buckets[index] == null) {
            buckets[index] = new LinkedList<>();
        }
        
        IList<Entry<K, V>> bucket = buckets[index];
        IIterator<Entry<K, V>> it = bucket.iterator();
        while (it.hasNext()) {
            Entry<K, V> entry = it.next();
            if ((key == null && entry.key == null) || 
                (key != null && key.equals(entry.key))) {
                entry.value = value;
                return;
            }
        }
        
        bucket.add(new Entry<>(key, value));
        size++;
        
        if ((double) size / buckets.length > LOAD_FACTOR) {
            resize();
        }
    }
    
    @Override
    public V get(K key) {
        int index = getBucketIndex(key);
        IList<Entry<K, V>> bucket = buckets[index];
        if (bucket == null) {
            return null;
        }
        
        IIterator<Entry<K, V>> it = bucket.iterator();
        while (it.hasNext()) {
            Entry<K, V> entry = it.next();
            if ((key == null && entry.key == null) || 
                (key != null && key.equals(entry.key))) {
                return entry.value;
            }
        }
        return null;
    }
    
    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }
    
    @Override
    public V remove(K key) {
        int index = getBucketIndex(key);
        IList<Entry<K, V>> bucket = buckets[index];
        if (bucket == null) {
            return null;
        }
        
        IIterator<Entry<K, V>> it = bucket.iterator();
        int position = 0;
        while (it.hasNext()) {
            Entry<K, V> entry = it.next();
            if ((key == null && entry.key == null) || 
                (key != null && key.equals(entry.key))) {
                bucket.remove(position);
                size--;
                return entry.value;
            }
            position++;
        }
        return null;
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
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                buckets[i].clear();
            }
        }
        size = 0;
    }
    
    @Override
    public IList<K> keys() {
        IList<K> allKeys = new LinkedList<>();
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                IIterator<Entry<K, V>> it = buckets[i].iterator();
                while (it.hasNext()) {
                    allKeys.add(it.next().key);
                }
            }
        }
        return allKeys;
    }
    
    @Override
    public IList<V> values() {
        IList<V> allValues = new LinkedList<>();
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                IIterator<Entry<K, V>> it = buckets[i].iterator();
                while (it.hasNext()) {
                    allValues.add(it.next().value);
                }
            }
        }
        return allValues;
    }
    
    @SuppressWarnings("unchecked")
    private void resize() {
        IList<Entry<K, V>>[] oldBuckets = buckets;
        buckets = (IList<Entry<K, V>>[]) new IList[oldBuckets.length * 2];
        size = 0;
        
        for (int i = 0; i < oldBuckets.length; i++) {
            if (oldBuckets[i] != null) {
                IIterator<Entry<K, V>> it = oldBuckets[i].iterator();
                while (it.hasNext()) {
                    Entry<K, V> entry = it.next();
                    put(entry.key, entry.value);
                }
            }
        }
    }
}

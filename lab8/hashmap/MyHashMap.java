package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    private int size;
    private double maxLoad;
    private HashSet<K> keys;
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }



    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = 0;
        this.maxLoad = maxLoad;
        keys = new HashSet<>();
        buckets = createTable(initialSize);
        for (int i = 0; i < initialSize; i ++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
        keys.clear();
        for (int i = 0; i < buckets.length - 1; i++) {
            buckets[i].clear();
        }
    }

    private int bucketIndex(K key) {
        return Math.floorMod(key.hashCode(), buckets.length);
    }

    public V get(K key) {
        Node node = getNode(key);
        if (node != null) {
            return node.value;
        }
        return null;
    }

    public boolean containsKey(K key) {
        return getNode(key) != null;
    }

    private Node getNode(K key) {
        int index = bucketIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    public void put(K key, V val) {
        Node node = getNode(key);
        if (node != null) {
            node.value = val;
        } else {
            node = createNode(key, val);
            int index = bucketIndex(key);
            buckets[index].add(node);
            size += 1;
            keys.add(key);
            if (((double)size / buckets.length) > maxLoad) {
                resize(buckets.length * 2);
            }
        }
    }

    private void resize(int newSize) {
        Collection<Node>[] oldTable = this.buckets;
        buckets = createTable(newSize);
        for (int i = 0; i < newSize; i ++) {
            buckets[i] = createBucket();
        }
        for (int i = 0; i < oldTable.length; i++) {
            for (Node node : oldTable[i]) {
                int index = bucketIndex(node.key);
                buckets[index].add(node);
            }
        }
    }

    public HashSet<K> keySet() {
        return new HashSet<>(keys);
    }

    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }
}

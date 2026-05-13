package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>  {
    private BSTNode root;
    private int size;

    private class BSTNode {
        K key;
        V value;
        BSTNode left;
        BSTNode right;

        BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    @Override
    public int size() {
        return size;
    }
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public V get(K key) {
        return getHelper(root, key);
    }

    private V getHelper(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        if (node.key.equals(key)) {
            return node.value;
        } else if (key.compareTo(node.key) < 0) {
            return getHelper(node.left, key);
        } else {
            return getHelper(node.right, key);
        }
    }

    @Override
    public boolean containsKey(K key) {
        return containsKeyHelper(root, key);
    }

    private boolean containsKeyHelper(BSTNode node, K key) {
        if (node == null) {
            return false;
        } else if (node.key.equals(key)) {
            return true;
        } else if (key.compareTo(node.key) < 0) {
            return containsKeyHelper(node.left, key);
        } else {
            return containsKeyHelper(node.right, key);
        }
    }

    @Override
    public void put(K key, V value) {
        root = putHelper(root, key, value);
    }

    private BSTNode putHelper(BSTNode node, K key, V value) {
        if (node == null) {
            size += 1;
            return new BSTNode(key, value);
        } else if (node.key.equals(key)) {
            node.value = value;
        } else if (key.compareTo(node.key) < 0) {
            node.left = putHelper(node.left, key, value);
        } else {
            node.right = putHelper(node.right, key, value);
        }
        return node;
    }

    public void printInOrder() {
        printHelper(root);
        System.out.println();
    }

    private void printHelper(BSTNode node) {
        if (node == null) {
            return;
        }
        printHelper(node.left);
        System.out.print(node.key + ", ");
        printHelper(node.right);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
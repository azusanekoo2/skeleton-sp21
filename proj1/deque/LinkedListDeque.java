package deque;

import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    private int size;
    private Node sentinel;

    private class Node {
        T item;
        Node prev;
        Node next;

        private Node(T i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }
    private class LinkedListDequeIterator implements Iterator<T> {
        private Node N;

        public LinkedListDequeIterator() {
            N = sentinel.next;
        }

        public boolean hasNext() {
            return N != sentinel;
        }

        public T next() {
            T returnItem = N.item;
            N = N.next;
            return returnItem;
        }
    }


    public LinkedListDeque() {
        size = 0;
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    public void addFirst(T item) {
        Node newNode = new Node(item, sentinel, sentinel.next);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size += 1;
    }

    public void addLast(T item) {
        Node newNode = new Node(item, sentinel.prev, sentinel);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size += 1;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (T x : this) {
            System.out.print(x + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T returnItem = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return returnItem;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T returnItem = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return returnItem;
    }

    public T get(int index) {
        if (index < 0) {
            return null;
        }
        for (T x : this) {
            if (index == 0) {
                return x;
            }
            index -= 1;
        }
        return null;
    }

    public T getRecursive(int index) {
        if (index < 0) {
            return null;
        }
        if (index >= size) {
            return null;
        }
        return helper(sentinel.next, index);
    }

    private T helper(Node node, int index) {
        if (index == 0) {
            return node.item;
        }
        return helper(node.next, index - 1);
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (this.size != other.size()) {
            return false;
        }
        for (int i = 0; i < this.size; i++) {
            if (!Objects.equals(this.get(i), other.get(i))) {
                return false;
            }
        }
        return true;
    }
}

package deque;

import java.util.Iterator;
import java.util.Objects;

public class ArrayDeque<T> implements Iterable<T> {
    private int size;
    private T[] items;
    private int nextFirst;
    private int nextLast;
    private int INITIAL_CAPACITY = 8;

    public ArrayDeque() {
        size = 0;
        items = (T[]) new Object[INITIAL_CAPACITY];
        nextFirst = 4;
        nextLast = 5;
    }
    public class ArrayDequeIterator implements Iterator<T> {
        private int i;
        private int n;
        public ArrayDequeIterator() {
            if (nextFirst + 1 < items.length) i = nextFirst + 1;
            else i = nextFirst + 1 - items.length;
            n = size;
        }

        public boolean hasNext() {
            return n != 0;
        }
        public T next() {
            T returnItem = items[i];
            i += 1;
            if (i == items.length) i = 0;
            n -= 1;
            return returnItem;
        }
    }
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
    public void addFirst(T item) {
        if (size == items.length) {
            resize(2 * items.length);
        }
        items[nextFirst] = item;
        nextFirst -= 1;
        size += 1;
        if (nextFirst < 0) nextFirst = items.length - 1;
    }
    public void addLast(T item) {
        if (size == items.length) {
            resize(2 * items.length);
        }
        items[nextLast] = item;
        nextLast += 1;
        size += 1;
        if (nextLast == items.length) nextLast = 0;
    }
    public boolean isEmpty() {
        return (size == 0);
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
        if (size == 0) return null;
        T returnItem;
        if (nextFirst + 1 < items.length) {
            returnItem = items[nextFirst + 1];
        } else {
            returnItem = items[nextFirst + 1 - items.length];
        }
        nextFirst += 1;
        if (nextFirst == items.length) nextFirst = 0;
        size -= 1;
        if (items.length >= 16 && ((double) size / items.length) < 0.25) {
            resize (items.length / 2);
        }
        return returnItem;
    }
    public T removeLast() {
        if (size == 0) return null;
        T returnItem;
        if (nextLast - 1 < 0) {
            returnItem = items[nextLast - 1 + items.length];
        } else {
            returnItem = items[nextLast - 1];
        }
        nextLast -= 1;
        if (nextLast < 0) nextFirst = items.length - 1;
        size -= 1;
        if (items.length >= 16 && ((double) size / items.length) < 0.25) {
            resize (items.length / 2);
        }
        return returnItem;
    }
    public T get(int index) {
        if (index < 0) return null;
        int k = nextFirst;
        while (index != 0) {
            index -= 1;
            k += 1;
            if (k == items.length) k = 0;
        }
        if (k + 1 < items.length) return items[k + 1];
        else return items[k + 1 - items.length];
    }
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof ArrayDeque)) return false;
        ArrayDeque<T> other = (ArrayDeque<T>) o;
        if (this.size != other.size) return false;
        Iterator<T> thisIterator = this.iterator();
        Iterator<T> otherIterator = other.iterator();
        while (thisIterator.hasNext()) {
            if (thisIterator.next() != otherIterator.next()) return false;
        }
        return true;
    }
    public void resize(int capacity) {
            T[] newArray = (T[]) new Object[capacity];
            int i = 0;
            for (T x : this) {
                newArray[i] = x;
                i += 1;
            }
            nextFirst = newArray.length - 1;
            nextLast = i;
            items = newArray;

    }
}

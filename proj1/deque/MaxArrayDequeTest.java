package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MaxArrayDequeTest {
    private static class IntComparator implements Comparator<Integer> {
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }

    private static class ReverseIntComparator implements Comparator<Integer> {
        public int compare(Integer a, Integer b) {
            return b - a;
        }
    }

    private static class StringLengthComparator implements Comparator<String> {
        public int compare(String a, String b) {
            return a.length() - b.length();
        }
    }

    @Test
    public void emptyMaxTest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());

        assertNull("max() on an empty deque should return null", mad.max());
        assertNull("max(c) on an empty deque should return null", mad.max(new ReverseIntComparator()));
    }

    @Test
    public void defaultComparatorMaxTest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(3);
        mad.addLast(9);
        mad.addLast(2);
        mad.addLast(7);

        assertEquals("max() should use the deque's default comparator", Integer.valueOf(9), mad.max());
    }

    @Test
    public void customComparatorMaxTest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(3);
        mad.addLast(9);
        mad.addLast(2);
        mad.addLast(7);

        assertEquals("max(c) should use the provided comparator", Integer.valueOf(2),
                mad.max(new ReverseIntComparator()));
    }

    @Test
    public void stringComparatorTest() {
        MaxArrayDeque<String> mad = new MaxArrayDeque<>(new StringLengthComparator());
        mad.addLast("cat");
        mad.addLast("giraffe");
        mad.addLast("bird");

        assertEquals("The longest string should be returned", "giraffe", mad.max());
    }

    @Test
    public void maxAfterRemovalsTest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(5);
        mad.addLast(1);
        mad.addLast(8);
        mad.addLast(4);
        mad.removeLast();
        mad.removeFirst();

        assertEquals("max() should reflect the current contents of the deque", Integer.valueOf(8), mad.max());
    }
}

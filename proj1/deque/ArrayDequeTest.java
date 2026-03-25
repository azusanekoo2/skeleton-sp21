package deque;

import org.junit.Test;

import static org.junit.Assert.*;

/** Performs some basic array deque tests. */
public class ArrayDequeTest {

    @Test
    /** Adds a few things to the deque, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<String>();

        assertTrue("A newly initialized ArrayDeque should be empty", ad1.isEmpty());
        ad1.addFirst("front");

        assertEquals(1, ad1.size());
        assertFalse("ad1 should now contain 1 item", ad1.isEmpty());

        ad1.addLast("middle");
        assertEquals(2, ad1.size());

        ad1.addLast("back");
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
        ad1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that deque is empty afterwards. */
    public void addRemoveTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();

        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());

        ad1.removeFirst();
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create ArrayDeques with different parameterized types. */
    public void multipleParamTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<String>();
        ArrayDeque<Double> ad2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<Boolean>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();
    }

    @Test
    /* Checks if null is returned when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();

        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, ad1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, ad1.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigArrayDequeTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            ad1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) ad1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) ad1.removeLast(), 0.0);
        }
    }

    @Test
    public void getTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addLast(10);
        ad1.addLast(20);
        ad1.addLast(30);

        assertEquals("get(0) should return the first item", Integer.valueOf(10), ad1.get(0));
        assertEquals("get(1) should return the middle item", Integer.valueOf(20), ad1.get(1));
        assertEquals("get(2) should return the last item", Integer.valueOf(30), ad1.get(2));
        assertNull("Negative indices should return null", ad1.get(-1));
    }

    @Test
    public void iteratorOrderTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i < 5; i += 1) {
            ad1.addLast(i);
        }

        int expected = 0;
        for (int actual : ad1) {
            assertEquals("Iterator should return items in deque order", expected, actual);
            expected += 1;
        }
        assertEquals("Iterator should have returned every item", 5, expected);
    }

    @Test
    public void equalsTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<>();
        ArrayDeque<String> ad2 = new ArrayDeque<>();

        ad1.addLast("a");
        ad1.addLast("b");
        ad2.addLast("a");
        ad2.addLast("b");

        assertTrue("Deques with the same contents should be equal", ad1.equals(ad2));

        ad2.removeLast();
        ad2.addLast("c");
        assertFalse("Deques with different contents should not be equal", ad1.equals(ad2));
        assertFalse("A deque should not equal null", ad1.equals(null));
    }

    @Test
    public void wrapAroundOrderTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i < 8; i += 1) {
            ad1.addLast(i);
        }
        for (int i = 0; i < 4; i += 1) {
            assertEquals("removeFirst should preserve order before wraparound",
                    Integer.valueOf(i), ad1.removeFirst());
        }
        for (int i = 8; i < 12; i += 1) {
            ad1.addLast(i);
        }

        for (int i = 4; i < 12; i += 1) {
            assertEquals("Deque order should remain correct after wraparound",
                    Integer.valueOf(i), ad1.removeFirst());
        }
        assertTrue("Deque should be empty after removing everything", ad1.isEmpty());
    }

    @Test
    public void resizePreservesOrderTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i < 32; i += 1) {
            ad1.addLast(i);
        }

        for (int i = 0; i < 32; i += 1) {
            assertEquals("Order should remain correct after growing the backing array",
                    Integer.valueOf(i), ad1.removeFirst());
        }
        assertTrue("Deque should be empty after all removals", ad1.isEmpty());
    }
}

package deque;

import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {



        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

		assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
		lld1.addFirst("front");

		// The && operator is the same as "and" in Python.
		// It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

		lld1.addLast("middle");
		assertEquals(2, lld1.size());

		lld1.addLast("back");
		assertEquals(3, lld1.size());

		System.out.println("Printing out deque: ");
		lld1.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {



        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
		// should be empty
		assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

		lld1.addFirst(10);
		// should not be empty
		assertFalse("lld1 should contain 1 item", lld1.isEmpty());

		lld1.removeFirst();
		// should be empty
		assertTrue("lld1 should be empty after removal", lld1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {



        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {


        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {



        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());


    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {



        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }


    }

    @Test
    public void getTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addLast(10);
        lld1.addLast(20);
        lld1.addLast(30);

        assertEquals("get(0) should return the first item", Integer.valueOf(10), lld1.get(0));
        assertEquals("get(1) should return the middle item", Integer.valueOf(20), lld1.get(1));
        assertEquals("get(2) should return the last item", Integer.valueOf(30), lld1.get(2));
        assertNull("Negative indices should return null", lld1.get(-1));
        assertNull("Out of bounds indices should return null", lld1.get(3));
    }

    @Test
    public void iteratorOrderTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        for (int i = 0; i < 5; i += 1) {
            lld1.addLast(i);
        }

        int expected = 0;
        for (int actual : lld1) {
            assertEquals("Iterator should return items in deque order", expected, actual);
            expected += 1;
        }
        assertEquals("Iterator should have returned every item", 5, expected);
    }

    @Test
    public void equalsTest() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<>();
        LinkedListDeque<String> lld2 = new LinkedListDeque<>();

        lld1.addLast("a");
        lld1.addLast("b");
        lld2.addLast("a");
        lld2.addLast("b");

        assertTrue("Deques with the same contents should be equal", lld1.equals(lld2));

        lld2.removeLast();
        lld2.addLast("c");
        assertFalse("Deques with different contents should not be equal", lld1.equals(lld2));
        assertFalse("A deque should not equal null", lld1.equals(null));
    }

    @Test
    public void mixedAddRemoveOrderTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(2);
        lld1.addFirst(1);
        lld1.addLast(3);
        lld1.addLast(4);

        assertEquals("First removal should return the front item", Integer.valueOf(1), lld1.removeFirst());
        assertEquals("Last removal should return the back item", Integer.valueOf(4), lld1.removeLast());
        assertEquals("Remaining front item should be 2", Integer.valueOf(2), lld1.removeFirst());
        assertEquals("Remaining back item should be 3", Integer.valueOf(3), lld1.removeLast());
        assertTrue("Deque should be empty after removing everything", lld1.isEmpty());
    }
}

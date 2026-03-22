package flik;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestFlik {
    @Test
    public void testIsSameNumberSmall() {
        assertTrue(Flik.isSameNumber(1, 1));
        assertTrue(Flik.isSameNumber(10, 10));

    }

    @Test
    public void testIsSameNumberLarge() {
        assertTrue("128 should equal 128", Flik.isSameNumber(128, 128));
        assertTrue("129 should equal 129", Flik.isSameNumber(129, 129));
        assertTrue("200 should equal 200", Flik.isSameNumber(200, 200));
    }
}

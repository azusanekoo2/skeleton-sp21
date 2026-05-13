package bstmap;

import java.util.HashMap;
import java.util.TreeMap;

public class AutoInsertInOrderSpeedTest {
    private static final int[] INPUT_SIZES = {1000, 5000, 10000, 20000};

    public static void main(String[] args) {
        System.out.println("Automatic in-order insertion speed test");
        for (int n : INPUT_SIZES) {
            System.out.println();
            System.out.println("N = " + n);
            InsertInOrderSpeedTest.timeInOrderMap61B(new ULLMap<>(), n);
            InsertInOrderSpeedTest.timeInOrderMap61B(new BSTMap<>(), n);
            InsertInOrderSpeedTest.timeInOrderTreeMap(new TreeMap<>(), n);
            InsertInOrderSpeedTest.timeInOrderHashMap(new HashMap<>(), n);
        }
    }
}

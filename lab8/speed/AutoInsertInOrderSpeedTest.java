package speed;

import hashmap.MyHashMap;
import hashmap.ULLMap;

import java.util.HashMap;

public class AutoInsertInOrderSpeedTest {
    private static final int[] INPUT_SIZES = {1000, 5000, 10000, 20000, 50000};

    public static void main(String[] args) {
        System.out.println("Automatic in-order insertion speed test");
        for (int n : INPUT_SIZES) {
            System.out.println();
            System.out.println("N = " + n);
            InsertInOrderSpeedTest.timeInOrderMap61B(new ULLMap<>(), n);
            InsertInOrderSpeedTest.timeInOrderMap61B(new MyHashMap<>(), n);
            InsertInOrderSpeedTest.timeInOrderHashMap(new HashMap<>(), n);
        }
    }
}

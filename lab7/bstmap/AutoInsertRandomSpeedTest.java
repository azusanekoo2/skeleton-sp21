package bstmap;

import java.util.HashMap;
import java.util.TreeMap;

public class AutoInsertRandomSpeedTest {
    private static final int STRING_LENGTH = 10;
    private static final int[] INPUT_SIZES = {1000, 5000, 10000, 20000};

    public static void main(String[] args) {
        System.out.println("Automatic random insertion speed test");
        System.out.println("String length: " + STRING_LENGTH);
        for (int n : INPUT_SIZES) {
            System.out.println();
            System.out.println("N = " + n);
            StringUtils.setSeed(61);
            InsertRandomSpeedTest.timeRandomMap61B(new ULLMap<>(), n, STRING_LENGTH);
            StringUtils.setSeed(61);
            InsertRandomSpeedTest.timeRandomMap61B(new BSTMap<>(), n, STRING_LENGTH);
            StringUtils.setSeed(61);
            InsertRandomSpeedTest.timeRandomTreeMap(new TreeMap<>(), n, STRING_LENGTH);
            StringUtils.setSeed(61);
            InsertRandomSpeedTest.timeRandomHashMap(new HashMap<>(), n, STRING_LENGTH);
        }
    }
}

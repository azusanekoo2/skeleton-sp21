package speed;

import hashmap.MyHashMapALBuckets;
import hashmap.MyHashMapHSBuckets;
import hashmap.MyHashMapLLBuckets;
import hashmap.MyHashMapPQBuckets;
import hashmap.MyHashMapTSBuckets;

public class AutoBucketsSpeedTest {
    private static final int STRING_LENGTH = 10;
    private static final int[] INPUT_SIZES = {1000, 5000, 10000, 20000, 50000};

    public static void main(String[] args) {
        System.out.println("Automatic bucket implementation speed test");
        System.out.println("String length: " + STRING_LENGTH);
        for (int n : INPUT_SIZES) {
            System.out.println();
            System.out.println("N = " + n);
            StringUtils.setSeed(61);
            BucketsSpeedTest.timeRandomMap61B(new MyHashMapALBuckets<>(), n, STRING_LENGTH);
            StringUtils.setSeed(61);
            BucketsSpeedTest.timeRandomMap61B(new MyHashMapLLBuckets<>(), n, STRING_LENGTH);
            StringUtils.setSeed(61);
            BucketsSpeedTest.timeRandomMap61B(new MyHashMapTSBuckets<>(), n, STRING_LENGTH);
            StringUtils.setSeed(61);
            BucketsSpeedTest.timeRandomMap61B(new MyHashMapHSBuckets<>(), n, STRING_LENGTH);
            StringUtils.setSeed(61);
            BucketsSpeedTest.timeRandomMap61B(new MyHashMapPQBuckets<>(), n, STRING_LENGTH);
        }
    }
}

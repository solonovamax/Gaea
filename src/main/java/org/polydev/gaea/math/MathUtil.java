package org.polydev.gaea.math;

import java.util.Random;

/**
 * Utility class for mathematical functions.
 */
public class MathUtil {
    /**
     * Gets the standard deviation of an array of doubles.
     *
     * @param numArray The array of numbers to calculate the standard deviation of.
     * @return double - The standard deviation.
     */
    public static double standardDeviation(double[] numArray) {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum / length;

        for(double num : numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation / length);
    }

    /**
     * Gets the carver seed for a chunk.
     *
     * @param chunkX Chunk's X coordinate
     * @param chunkZ Chunk's Z coordinate
     * @param seed   World seed
     * @return long - The carver seed.
     */
    public static long getCarverChunkSeed(int chunkX, int chunkZ, long seed) {
        Random r = new Random(seed);
        return chunkX * r.nextLong() ^ chunkZ * r.nextLong() ^ seed;
    }

    public static long hashToLong(String s) {
        if(s == null) {
            return 0;
        }
        long hash = 0;
        for(char c : s.toCharArray()) {
            hash = 31L * hash + c;
        }
        return hash;
    }
}

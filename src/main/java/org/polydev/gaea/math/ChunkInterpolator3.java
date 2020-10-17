package org.polydev.gaea.math;

import org.bukkit.World;
import org.polydev.gaea.biome.BiomeGrid;
import org.polydev.gaea.biome.Generator;
import org.polydev.gaea.generation.GenerationPhase;

/**
 * Class to abstract away the 16 Interpolators needed to generate a chunk.<br>
 * Contains method to get interpolated noise at a coordinate within the chunk.
 */
public class ChunkInterpolator3 implements ChunkInterpolator {
    private final Interpolator3[][][] interpGrid = new Interpolator3[4][64][4];
    private final BiomeGrid grid;
    private final FastNoiseLite noise;
    private final int xOrigin;
    private final int zOrigin;
    private final World w;

    /**
     * Instantiates a 3D ChunkInterpolator at a pair of chunk coordinates, with a BiomeGrid and FastNoiseLite instance.
     *
     * @param chunkX X coordinate of the chunk.
     * @param chunkZ Z coordinate of the chunk.
     * @param grid   BiomeGrid to use for noise fetching.
     * @param noise  FastNoiseLite instance to use.
     */
    public ChunkInterpolator3(World w, int chunkX, int chunkZ, BiomeGrid grid, FastNoiseLite noise) {
        this.xOrigin = chunkX << 4;
        this.zOrigin = chunkZ << 4;
        this.grid = grid;
        this.noise = noise;
        this.w = w;
        Generator[][] gridTemp = new Generator[8][8];


        for(int x = - 2; x < 6; x++) {
            for(int z = - 2; z < 6; z++) {
                gridTemp[x + 2][z + 2] = grid.getBiome(xOrigin + x * 4, zOrigin + z * 4, GenerationPhase.BASE).getGenerator();
            }
        }

        double[][][] stor = storeNoise(gridTemp);

        for(byte x = 0; x < 4; x++) {
            for(byte z = 0; z < 4; z++) {
                for(int y = 0; y < 64; y++) {
                    interpGrid[x][y][z] = new Interpolator3(
                            biomeAvg(x, y, z, stor),
                            biomeAvg(x + 1, y, z, stor),
                            biomeAvg(x, y + 1, z, stor),
                            biomeAvg(x + 1, y + 1, z, stor),
                            biomeAvg(x, y, z + 1, stor),
                            biomeAvg(x + 1, y, z + 1, stor),
                            biomeAvg(x, y + 1, z + 1, stor),
                            biomeAvg(x + 1, y + 1, z + 1, stor));
                }
            }
        }
    }

    private double[][][] storeNoise(Generator[][] gens) {
        double[][][] noiseStorage = new double[8][8][65];
        for(byte x = - 2; x < 6; x++) {
            for(byte z = - 2; z < 6; z++) {
                for(int y = 0; y < 64; y++) {
                    noiseStorage[x + 2][z + 2][y] = gens[x + 2][z + 2].getNoise(noise, w, x * 4 + xOrigin, y * 4, z * 4 + zOrigin);
                }
            }
        }
        return noiseStorage;
    }

    private double biomeAvg(int x, int y, int z, double[][][] noise) {
        return (noise[x + 3][z + 2][y]
                + noise[x + 1][z + 2][y]
                + noise[x + 2][z + 3][y]
                + noise[x + 2][z + 1][y]) / 4D;
    }

    @Override
    public double getNoise(double x, double z) {
        return getNoise(x, 0, z);
    }

    /**
     * Gets the noise at a pair of internal chunk coordinates.
     *
     * @param x The internal X coordinate (0-15).
     * @param z The internal Z coordinate (0-15).
     * @return double - The interpolated noise at the coordinates.
     */
    @Override
    public double getNoise(double x, double y, double z) {
        return interpGrid[((int) x) / 4][((int) y) / 4][((int) z) / 4].trilerp((float) (x % 4) / 4, (float) (y % 4) / 4, (float) (z % 4) / 4);
    }

    private static class CoordinatePair {
        private final int x;
        private final int z;

        public CoordinatePair(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }


        @Override
        public boolean equals(Object obj) {
            if(! (obj instanceof CoordinatePair)) return false;
            CoordinatePair other = (CoordinatePair) obj;
            return this.x == other.getX() && this.z == other.getZ();
        }

        @Override
        public String toString() {
            return x + ":" + z;
        }
    }
}

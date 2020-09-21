package org.polydev.gaea.math;

import org.polydev.gaea.biome.BiomeGrid;
import org.polydev.gaea.biome.BiomeTerrain;

/**
 * Class to abstract away the 16 Interpolators needed to generate a chunk.<br>
 *     Contains method to get interpolated noise at a coordinate within the chunk.
 */
public class ChunkInterpolator2 implements ChunkInterpolator {
    private final Interpolator[][] interpGrid = new Interpolator[4][4];

    private final int xOrigin;
    private final int zOrigin;
    private final FastNoise noise;
    /**
     * Instantiates a ChunkInterpolator at a pair of chunk coordinates, with a BiomeGrid and FastNoise instance.
     * @param chunkX X coordinate of the chunk.
     * @param chunkZ Z coordinate of the chunk.
     * @param grid BiomeGrid to use for noise fetching.
     * @param noise FastNoise instance to use.
     */
    public ChunkInterpolator2(int chunkX, int chunkZ, BiomeGrid grid, FastNoise noise) {
        this.xOrigin = chunkX << 4;
        this.zOrigin = chunkZ << 4;
        this.noise = noise;
        BiomeTerrain[][] gridTemp = new BiomeTerrain[8][8];
        for(int x = -2; x < 6; x++) {
            for(int z = -2; z < 6; z++) {
                gridTemp[x+2][z+2] = grid.getBiome(xOrigin + x * 4, zOrigin + z * 4).getGenerator();
            }
        }
        for(byte x = 0; x < 4; x++) {
            for(byte z = 0; z < 4; z++) {
                interpGrid[x][z] = new Interpolator(biomeAvg(x, z, gridTemp) * 2.0f,
                        biomeAvg(x + 1, z, gridTemp) * 2.0f,
                        biomeAvg(x, z + 1, gridTemp) * 2.0f,
                        biomeAvg(x + 1, z + 1, gridTemp) * 2.0f);
            }
        }
    }

    private double biomeAvg(int x, int z, BiomeTerrain[][] g) {
        return (g[x+3][z+2].getNoise(noise, x*4+xOrigin, z*4+zOrigin)
                + g[x+1][z+2].getNoise(noise, x*4+xOrigin, z*4+zOrigin)
                + g[x+2][z+3].getNoise(noise, x*4+xOrigin, z*4+zOrigin)
                + g[x+2][z+1].getNoise(noise, x*4+xOrigin, z*4+zOrigin))/4D;
    }

    /**
     * Gets the noise at a pair of internal chunk coordinates.
     * @param x The internal X coordinate (0-15).
     * @param z The internal Z coordinate (0-15).
     * @return double - The interpolated noise at the coordinates.
     */
    public double getNoise(byte x, byte z) {
        return interpGrid[x / 4][z / 4].bilerp((float) (x % 4) / 4, (float) (z % 4) / 4);
    }

    @Override
    public double getNoise(byte x, int y, byte z) {
        return getNoise(x, z);
    }
}
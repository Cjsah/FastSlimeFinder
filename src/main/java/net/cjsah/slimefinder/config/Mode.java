package net.cjsah.slimefinder.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiPredicate;

@RequiredArgsConstructor
@Getter
public enum Mode {
    NORMAL(Mode::isCircleContainsChunk, (x, z) -> x == 8 && z == 8),
    COVER(Mode::isCircleCoverChunk, (x, z) -> x >= 7 && x <= 9 && z >= 7 && z <= 9)
    ;

    private final String name = this.name().toLowerCase();
    private final CoverPredicate isCovered;
    private final BiPredicate<Integer, Integer> isCenter;

    public boolean isCovered(int cx, int cz, int chunkX, int chunkZ) {
        return this.isCovered.test(cx, cz, chunkX, chunkZ);
    }

    public boolean isCenter(int x, int z) {
        return this.isCenter.test(x, z);
    }

    private static boolean isCircleContainsChunk(int cx, int cz, int chunkX, int chunkZ) {
        int dx = cx - Math.clamp(cx, chunkX * 16, chunkX * 16 + 15);
        int dz = cz - Math.clamp(cz, chunkZ * 16, chunkZ * 16 + 15);
        return dx * dx + dz * dz <= 16384;
    }

    private static boolean isCircleCoverChunk(int cx, int cz, int chunkX, int chunkZ) {
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        int maxX = minX + 15;
        int maxZ = minZ + 15;

        long dx1 = (long) minX - cx;
        long dz1 = (long) minZ - cz;
        long dx2 = (long) maxX - cx;
        long dz2 = (long) maxZ - cz;

        return dx1 * dx1 + dz1 * dz1 <= 16384
            && dx1 * dx1 + dz2 * dz2 <= 16384
            && dx2 * dx2 + dz1 * dz1 <= 16384
            && dx2 * dx2 + dz2 * dz2 <= 16384;
    }

    public interface CoverPredicate {
        boolean test(int px, int pz, int chunkX, int chunkZ);
    }

}

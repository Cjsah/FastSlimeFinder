package net.cjsah.slimefinder.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cjsah.slimefinder.data.ChunkInfo;
import net.cjsah.slimefinder.data.Position;

import java.util.function.BiPredicate;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Getter
public enum Mode {
    NORMAL(Mode::isCircleContainsChunk, Mode::normalBlockCount, (x, z) -> x == 8 && z == 8),
    COVER(Mode::isCircleCoverChunk, Mode::coverBlockCount, (x, z) -> x >= 7 && x <= 9 && z >= 7 && z <= 9)
    ;

    private final String name = this.name().toLowerCase();
    private final CoverPredicate isCovered;
    private final CoverBlockCounter blockCounter;
    private final BiPredicate<Integer, Integer> isCenter;

    public boolean isCovered(int cx, int cz, int chunkX, int chunkZ) {
        return this.isCovered.test(cx, cz, chunkX, chunkZ);
    }

    public int calcCoverBlockCount(Position offset, ChunkInfo center, ChunkInfo chunk) {
        return this.blockCounter.count(offset, center, chunk);
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

    private static int normalBlockCount(Position offset, ChunkInfo center, ChunkInfo chunk) {
        return counterCoveredBlock(offset, center, chunk, false);
    }

    private static int coverBlockCount(Position offset, ChunkInfo center, ChunkInfo chunk) {
        int ox = center.getX() - chunk.getX();
        int oz = center.getZ() - chunk.getZ();
        if (ox >= -1 && ox <= 1 && oz >= -1 && oz <= 1) {
            return counterCoveredBlock(offset, center, chunk, true);
        }
        return 256;
    }

    private static int counterCoveredBlock(Position offset, ChunkInfo center, ChunkInfo chunk, boolean passMax) {
        int cx = center.getX() * 16 + offset.x();
        int cz = center.getZ() * 16 + offset.z();
        int startX = chunk.getX() * 16;
        int startZ = chunk.getZ() * 16;

        return (int) IntStream.range(0, 256).boxed().parallel().map(it -> {
            int px = (it >> 4) + startX - cx;
            int pz = (it & 16) + startZ - cz;
            int distance = px * px + pz * pz;
            return distance > 576 && (passMax || distance <= 16384);
        }).filter(it -> it).count();
    }

    public interface CoverPredicate {
        boolean test(int px, int pz, int chunkX, int chunkZ);
    }

    public interface CoverBlockCounter {
        int count(Position offset, ChunkInfo center, ChunkInfo chunk);
    }

}

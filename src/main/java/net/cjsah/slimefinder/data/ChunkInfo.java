package net.cjsah.slimefinder.data;

import lombok.Data;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class ChunkInfo {
    private final int x;
    private final int z;
    private final AtomicInteger counter = new AtomicInteger(0);
    private boolean isSlimeChunk = false;

    // 来自https://minecraft.wiki/w/Slime
    public boolean updateIsSlimeChunk(long seed, int startX, int startZ) {
        int x = this.x + startX;
        int z = this.z + startZ;
        Random rng = new Random();
        //noinspection IntegerMultiplicationImplicitCastToLong
        rng.setSeed(seed +
            (long) (x * x * 4987142) +
            (long) (x * 5947611) +
            (long) (z * z) * 4392871L +
            (long) (z * 389711) ^ 987234911L
        );
        return (this.isSlimeChunk = rng.nextInt(10) == 0);
    }

    public void near() {
        this.counter.incrementAndGet();
    }

    public int getCount() {
        return this.counter.get();
    }
}

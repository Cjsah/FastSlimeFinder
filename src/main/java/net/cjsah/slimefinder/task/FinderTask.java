package net.cjsah.slimefinder.task;

import net.cjsah.slimefinder.CLI;
import net.cjsah.slimefinder.config.Config;
import net.cjsah.slimefinder.data.ChunkInfo;
import net.cjsah.slimefinder.data.Position;
import net.cjsah.slimefinder.util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class FinderTask extends TimerTask {
    private final long seed;

    private final Position center;
    private final Position offset;
    private final int radius;
    private final int record;
    private final int total;
    private final AtomicInteger process = new AtomicInteger(0);
    private final int[][] coverCounter;

    public FinderTask(Config config, long seed) {
        this.seed = seed;
        this.center = config.getCenter();
        this.offset = config.getOffset();
        this.radius = config.getRadius();
        this.record = config.getRecord();
        int length = this.radius * 2 + 1;
        this.total = length * length;
        this.coverCounter = this.preCalcCoverCount();
    }

    public String getProcess() {
        int process = this.process.get();
        return "%.2f%% (%d/%d) %s".formatted(
            process * 100.0F / this.total,
            process,
            this.total,
            this.formatDuration()
        );
    }

    private int[][] preCalcCoverCount() {
        CLI.log("正在预计算挂机点覆盖的史莱姆区块数量");
        int[][] counter = new int[17][17];
        int cx = this.offset.x();
        int cz = this.offset.z();
        for (int x = 0; x < 17; x++) {
            for (int z = 0; z < 17; z++) {
                int chunkX = x - 8;
                int chunkZ = z - 8;
                int startX = chunkX * 16;
                int startZ = chunkZ * 16;

                counter[x][z] = (int) IntStream.range(0, 256).boxed().parallel().filter(it -> {
                    int px = (it >> 4) + startX - cx;
                    int pz = (it & 15) + startZ - cz;
                    int distance = px * px + pz * pz;
                    return distance > 576 && distance <= 16384;
                }).count();
            }
        }
        return counter;
    }

    @Override
    public void start() {
        int startX = this.center.x() - this.radius;
        int startZ = this.center.z() - this.radius;
        int length = this.radius * 2 + 1;

        ChunkInfo[] chunks = new ChunkInfo[length * length];
        for (int x = 0; x < length; x++) {
            for (int z = 0; z < length; z++) {
                chunks[x * length + z] = new ChunkInfo(x, z);
            }
        }

        List<ChunkInfo> founded = Arrays.stream(chunks).parallel()
            .peek(chunk -> {
                if (chunk.updateIsSlimeChunk(this.seed, startX, startZ)) {
                    for (int x = 0; x < 17; x++) {
                        for (int z = 0; z < 17; z++) {
                            int chunkX = chunk.getX() + x - 8;
                            int chunkZ = chunk.getZ() + z - 8;

                            int index = chunkX * length + chunkZ;
                            if (index < 0 || index >= chunks.length) continue;

                            int count = this.coverCounter[16 - x][16 - z];
                            if (count > 0) {
                                chunks[index].near(count);
                            }
                        }
                    }
                }
                this.process.incrementAndGet();
            })
            .sorted(Comparator.comparingInt(ChunkInfo::getBlockCounter).reversed())
            .limit(this.record)
            .toList();

        CLI.completedSearch(this);
        CLI.log("搜索完成，记录前 %d 个合适的史莱姆区块. 耗时: %s".formatted(founded.size(), this.formatDuration()));
        for (ChunkInfo info : founded) {
            int cx = info.getX() + startX;
            int cz = info.getZ() + startZ;
            int px = cx * 16 + this.offset.x();
            int pz = cz * 16 + this.offset.z();
            CLI.log("Pos:[x=%d, z=%d] Chunk:[x=%d, z=%d] 共%d个史莱姆区块, %d个方块".formatted(px, pz, cx, cz, info.getChunkCounter(), info.getBlockCounter()));
        }

        CLI.log("正在生成图片...");

        for (ChunkInfo info : founded) {
            int cx = info.getX() + startX;
            int cz = info.getZ() + startZ;
            int px = cx * 16 + this.offset.x();
            int pz = cz * 16 + this.offset.z();

            try {
                BufferedImage image = ImageUtil.drawImage(chunks, info, length, this.offset);
                File file = new File("./images/%d_%d_(%d_%d)[%d_%d].png".formatted(info.getBlockCounter(), info.getChunkCounter(), px, pz, cx, cz));
                file.getParentFile().mkdirs();
                ImageIO.write(image, "png", file);
            } catch (Exception e) {
                CLI.log("保存图片文件失败: %s".formatted(e.getMessage()));
            }
        }

    }
}

package net.cjsah.slimefinder.task;

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

public class FinderTask extends TimerTask {
    private final long seed;

    private final Position center;
    private final Position offset;
    private final int radius;
    private final int record;

    public FinderTask(Config config, long seed) {
        this.seed = seed;
        this.center = config.getCenter();
        this.offset = config.getOffset();
        this.radius = config.getRadius();
        this.record = config.getRecord();
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
                if (!chunk.updateIsSlimeChunk(this.seed, startX, startZ)) return;
                int cx = chunk.getX() * 16 + this.offset.x();
                int cz = chunk.getZ() * 16 + this.offset.z();
                for (int x = 0; x < 17; x++) {
                    for (int z = 0; z < 17; z++) {
                        if (x == 0 && z == 0) continue;
                        int chunkX = chunk.getX() + x - 8;
                        int chunkZ = chunk.getZ() + z - 8;

                        int index = chunkX * length + chunkZ;
                        if (index < 0 || index >= chunks.length) continue;

                        int dx = cx - Math.clamp(cx, chunkX * 16, chunkX * 16 + 15);
                        int dz = cz - Math.clamp(cz, chunkZ * 16, chunkZ * 16 + 15);
                        if (dx * dx + dz * dz <= 16384) {
                            chunks[index].near();
                        }
                    }
                }
            })
            .sorted(Comparator.comparingInt(ChunkInfo::getCount).reversed())
            .limit(this.record)
            .toList();

        this.paused();
        System.out.printf("搜索完成，共找到 %d 个合适的史莱姆区块. 耗时: %s%n", founded.size(), this.formatDuration());
        System.out.println("正在生成图片...");

        for (ChunkInfo info : founded) {
            int cx = info.getX() + startX;
            int cz = info.getZ() + startZ;
            int px = cx * 16 + this.offset.x();
            int pz = cz * 16 + this.offset.z();
            System.out.printf("Pos:[x=%d, z=%d] Chunk:[x=%d, z=%d] 共%d个史莱姆区块%n", px, pz, cx, cz, info.getCount());

            try {
                BufferedImage image = ImageUtil.drawImage(chunks, info, length, this.offset);
                File file = new File("./images/(%d_%d)[%d_%d]_%d.png".formatted(px, pz, cx, cz, info.getCount()));
                file.getParentFile().mkdirs();
                ImageIO.write(image, "png", file);
            } catch (Exception e) {
                System.out.printf("保存图片文件失败: %s%n", e.getMessage());
            }
        }

    }

}

package net.cjsah.slimefinder.util;

import net.cjsah.slimefinder.config.Mode;
import net.cjsah.slimefinder.data.ChunkInfo;
import net.cjsah.slimefinder.data.Position;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class ImageUtil {

    public static BufferedImage drawImage(ChunkInfo[] chunks, ChunkInfo info, int length, Position offset) {
        int cx = 128 + offset.x();
        int cz = 128 + offset.z();

        BufferedImage image = new BufferedImage(290, 290, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        for (int x = 0; x < 17; x++) {
            for (int z = 0; z < 17; z++) {
                int chunkX = info.getX() + x - 8;
                int chunkZ = info.getZ() + z - 8;
                int index = chunkX * length + chunkZ;
                boolean isSlimeChunk = index >= 0 && index < chunks.length && chunks[index].isSlimeChunk();
                Color color = isSlimeChunk ? Color.GREEN : Color.WHITE;

                if (Mode.NORMAL.isCenter(x, z) || !Mode.NORMAL.isCovered(cx, cz, x, z)) {
                    color = getMarkColor(color);
                }

                graphics.setColor(Color.BLACK);
                graphics.drawLine(x * 17, z * 17, x * 17 + 16, z * 17);
                graphics.drawLine(x * 17, z * 17, x * 17, z * 17 + 16);

                graphics.setColor(color);
                graphics.fillRect(x * 17 + 1, z * 17 + 1, 16, 16);

            }
        }

        // image abs center
        cx += 9;
        cz += 9;

        Graphics2D gMark = (Graphics2D) graphics.create();

        gMark.setColor(new Color(0, 0, 0, 128));
        Area cover = new Area(new Ellipse2D.Double(cx - 210, cz - 210, 420, 420));
        Area inner = new Area(new Ellipse2D.Double(cx - 136, cz - 136, 272, 272));
        Area center = new Area(new Ellipse2D.Double(cx - 26, cz - 26, 52, 52));
        cover.subtract(inner);
        cover.add(center);
        gMark.fill(cover);
        gMark.dispose();

        graphics.setColor(Color.RED);
        graphics.drawLine(cx - 3, cz, cx + 3, cz);
        graphics.drawLine(cx, cz - 3, cx, cz + 3);

        graphics.dispose();
        return image;
    }

    private static Color getMarkColor(Color color) {
        int red = (int) ((float) color.getRed() * 0.55f);
        int green = (int) ((float) color.getGreen() * 0.55f);
        int blue = (int) ((float) color.getBlue() * 0.55f);
        return new Color(red, green, blue);
    }

}

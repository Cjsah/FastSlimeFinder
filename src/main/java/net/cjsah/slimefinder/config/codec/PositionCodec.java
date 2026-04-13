package net.cjsah.slimefinder.config.codec;

import net.cjsah.slimefinder.CLI;
import net.cjsah.slimefinder.config.Config;
import net.cjsah.slimefinder.data.Position;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PositionCodec extends Codec<Position> {

    public PositionCodec(String key, Function<Config, Position> encoder, BiConsumer<Config, Position> decoder) {
        super(key, encoder, decoder);
    }

    @Override
    protected String serialize(Position value) {
        return value.toString();
    }

    @Override
    protected Position deserialize(String value) {
        if (value == null || value.isBlank()) {
            CLI.log("坐标值无效, 必须是 x,z 格式");
            return null;
        }

        String[] parts = value.split(",");
        if (parts.length != 2) {
            CLI.log("坐标值无效, 必须是 x,z 格式");
            return null;
        }

        try {
            int x = Integer.parseInt(parts[0].trim());
            int z = Integer.parseInt(parts[1].trim());
            return new Position(x, z);
        } catch (NumberFormatException e) {
            CLI.log("坐标值无效, 必须是 x,z 格式");
            return null;
        }
    }
}

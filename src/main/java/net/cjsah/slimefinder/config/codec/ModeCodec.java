package net.cjsah.slimefinder.config.codec;

import net.cjsah.slimefinder.config.Config;
import net.cjsah.slimefinder.config.Mode;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ModeCodec extends Codec<Mode> {

    public ModeCodec(String key, Function<Config, Mode> encoder, BiConsumer<Config, Mode> decoder) {
        super(key, encoder, decoder);
    }

    @Override
    protected String serialize(Mode value) {
        return value.getName();
    }

    @Override
    protected Mode deserialize(String value) {
        if (value == null || value.isBlank()) {
            System.out.println("坐标值无效, 必须是 x,z 格式");
            return null;
        }

        String name = value.trim().toLowerCase();

        for (Mode mode : Mode.values()) {
            if (mode.getName().equals(name)) return mode;
        }

        System.out.println("mode 值无效, 使用默认值");
        return null;
    }
}

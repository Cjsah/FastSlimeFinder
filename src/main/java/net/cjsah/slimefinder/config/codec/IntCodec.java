package net.cjsah.slimefinder.config.codec;

import net.cjsah.slimefinder.CLI;
import net.cjsah.slimefinder.config.Config;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class IntCodec extends Codec<Integer> {

    public IntCodec(String key, Function<Config, Integer> encoder, BiConsumer<Config, Integer> decoder) {
        super(key, encoder, decoder);
    }

    @Override
    protected String serialize(Integer value) {
        return String.valueOf(value);
    }

    @Override
    protected Integer deserialize(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            CLI.log("配置文件中的 " + this.key + " 的值无效，必须是一个整数。");
            return null;
        }
    }
}

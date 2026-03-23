package net.cjsah.slimefinder.config.codec;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cjsah.slimefinder.config.Config;

import java.util.function.BiConsumer;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class Codec<T> {
    @Getter
    protected final String key;
    private final Function<Config, T> encoder;
    private final BiConsumer<Config, T> decoder;

    protected abstract String serialize(T value);

    protected abstract T deserialize(String value);

    public String encode(Config config) {
        return this.serialize(this.encoder.apply(config));
    }

    public void decode(Config config, String value) {
        if (value == null) return;
        T deserialized = this.deserialize(value);
        if (deserialized == null) return;
        this.decoder.accept(config, deserialized);
    }
}

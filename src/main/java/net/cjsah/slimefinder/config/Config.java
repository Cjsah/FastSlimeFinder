package net.cjsah.slimefinder.config;

import lombok.Data;
import net.cjsah.slimefinder.config.codec.Codec;
import net.cjsah.slimefinder.config.codec.IntCodec;
import net.cjsah.slimefinder.config.codec.PositionCodec;
import net.cjsah.slimefinder.data.Position;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Data
public class Config {
    private static final List<Codec<?>> CODECS = new ArrayList<>(10);

    private Position center = Position.ZERO;
    private Position offset = new Position(8, 8);
    private int radius = 128;
    private int record = 20;

    public static Config getOrCreateConfig() throws IOException {
        File file = new File("config.properties");
        Path path = file.toPath();

        Config config = new Config();
        if (Files.isRegularFile(path)) {
            Properties props = new Properties();
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                props.load(reader);
            }

            for (Codec<?> codec : CODECS) {
                String value = props.getProperty(codec.getKey());
                codec.decode(config, value);
            }

            config.print();
            return config;
        }

        file.getAbsoluteFile().getParentFile().mkdirs();

        Properties props = new Properties();
        for (Codec<?> codec : CODECS) {
            props.put(codec.getKey(), codec.encode(config));
        }
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            props.store(writer, null);
        }

        config.print();
        return config;
    }

    public void print() {
        String[][] rows = new String[][]{
            {"center", this.center.toString()},
            {"radius", String.valueOf(this.radius)},
            {"offset", this.offset.toString()},
            {"record", String.valueOf(this.record)}
        };

        int keyWidth = "Field".length();
        int valueWidth = "Value".length();
        for (String[] row : rows) {
            keyWidth = Math.max(keyWidth, row[0].length());
            valueWidth = Math.max(valueWidth, row[1].length());
        }

        String border = "+" + "-".repeat(keyWidth + 2) + "+" + "-".repeat(valueWidth + 2)
            + "+" + "-".repeat(keyWidth + 2) + "+" + "-".repeat(valueWidth + 2) + "+";
        System.out.println(border);
        System.out.printf(
            "| %-" + keyWidth + "s | %-" + valueWidth + "s | %-" + keyWidth + "s | %-" + valueWidth + "s |%n",
            "Field", "Value", "Field", "Value"
        );
        System.out.println(border);
        for (int i = 0; i < rows.length; i += 2) {
            String[] left = rows[i];
            String[] right = i + 1 < rows.length ? rows[i + 1] : new String[]{"", ""};
            System.out.printf(
                "| %-" + keyWidth + "s | %-" + valueWidth + "s | %-" + keyWidth + "s | %-" + valueWidth + "s |%n",
                left[0], left[1], right[0], right[1]
            );
        }
        System.out.println(border);
    }

    static {
        CODECS.add(new PositionCodec("center", Config::getCenter, Config::setCenter));
        CODECS.add(new PositionCodec("offset", Config::getOffset, Config::setOffset));
        CODECS.add(new IntCodec("radius", Config::getRadius, Config::setRadius));
        CODECS.add(new IntCodec("record", Config::getRecord, Config::setRecord));
    }
}

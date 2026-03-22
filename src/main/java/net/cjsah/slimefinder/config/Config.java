package net.cjsah.slimefinder.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONWriter;
import lombok.Data;
import net.cjsah.slimefinder.data.Position;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Data
public class Config {
    private Position center = Position.ZERO;
    private Position offset = new Position(8, 8);
    private int radius = 128;
    private int record = 20;

    public static Config load() throws IOException {
        File configFile = new File("config.json");
        if (!configFile.exists()) {
            Config defaultConfig = new Config();
            String content = JSON.toJSONString(defaultConfig, JSONWriter.Feature.PrettyFormat);
            Files.writeString(configFile.toPath(), content, StandardCharsets.UTF_8);
            return defaultConfig.print();
        }
        String json = Files.readString(new File("config.json").toPath());
        try {
            return JSON.parseObject(json, Config.class).print();
        } catch (JSONException e) {
            System.out.println("配置文件读取失败");
            throw e;
        }
    }

    private static Properties getOrCreateProps(Path path) throws IOException {
        Config config = new Config();
        if (Files.isRegularFile(path)) {
            Properties props = new Properties();
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                props.load(reader);
            }

            props.contains("center");
            props.contains("offset");
            props.contains("radius");
            props.contains("record");

            props.getProperty()


            return props;
        }

        Files.createDirectories(path.getParent());

        Properties props = new Properties();
        props.put("center", config.center.toString());
        props.put("offset", config.offset.toString());
        props.put("radius", config.radius);
        props.put("record", config.record);

        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            props.store(writer, null);
        }

        return config;
    }



    public Config print() {
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
        return this;
    }
}

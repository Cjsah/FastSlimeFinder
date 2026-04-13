package net.cjsah.slimefinder;

import net.cjsah.slimefinder.config.Config;
import net.cjsah.slimefinder.task.FinderTask;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    private static boolean running = true;

    public static void main(String[] args) throws IOException {
        Config config = Config.getOrCreateConfig();

        while (running) {
            CLI.log("###################");
            CLI.log("1.搜索史莱姆区块");
            CLI.log("2.重新加载配置");
            CLI.log("3.退出");
            CLI.log("###################");

            String input = CLI.readLine("> ");
            switch (input) {
                case "1" -> {
                    try {
                        long seed = Long.parseLong(CLI.readLine("请输入地图种子: "));
                        startSearch(config, seed);
                    } catch (NumberFormatException e) {
                        CLI.log("输入的种子无效");
                    }
                }
                case "2" -> {
                    CLI.log("正在重新加载配置...");
                    config = Config.getOrCreateConfig();
                }
                case "3" -> {
                    CLI.log("正在退出...");
                    running = false;
                }
                default -> CLI.log("无效的选项");
            }
        }
    }

    private static void startSearch(Config config, long seed) {
        CLI.log("正在搜索史莱姆区块...");
        FinderTask task = new FinderTask(config, seed);
        Thread taskThread = new Thread(task);
        CLI.initSearch();
        taskThread.start();

        try {
            while (taskThread.isAlive()) {
                TimeUnit.MILLISECONDS.sleep(100);
                CLI.updateProcess(task);
            }
            running = false;
        } catch (InterruptedException ignored) {
        }
    }
}


package net.cjsah.slimefinder;

import net.cjsah.slimefinder.config.Config;
import net.cjsah.slimefinder.task.FinderTask;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    private static boolean running = true;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        Config config = Config.getOrCreateConfig();

        while (running) {
            System.out.println("###################");
            System.out.println("1.搜索史莱姆区块");
            System.out.println("2.重新加载配置");
            System.out.println("3.退出");
            System.out.println("###################");

            String input = readLine("> ");
            switch (input) {
                case "1" -> {
                    try {
                        long seed = Long.parseLong(readLine("请输入地图种子: "));
                        startSearch(config, seed);
                    } catch (NumberFormatException e) {
                        System.out.println("输入的种子无效");
                    }
                }
                case "2" -> {
                    System.out.println("正在重新加载配置...");
                    config = Config.getOrCreateConfig();
                }
                case "3" -> {
                    System.out.println("正在退出...");
                    running = false;
                }
                default -> System.out.println("无效的选项");
            }
        }
    }

    private static void startSearch(Config config, long seed) {
        System.out.println("正在搜索史莱姆区块...");
        Thread taskThread = new Thread(new FinderTask(config, seed));
        taskThread.start();

        try {
            while (taskThread.isAlive()) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            running = false;
        } catch (InterruptedException ignored) {
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

}


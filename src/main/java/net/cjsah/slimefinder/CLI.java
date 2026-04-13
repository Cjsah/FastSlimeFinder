package net.cjsah.slimefinder;

import net.cjsah.slimefinder.task.FinderTask;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class CLI {
    private static final Scanner scanner = new Scanner(System.in);

    private static final AtomicBoolean loggingProcess = new AtomicBoolean(false);
    private static final AtomicBoolean completedSearch = new AtomicBoolean(false);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static void log(String text) {
        if (loggingProcess.get()) {
            text = "\n" + text;
        }
        System.out.println(text);
        loggingProcess.set(false);
    }

    public static void initSearch() {
        completedSearch.set(false);
        loggingProcess.set(true);
    }

    public static void completedSearch(FinderTask task) {
        updateProcess(task);
        completedSearch.set(true);
    }

    public static void updateProcess(FinderTask task) {
        if (completedSearch.get()) {
            return;
        }
        String process = task.getProcess();
        System.out.print("\r" + process);
        loggingProcess.set(true);

    }

}

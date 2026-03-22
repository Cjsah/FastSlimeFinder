package net.cjsah.slimefinder.logger;

import java.util.Scanner;

public class ConsoleIO {
    private static final Scanner Scanner = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return Scanner.nextLine().trim();
    }

}

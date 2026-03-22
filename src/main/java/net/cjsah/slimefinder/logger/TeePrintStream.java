package net.cjsah.slimefinder.logger;

import java.io.PrintStream;

public class TeePrintStream extends PrintStream {

    public TeePrintStream(PrintStream std, PrintStream logger) {
        super(new TeeOutputStream(std, logger), true);
    }
}

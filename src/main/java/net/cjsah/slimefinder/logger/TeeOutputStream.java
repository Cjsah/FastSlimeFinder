package net.cjsah.slimefinder.logger;

import java.io.OutputStream;
import java.io.PrintStream;

public class TeeOutputStream extends OutputStream {
    private final PrintStream std;
    private final PrintStream logger;

    public TeeOutputStream(PrintStream std, PrintStream logger) {
        this.std = std;
        this.logger = logger;
    }

    @Override
    public void write(int b) {
        this.std.write(b);
        this.logger.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        this.std.write(b, off, len);
        this.logger.write(b, off, len);
    }

    @Override
    public void flush() {
        this.std.flush();
        this.logger.flush();
    }

    @Override
    public void close() {
        this.flush();
    }
}

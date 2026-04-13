package net.cjsah.slimefinder.task;

import lombok.Data;
import net.cjsah.slimefinder.CLI;

import java.time.Duration;
import java.time.Instant;

@Data
public abstract class TimerTask implements Runnable {
    private Instant startTime;
    protected Instant endTime;
    protected Duration duration;

    @Override
    public void run() {
        this.startTime = Instant.now();
        this.start();
        this.paused();
        CLI.log("全部任务完成，耗时: %s".formatted(this.formatDuration()));
    }

    public abstract void start();

    protected void paused() {
        this.endTime = Instant.now();
        this.duration = Duration.between(this.startTime, this.endTime);
    }

    public String formatDuration() {
        Instant now = Instant.now();
        Duration duration = Duration.between(this.startTime, now);
        long totalMillis = duration.toMillis();
        long seconds = totalMillis / 1000;
        long millis = totalMillis % 1000;
        return String.format("%d.%03ds", seconds, millis);
    }
}

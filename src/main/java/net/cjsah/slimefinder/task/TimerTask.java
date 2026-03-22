package net.cjsah.slimefinder.task;

import lombok.Data;

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
        System.out.printf("任务完成，耗时: %s", this.formatDuration());
    }

    public abstract void start();

    protected void paused() {
        this.endTime = Instant.now();
        this.duration = Duration.between(this.startTime, this.endTime);
    }

    public String formatDuration() {
        if (this.duration == null) return "";
        long totalMillis = this.duration.toMillis();
        long seconds = totalMillis / 1000;
        long millis = totalMillis % 1000;
        return String.format("%d.%03ds", seconds, millis);
    }
}

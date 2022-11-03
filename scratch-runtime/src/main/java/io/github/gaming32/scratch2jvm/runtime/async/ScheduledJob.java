package io.github.gaming32.scratch2jvm.runtime.async;

@SuppressWarnings("unused")
public final class ScheduledJob {
    public final AsyncHandler handler;
    public double[] state;
    public int label = 0;

    ScheduledJob(AsyncHandler handler) {
        this.handler = handler;
    }
}

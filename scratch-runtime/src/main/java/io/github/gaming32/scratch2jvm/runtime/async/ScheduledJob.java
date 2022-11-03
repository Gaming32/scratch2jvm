package io.github.gaming32.scratch2jvm.runtime.async;

public final class ScheduledJob {
    final AsyncHandler handler;
    int label = 0;

    ScheduledJob(AsyncHandler handler) {
        this.handler = handler;
    }
}

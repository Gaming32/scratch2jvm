package io.github.gaming32.scratch2jvm.runtime.async;

import io.github.gaming32.scratch2jvm.runtime.target.Target;

@SuppressWarnings("unused")
public final class ScheduledJob {
    private static final Object[] EMPTY = new Object[0];

    public final AsyncHandler handler;
    public final Object[] args;
    public double[] state;
    public int label = 0;
    public ScheduledJob awaiting;
    public boolean finished;

    public ScheduledJob(AsyncHandler handler, Object... args) {
        this.handler = handler;
        this.args = args;
    }

    public ScheduledJob(AsyncHandler handler) {
        this(handler, EMPTY);
    }

    public int step(Target target) {
        return handler.handle(target, this);
    }
}

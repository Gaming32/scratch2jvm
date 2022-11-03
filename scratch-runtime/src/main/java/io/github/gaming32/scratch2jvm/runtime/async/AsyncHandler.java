package io.github.gaming32.scratch2jvm.runtime.async;

import io.github.gaming32.scratch2jvm.runtime.target.Target;

@FunctionalInterface
public interface AsyncHandler {
    int handle(Target target, ScheduledJob job);
}

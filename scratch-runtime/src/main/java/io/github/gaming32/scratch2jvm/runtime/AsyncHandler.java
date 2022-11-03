package io.github.gaming32.scratch2jvm.runtime;

@FunctionalInterface
public interface AsyncHandler {
    int handle(Target target, int label);
}

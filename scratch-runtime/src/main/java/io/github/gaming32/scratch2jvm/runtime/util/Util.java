package io.github.gaming32.scratch2jvm.runtime.util;

public final class Util {
    private Util() {
        throw new AssertionError();
    }

    public static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(v, max));
    }
}

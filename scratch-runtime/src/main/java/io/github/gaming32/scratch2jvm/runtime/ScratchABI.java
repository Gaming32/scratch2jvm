package io.github.gaming32.scratch2jvm.runtime;

public final class ScratchABI {
    public static final boolean CONSOLE_MODE = Boolean.getBoolean("scratch.consoleMode");

    private ScratchABI() {
        throw new AssertionError();
    }
}

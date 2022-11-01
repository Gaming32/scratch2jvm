package io.github.gaming32.scratch2jvm.runtime;

@SuppressWarnings("unused")
public final class ScratchABI {
    public static final boolean CONSOLE_MODE = Boolean.getBoolean("scratch.consoleMode");

    private ScratchABI() {
        throw new AssertionError();
    }

    public static void say(String what) {
        if (CONSOLE_MODE) {
            System.out.println(what);
        } else {
            onlyImplementedInConsole("looks_say");
        }
    }

    private static void onlyImplementedInConsole(String opcode) {
        throw new UnsupportedOperationException(opcode + " outside of console mode");
    }
}

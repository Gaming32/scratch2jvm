package io.github.gaming32.scratch2jvm.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class ScratchABI {
    public static final boolean CONSOLE_MODE = Boolean.getBoolean("scratch.consoleMode");

    private ScratchABI() {
        throw new AssertionError();
    }

    public static List<String> loadListResource(
        Class<? extends Target> clazz,
        String resourceName,
        int size
    ) throws IOException {
        final List<String> result = new ArrayList<>(size);
        final InputStream resource = clazz.getResourceAsStream("/" + resourceName);
        if (resource != null) {
            try (
                final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource, StandardCharsets.UTF_8)
                )
            ) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.add(line);
                }
            }
        }
        return result;
    }

    public static void say(Target target, String what) {
        if (CONSOLE_MODE) {
            System.out.println("SAY: " + target.name + ": " + what);
        } else {
            onlyImplementedInConsole("looks_say");
        }
    }

    private static void onlyImplementedInConsole(String opcode) {
        throw new UnsupportedOperationException(opcode + " outside of console mode");
    }
}

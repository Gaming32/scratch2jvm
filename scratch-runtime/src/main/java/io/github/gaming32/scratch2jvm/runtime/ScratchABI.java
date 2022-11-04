package io.github.gaming32.scratch2jvm.runtime;

import io.github.gaming32.scratch2jvm.runtime.async.AsyncScheduler;
import io.github.gaming32.scratch2jvm.runtime.target.Target;

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
    public static final AsyncScheduler SCHEDULER = new AsyncScheduler();

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

    public static double getNumber(String value) {
        if (value.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static double mod(double a, double b) {
        final double result = a % b;
        if ((b < 0) != (result < 0)) {
            return result + b;
        }
        return result;
    }

    public static double random(double from, double to) {
        return Math.floor(from + Math.random() * (to - from + 1));
    }

    public static String letterOf(String s, double letter) {
        if (letter < 1 || letter > s.length()) {
            return "";
        }
        return String.valueOf(s.charAt((int)(letter - 1)));
    }

    public static void deleteOfList(List<String> list, double index) {
        if (index < 1 || index > list.size()) {
            return;
        }
        list.remove((int)(index - 1));
    }

    public static void insertAtList(List<String> list, double index, String value) {
        if (index < 1 || index > list.size() + 1) {
            return;
        }
        list.add((int)(index - 1), value);
    }

    public static void replaceItemOfList(List<String> list, double index, String value) {
        if (index < 1 || index > list.size()) {
            return;
        }
        list.add((int)(index - 1), value);
    }

    public static String itemOfList(List<String> list, double index) {
        if (index < 1 || index > list.size()) {
            return "";
        }
        return list.get((int)(index - 1));
    }

    public static String listToString(List<String> list) {
        int sep = 0;
        for (final String value : list) {
            if (value.length() != 1) {
                sep = 1;
                break;
            }
        }
        final StringBuilder result = new StringBuilder();
        for (final String value : list) {
            if (sep == 2) {
                result.append(' ');
            } else if (sep == 1) {
                sep = 2;
            }
            result.append(value);
        }
        return result.toString();
    }

    public static String doubleToString(double d) {
        final long l = (long)d;
        if (l == d) {
            return Long.toString(l);
        }
        return Double.toString(d);
    }

    private static void onlyImplementedInConsole(String opcode) {
        throw new UnsupportedOperationException(opcode + " outside of console mode");
    }
}

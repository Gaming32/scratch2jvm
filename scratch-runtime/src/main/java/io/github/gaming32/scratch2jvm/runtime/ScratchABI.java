package io.github.gaming32.scratch2jvm.runtime;

import io.github.gaming32.scratch2jvm.runtime.async.AsyncScheduler;
import io.github.gaming32.scratch2jvm.runtime.async.ScheduledJob;
import io.github.gaming32.scratch2jvm.runtime.renderer.GlRenderer;
import io.github.gaming32.scratch2jvm.runtime.renderer.ScratchRenderer;
import io.github.gaming32.scratch2jvm.runtime.renderer.StubRenderer;
import io.github.gaming32.scratch2jvm.runtime.target.Sprite;
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
    public static final boolean HEADLESS;
    public static final boolean DO_LOGGING;
    public static final AsyncScheduler SCHEDULER = new AsyncScheduler();
    public static final ScratchRenderer RENDERER;
    public static double timerStart = 0;
    public static int cloneCount = 0;

    static {
        boolean headless = Boolean.getBoolean("scratch.headless");
        if (headless) {
            RENDERER = new StubRenderer();
        } else {
            ScratchRenderer renderer;
            try {
                renderer = new GlRenderer();
            } catch (Throwable t) {
                System.err.println("[ERROR] Could not create GL renderer. Falling back to stub renderer.");
                System.err.println("[ERROR] Use -Dscratch.headless=true to remove this error.");
                t.printStackTrace();
                renderer = new StubRenderer();
                headless = true;
            }
            RENDERER = renderer;
        }
        HEADLESS = headless;
        DO_LOGGING = HEADLESS || Boolean.getBoolean("scratch.logging");
    }

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
        if (DO_LOGGING) {
            System.out.println("SAY: " + target.name + ": " + what);
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

    public static double flooredRandom(double from, double to) {
        return Math.floor(random(from, to));
    }

    public static double random(double from, double to) {
        return from + Math.random() * (to - from + 1);
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

    public static ScheduledJob wait(double seconds) {
        final long targetTime = System.nanoTime() + (long)(seconds * 1e9);
        return new ScheduledJob((target, job) -> {
            if (System.nanoTime() >= targetTime) {
                return -1;
            }
            return 0;
        });
    }

    public static boolean compareValues(String v1, String v2, int check) {
        try {
            return Double.compare(Double.parseDouble(v1), Double.parseDouble(v2)) == check;
        } catch (NumberFormatException e) {
            return v1.compareToIgnoreCase(v2) == check;
        }
    }

    public static double wrapClamp(double n, double min, double max) {
        final double range = (max - min) + 1;
        return n - (Math.floor((n - min) / range) * range);
    }

    public static String of(Target target, String property) {
        if (target.isStage) {
            switch (property) {
                case "background #":
                case "backdrop #":
                    return Integer.toString(target.costume + 1);
                case "backdrop name":
                    return target.costumes.get(target.costume).name;
                case "volume":
                    return doubleToString(target.volume);
            }
        } else {
            switch (property) {
                case "x position":
                    return doubleToString(((Sprite)target).x);
                case "y position":
                    return doubleToString(((Sprite)target).y);
                case "direction":
                    return doubleToString(((Sprite)target).direction);
                case "costume #":
                    return Integer.toString(target.costume + 1);
                case "costume name":
                    return target.costumes.get(target.costume).name;
                case "size":
                    return doubleToString(((Sprite)target).size);
                case "volume":
                    return doubleToString(target.volume);
            }
        }

        return target.getVariable(property);
    }
}

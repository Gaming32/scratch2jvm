package io.github.gaming32.scratch2jvm.runtime.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class NamedIndexedArray<T> {
    private static final Object[] EMPTY = new Object[0];

    private Object[] values = EMPTY;
    private final Map<String, Integer> indices = new HashMap<>();

    public NamedIndexedArray() {
    }

    public NamedIndexedArray(String[] names, T[] values) {
        if (names.length != values.length) {
            throw new IllegalArgumentException("names and values must have matching lengths");
        }
        this.values = values;
        for (int i = 0; i < names.length; i++) {
            indices.put(names[i], i);
        }
    }

    public int put(String name, T value) {
        int end = values.length - 1;
        final Integer oldIndex = indices.get(name);
        if (oldIndex == null) {
            values = Arrays.copyOf(values, ++end);
        } else if (oldIndex != end) {
            System.arraycopy(values, oldIndex + 1, values, oldIndex, end - oldIndex);
        }
        indices.put(name, end);
        values[end] = value;
        return end;
    }

    public int getIndex(String name) {
        final Integer index = indices.get(name);
        return index == null ? -1 : index;
    }

    public T get(String name) {
        return get(getIndex(name));
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        return (T)values[index];
    }

    @Override
    public String toString() {
        return Arrays.stream(values)
            .map(String::valueOf)
            .collect(Collectors.joining(", ", "[", "]"));
    }

    public int size() {
        return values.length;
    }
}

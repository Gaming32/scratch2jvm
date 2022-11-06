package io.github.gaming32.scratch2jvm.runtime.target;

import io.github.gaming32.scratch2jvm.runtime.ScratchCostume;
import io.github.gaming32.scratch2jvm.runtime.util.NamedIndexedArray;

@SuppressWarnings("unused")
public abstract class Sprite extends Target {
    public double x, y, size, direction;
    public boolean draggable;
    public byte rotationStyle;

    protected Sprite(String name, NamedIndexedArray<ScratchCostume> costumes) {
        super(name, false, costumes);
    }
}

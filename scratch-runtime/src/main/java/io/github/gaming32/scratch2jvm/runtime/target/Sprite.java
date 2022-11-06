package io.github.gaming32.scratch2jvm.runtime.target;

import io.github.gaming32.scratch2jvm.runtime.ScratchCostume;
import io.github.gaming32.scratch2jvm.runtime.util.NamedIndexedArray;
import io.github.gaming32.scratch2jvm.runtime.util.Util;

@SuppressWarnings("unused")
public abstract class Sprite extends Target {
    public double x, y, size, direction;
    public boolean draggable;
    public byte rotationStyle;

    protected Sprite(String name, NamedIndexedArray<ScratchCostume> costumes) {
        super(name, false, costumes);
    }

    public final void setX(double x) {
        final double dist = 225 + costumes.get(costume).centerX;
        this.x = Util.clamp(x, -dist, dist);
    }

    public final void setY(double y) {
        final double dist = 165 + costumes.get(costume).centerY;
        this.y = Util.clamp(y, -dist, dist);
    }
}

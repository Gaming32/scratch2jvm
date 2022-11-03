package io.github.gaming32.scratch2jvm.runtime.target;

@SuppressWarnings("unused")
public abstract class Sprite extends Target {
    public double x, y, size, direction;
    public boolean draggable;
    public byte rotationStyle;

    protected Sprite(String name) {
        super(name, false);
    }
}

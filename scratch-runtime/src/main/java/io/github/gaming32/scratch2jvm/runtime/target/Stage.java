package io.github.gaming32.scratch2jvm.runtime.target;

@SuppressWarnings("unused")
public abstract class Stage extends Target {
    public double tempo;

    protected Stage(String name) {
        super(name, true);
    }
}
package io.github.gaming32.scratch2jvm.runtime;

@SuppressWarnings("unused")
public abstract class Target {
    public final String name;
    public final boolean isStage;
    public int costume;
    public double volume;
    public int layerOrder;

    Target(String name, boolean isStage) {
        this.name = name;
        this.isStage = isStage;
    }

    public abstract void registerEvents(AsyncScheduler scheduler);
}

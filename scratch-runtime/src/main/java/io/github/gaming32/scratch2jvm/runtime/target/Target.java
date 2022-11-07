package io.github.gaming32.scratch2jvm.runtime.target;

import io.github.gaming32.scratch2jvm.runtime.ScratchCostume;
import io.github.gaming32.scratch2jvm.runtime.async.AsyncScheduler;
import io.github.gaming32.scratch2jvm.runtime.util.NamedIndexedArray;

@SuppressWarnings("unused")
public abstract class Target {
    public final String name;
    public final boolean isStage;
    public final NamedIndexedArray<ScratchCostume> costumes;
    public int costume;
    public double volume;
    public int layerOrder;

    Target(String name, boolean isStage, NamedIndexedArray<ScratchCostume> costumes) {
        this.name = name;
        this.isStage = isStage;
        this.costumes = costumes;
    }

    public abstract void registerEvents(AsyncScheduler scheduler);

    public final ScratchCostume getCostume() {
        return costumes.get(costume);
    }
}

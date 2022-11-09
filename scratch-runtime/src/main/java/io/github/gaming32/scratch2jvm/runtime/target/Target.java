package io.github.gaming32.scratch2jvm.runtime.target;

import io.github.gaming32.scratch2jvm.runtime.ScratchABI;
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

    public abstract String getVariable(String name);

    public final ScratchCostume getCostume() {
        return costumes.get(costume);
    }

    public final void setCostume(double costume) {
        this.costume = (int)ScratchABI.wrapClamp(costume, 1, costumes.size()) - 1;
    }

    public final void setCostume(String requested) {
        try {
            setCostume(Integer.parseInt(requested));
            return;
        } catch (NumberFormatException ignored) {
        }
        final int index = costumes.getIndex(requested);
        if (index != -1) {
            setCostume(index);
        } else if (requested.equals("next costume")) {
            setCostume(costume + 1);
        } else if (requested.equals("previous costume")) {
            setCostume(costume - 1);
        } else {
            try {
                setCostume(Double.parseDouble(requested));
            } catch (NumberFormatException ignored) {
            }
        }
    }
}

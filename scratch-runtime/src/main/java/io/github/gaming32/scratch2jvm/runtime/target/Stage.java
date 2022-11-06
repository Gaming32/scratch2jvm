package io.github.gaming32.scratch2jvm.runtime.target;

import io.github.gaming32.scratch2jvm.runtime.ScratchCostume;
import io.github.gaming32.scratch2jvm.runtime.util.NamedIndexedArray;

@SuppressWarnings("unused")
public abstract class Stage extends Target {
    public double tempo;

    protected Stage(String name, NamedIndexedArray<ScratchCostume> costumes) {
        super(name, true, costumes);
    }
}

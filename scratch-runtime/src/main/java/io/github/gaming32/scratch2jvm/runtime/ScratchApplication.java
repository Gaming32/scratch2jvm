package io.github.gaming32.scratch2jvm.runtime;

public final class ScratchApplication {
    public final String name;
    public final int framerate;
    public final int penScale;

    public ScratchApplication(String name, int framerate, int penScale) {
        this.name = name;
        this.framerate = framerate;
        this.penScale = penScale;
    }
}

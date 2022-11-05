package io.github.gaming32.scratch2jvm.runtime.renderer;

import io.github.gaming32.scratch2jvm.runtime.async.AsyncScheduler;

public final class GlRenderer implements ScratchRenderer {
    public GlRenderer() {
        throw new UnsupportedOperationException("GL renderer not yet implemented");
    }

    @Override
    public void init() {
        System.out.println("Using GL renderer");
    }

    @Override
    public void render(AsyncScheduler scheduler) {
    }

    @Override
    public void quit() {
    }
}

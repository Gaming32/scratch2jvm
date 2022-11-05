package io.github.gaming32.scratch2jvm.runtime.renderer;

import io.github.gaming32.scratch2jvm.runtime.async.AsyncScheduler;

public final class StubRenderer implements ScratchRenderer {
    @Override
    public void init() {
        System.out.println("Using stub (console) renderer");
    }

    @Override
    public void render(AsyncScheduler scheduler) {
    }

    @Override
    public void quit() {
    }
}

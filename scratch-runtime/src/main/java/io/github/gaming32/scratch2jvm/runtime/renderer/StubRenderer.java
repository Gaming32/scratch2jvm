package io.github.gaming32.scratch2jvm.runtime.renderer;

import io.github.gaming32.scratch2jvm.runtime.ScratchApplication;
import io.github.gaming32.scratch2jvm.runtime.async.AsyncScheduler;

public final class StubRenderer implements ScratchRenderer {
    private ScratchApplication application;

    @Override
    public void init() {
        System.out.println("Using stub (console) renderer");
        System.out.println("Running " + application.name);
    }

    @Override
    public boolean render(AsyncScheduler scheduler) {
        return false;
    }

    @Override
    public void quit() {
    }

    @Override
    public void setApplication(ScratchApplication application) {
        this.application = application;
    }
}

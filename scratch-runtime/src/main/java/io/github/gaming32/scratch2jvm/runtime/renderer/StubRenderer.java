package io.github.gaming32.scratch2jvm.runtime.renderer;

import io.github.gaming32.scratch2jvm.runtime.ScratchApplication;
import io.github.gaming32.scratch2jvm.runtime.async.AsyncScheduler;

public final class StubRenderer implements ScratchRenderer {
    private ScratchApplication application;
    private final long timerStart = System.nanoTime();

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

    @Override
    public boolean isMouseDown() {
        return false;
    }

    @Override
    public void getMousePos(double[] xBuf, double[] yBuf) {
        if (xBuf != null) {
            xBuf[0] = 0;
        }
        if (yBuf != null) {
            yBuf[0] = 0;
        }
    }

    @Override
    public boolean keyPressed(int glfwKey) {
        return false;
    }

    @Override
    public double getAbsoluteTimer() {
        return (System.nanoTime() - timerStart) / 1e9;
    }
}

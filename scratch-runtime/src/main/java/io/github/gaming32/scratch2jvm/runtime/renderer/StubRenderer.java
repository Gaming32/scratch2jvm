package io.github.gaming32.scratch2jvm.runtime.renderer;

import io.github.gaming32.scratch2jvm.runtime.ScratchABI;
import io.github.gaming32.scratch2jvm.runtime.ScratchApplication;
import io.github.gaming32.scratch2jvm.runtime.async.AsyncScheduler;
import io.github.gaming32.scratch2jvm.runtime.extensions.PenState;

public final class StubRenderer implements ScratchRenderer {
    private ScratchApplication application;
    private final long timerStart = System.nanoTime();

    @Override
    public void init() {
        System.out.println("Using stub (console) renderer");
        System.out.println("Running " + application.name);
    }

    @Override
    public boolean tick(AsyncScheduler scheduler) {
        return false;
    }

    @Override
    public void render(AsyncScheduler scheduler) {
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

    @Override
    public void penClear() {
        if (ScratchABI.DO_LOGGING) {
            System.out.println("PEN CLEAR");
        }
    }

    @Override
    public void penLine(double x1, double y1, double x2, double y2, PenState state) {
        System.out.println("PEN LINE: " + x1 + ", " + y1 + " -> " + x2 + ", " + y2);
    }
}

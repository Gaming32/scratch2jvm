package io.github.gaming32.scratch2jvm.runtime.renderer;

import io.github.gaming32.scratch2jvm.runtime.ScratchApplication;
import io.github.gaming32.scratch2jvm.runtime.async.AsyncScheduler;
import io.github.gaming32.scratch2jvm.runtime.extensions.PenState;

@SuppressWarnings("unused")
public interface ScratchRenderer {
    void init();

    /**
     * @return Whether the application should exit
     */
    boolean tick(AsyncScheduler scheduler);

    void render(AsyncScheduler scheduler);

    void quit();

    void setApplication(ScratchApplication application);

    boolean isMouseDown();

    void getMousePos(double[] xBuf, double[] yBuf);

    default double getMouseX() {
        final double[] xBuf = new double[1];
        getMousePos(xBuf, null);
        return xBuf[0];
    }

    default double getMouseY() {
        final double[] yBuf = new double[1];
        getMousePos(null, yBuf);
        return yBuf[0];
    }

    boolean keyPressed(int glfwKey);

    double getAbsoluteTimer();

    void penClear();

    void penLine(double x1, double y1, double x2, double y2, PenState state);
}

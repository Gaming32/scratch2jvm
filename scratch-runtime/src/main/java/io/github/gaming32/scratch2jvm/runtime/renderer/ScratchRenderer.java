package io.github.gaming32.scratch2jvm.runtime.renderer;

import io.github.gaming32.scratch2jvm.runtime.ScratchApplication;
import io.github.gaming32.scratch2jvm.runtime.async.AsyncScheduler;

@SuppressWarnings("unused")
public interface ScratchRenderer {
    void init();

    /**
     * @return Whether the application should exit
     */
    boolean render(AsyncScheduler scheduler);

    void quit();

    void setApplication(ScratchApplication application);

    boolean isMouseDown();

    void getMousePos(double[] xBuf, double[] yBuf);

    boolean keyPressed(int glfwKey);
}

package io.github.gaming32.scratch2jvm.runtime.renderer;

import io.github.gaming32.scratch2jvm.runtime.async.AsyncScheduler;

public interface ScratchRenderer {
    void init();

    void render(AsyncScheduler scheduler);

    void quit();
}

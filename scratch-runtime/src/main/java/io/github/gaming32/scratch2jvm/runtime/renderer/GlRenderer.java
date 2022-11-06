package io.github.gaming32.scratch2jvm.runtime.renderer;

import io.github.gaming32.scratch2jvm.runtime.ScratchApplication;
import io.github.gaming32.scratch2jvm.runtime.ScratchCostume;
import io.github.gaming32.scratch2jvm.runtime.async.AsyncScheduler;
import io.github.gaming32.scratch2jvm.runtime.target.Sprite;
import io.github.gaming32.scratch2jvm.runtime.target.Target;
import org.joml.Vector2i;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nanovg.NSVGImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoSVG.*;
import static org.lwjgl.opengl.GL11.*;

public final class GlRenderer implements ScratchRenderer {
    private static final double IDEAL_ASPECT = 480.0 / 360.0;

    private ScratchApplication application;
    private final Map<ScratchCostume, Integer> costumeTextures = new IdentityHashMap<>();
    private final Map<ScratchCostume, NSVGImage> costumeSvgs = new IdentityHashMap<>();
    private final Map<ScratchCostume, Double> svgScales = new IdentityHashMap<>();
    private final Vector2i windowSize = new Vector2i();
    private final Vector2i barSize = new Vector2i();
    private int boundTex = -1;
    private long window;
    private double graphicsScale;
    private long rasterizer;
    private double lastTime;

    @Override
    public void init() {
        System.out.println("Using GL renderer");
        System.out.println("LWJGL " + Version.getVersion());
        GLFWErrorCallback.createPrint().set();
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }

        rasterizer = nsvgCreateRasterizer();

        glfwDefaultWindowHints();
        final long monitor = glfwGetPrimaryMonitor();
        final GLFWVidMode videoMode = glfwGetVideoMode(monitor);
        if (videoMode == null) {
            throw new RuntimeException("Could not determine video mode");
        }

        windowSize.set((int)(videoMode.height() / 2 * IDEAL_ASPECT), videoMode.height() / 2);
        window = glfwCreateWindow(windowSize.x, windowSize.y, application.name, 0, 0);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        //noinspection resource
        glfwSetWindowSizeCallback(window, (window2, width, height) -> {
            windowSize.set(width, height);
            setViewport();
        });
        setViewport();

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glClearColor(1, 1, 1, 1);
    }

    private void setViewport() {
        final double aspect = windowSize.x / (double)windowSize.y;
        int offsetX = 0;
        int offsetY = 0;
        int width = windowSize.x;
        int height = windowSize.y;
        if (aspect < IDEAL_ASPECT) {
            graphicsScale = width / 480.0;
            height = (int)(windowSize.x / IDEAL_ASPECT);
            offsetY = windowSize.y / 2 - height / 2;
        } else {
            graphicsScale = height / 360.0;
            width = (int)(windowSize.y * IDEAL_ASPECT);
            offsetX = windowSize.x / 2 - width / 2;
        }
        barSize.set(offsetX, offsetY);

        glViewport(0, 0, windowSize.x, windowSize.y);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(
            -windowSize.x / 2.0, windowSize.x / 2.0,
            -windowSize.y / 2.0, windowSize.y / 2.0,
            -1.0, 1.0
        );

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0, 0, -0.5f);
        glScalef((float)graphicsScale, (float)graphicsScale, 1);
    }

    @Override
    public boolean render(AsyncScheduler scheduler) {
        glfwPollEvents();
        if (glfwWindowShouldClose(window)) return true;
        if (application.framerate > 0) {
            //noinspection StatementWithEmptyBody
            while (glfwGetTime() < lastTime + 1.0 / application.framerate) {
                // Busy wait
            }
            lastTime = glfwGetTime();
        }
        glClear(GL_COLOR_BUFFER_BIT);
        glColor3f(1, 1, 1);
        glEnable(GL_TEXTURE_2D);
        for (final Target target : scheduler.getTargets()) {
            final ScratchCostume costume = target.costumes.get(target.costume);
            double x = 0, y = 0, scale = 1;
            if (target instanceof Sprite) {
                x = ((Sprite)target).x;
                y = ((Sprite)target).y;
                scale = ((Sprite)target).size / 100.0;
            }
            final int costumeTex = getCostumeTex(costume, graphicsScale * scale);
            if (costumeTex != boundTex) {
                glBindTexture(GL_TEXTURE_2D, costumeTex);
                boundTex = costumeTex;
            }
            final double width, height;
            if (target.isStage) {
                width = costume.centerX;
                height = costume.centerY;
            } else {
                width = costume.width * scale;
                height = costume.height * scale;
            }
            texturedQuad(
                (float)(x - width / 2),
                (float)(y + height / 2),
                (float)(x + width / 2),
                (float)(y - height / 2)
            );
        }
        final int barX = (int)(barSize.x / graphicsScale);
        final int barY = (int)(barSize.y / graphicsScale);
        glDisable(GL_TEXTURE_2D);
        if (barX > 0) {
            quad(-240 - barX, -180, -240, 180);
            quad(240, -180, 240 + barX, 180);
        } else if (barY > 0) {
            quad(-240, -180 - barY, 240, -180);
            quad(-240, 180, 240, 180 + barY);
        }
        glfwSwapBuffers(window);
        return false;
    }

    private int getCostumeTex(ScratchCostume costume, double scale) {
        final Function<ScratchCostume, Integer> calculateTex = key -> {
            final int width, height;
            final ByteBuffer rgba;
            if (key.format == ScratchCostume.Format.PNG) {
                final BufferedImage image;
                try {
                    image = ImageIO.read(Objects.requireNonNull(getClass().getResource(key.path)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                costume.width = width = image.getWidth();
                costume.height = height = image.getWidth();
                rgba = MemoryUtil.memAlloc(width * height * 4).order(ByteOrder.BIG_ENDIAN);
                final int[] argb = new int[width * height];
                image.getRGB(0, 0, width, height, argb, 0, width);
                for (int pixel : argb) {
                    rgba.putInt((pixel << 8) | (pixel >>> 24));
                }
                rgba.flip();
            } else { // SVG
                final NSVGImage image = costumeSvgs.computeIfAbsent(key, key2 -> {
                    final StringBuilder source = new StringBuilder();
                    try (
                        final Reader reader = new InputStreamReader(
                            Objects.requireNonNull(GlRenderer.class.getResourceAsStream(key2.path)),
                            StandardCharsets.US_ASCII
                        )
                    ) {
                        final char[] buf = new char[8192];
                        int n;
                        while ((n = reader.read(buf)) != -1) {
                            source.append(buf, 0, n);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    final NSVGImage svg = nsvgParse(source, "px", 96);
                    if (svg == null) {
                        throw new RuntimeException("Failed to parse " + key2.path);
                    }
                    return svg;
                });
                costume.width = image.width();
                costume.height = image.height();
                width = (int)(costume.width * scale);
                height = (int)(costume.height * scale);
                rgba = MemoryUtil.memAlloc(width * height * 4);
                nsvgRasterize(rasterizer, image, 0, 0, (float)scale, rgba, width, height, width * 4);
            }
            final int newTex = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, newTex);
            boundTex = newTex;
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, rgba);
            MemoryUtil.memFree(rgba);
            return newTex;
        };
        if (costume.format == ScratchCostume.Format.PNG) {
            return costumeTextures.computeIfAbsent(costume, calculateTex);
        }
        final Double previousScale = svgScales.get(costume);
        if (previousScale == null) {
            svgScales.put(costume, scale);
            return costumeTextures.computeIfAbsent(costume, calculateTex);
        }
        if (scale == previousScale) {
            return costumeTextures.computeIfAbsent(costume, calculateTex);
        }
        svgScales.put(costume, scale);
        final int newTex = calculateTex.apply(costume);
        //noinspection ConstantConditions
        glDeleteTextures(costumeTextures.put(costume, newTex));
        return newTex;
    }

    private static void quad(float x1, float y1, float x2, float y2) {
        glBegin(GL_QUADS);
        glVertex2f(x1, y1);
        glVertex2f(x1, y2);
        glVertex2f(x2, y2);
        glVertex2f(x2, y1);
        glEnd();
    }

    private static void texturedQuad(float x1, float y1, float x2, float y2) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x1, y1);
        glTexCoord2f(0, 1);
        glVertex2f(x1, y2);
        glTexCoord2f(1, 1);
        glVertex2f(x2, y2);
        glTexCoord2f(1, 0);
        glVertex2f(x2, y1);
        glEnd();
    }

    @Override
    public void quit() {
        for (final NSVGImage image : costumeSvgs.values()) {
            nsvgDelete(image);
        }
        for (final int texture : costumeTextures.values()) {
            glDeleteTextures(texture);
        }
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        nsvgDeleteRasterizer(rasterizer);
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    @Override
    public void setApplication(ScratchApplication application) {
        this.application = application;
    }

    @Override
    public boolean isMouseDown() {
        for (int button = GLFW_MOUSE_BUTTON_LEFT; button <= GLFW_MOUSE_BUTTON_LAST; button++) {
            if (glfwGetMouseButton(window, button) == GLFW_PRESS) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void getMousePos(double[] xBuf, double[] yBuf) {
        glfwGetCursorPos(window, xBuf, yBuf);
        if (xBuf != null) {
            xBuf[0] = (xBuf[0] - barSize.x) / graphicsScale - 240.0;
        }
        if (yBuf != null) {
            yBuf[0] = 180.0 - (yBuf[0] - barSize.y) / graphicsScale;
        }
    }

    @Override
    public boolean keyPressed(int glfwKey) {
        return glfwGetKey(window, glfwKey) == GLFW_PRESS;
    }
}

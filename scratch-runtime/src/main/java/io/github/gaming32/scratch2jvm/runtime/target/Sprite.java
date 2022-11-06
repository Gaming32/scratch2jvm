package io.github.gaming32.scratch2jvm.runtime.target;

import io.github.gaming32.scratch2jvm.runtime.ScratchABI;
import io.github.gaming32.scratch2jvm.runtime.ScratchCostume;
import io.github.gaming32.scratch2jvm.runtime.async.ScheduledJob;
import io.github.gaming32.scratch2jvm.runtime.util.NamedIndexedArray;
import org.joml.Math;

@SuppressWarnings("unused")
public abstract class Sprite extends Target {
    public double x, y, size, direction;
    public boolean draggable;
    public RotationStyle rotationStyle;

    protected Sprite(String name, NamedIndexedArray<ScratchCostume> costumes) {
        super(name, false, costumes);
    }

    public final void setXY(double x, double y) {
        setX(x);
        setY(y);
    }

    public final void setX(double x) {
        final double dist = 225 + costumes.get(costume).centerX;
        this.x = Math.clamp(-dist, dist, x);
    }

    public final void setY(double y) {
        final double dist = 165 + costumes.get(costume).centerY;
        this.y = Math.clamp(-dist, dist, y);
    }

    public final void setDirection(double direction) {
        direction = ScratchABI.mod(direction, 360);
        this.direction = direction > 180 ? direction - 360 : direction;
    }

    public final void gotoMousePosition() {
        final double[] xBuf = new double[1];
        final double[] yBuf = new double[1];
        ScratchABI.RENDERER.getMousePos(xBuf, yBuf);
        setXY(xBuf[0], yBuf[0]);
    }

    public final ScheduledJob glideToMousePosition(double secs) {
        final double[] xBuf = new double[1];
        final double[] yBuf = new double[1];
        ScratchABI.RENDERER.getMousePos(xBuf, yBuf);
        return glideTo(secs, xBuf[0], yBuf[0]);
    }

    public final ScheduledJob glideTo(double secs, double targetX, double targetY) {
        final long startTime = System.nanoTime();
        final long duration = (long)(secs * 1e9);
        final long targetTime = System.nanoTime() + duration;
        final double startX = x;
        final double startY = y;
        return new ScheduledJob((target, job) -> {
            final long time = System.nanoTime();
            if (time >= targetTime) {
                setXY(targetX, targetY);
                return -1;
            }
            final double progress = (time - startTime) / (double)duration;
            setXY(
                Math.lerp(startX, targetX, progress),
                Math.lerp(startY, targetY, progress)
            );
            return 0;
        });
    }
}

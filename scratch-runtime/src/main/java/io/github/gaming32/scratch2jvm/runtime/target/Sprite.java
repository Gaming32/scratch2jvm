package io.github.gaming32.scratch2jvm.runtime.target;

import io.github.gaming32.scratch2jvm.runtime.AABB;
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

    public final AABB getBounds() {
        final ScratchCostume costume = getCostume();
        final double leftX = x - costume.centerX;
        final double topY = y + costume.centerY;
        return new AABB(leftX, leftX + costume.width, topY - costume.height, topY);
    }

    public final void setXY(double x, double y) {
        setX(x);
        setY(y);
    }

    public final void setX(double x) {
        final ScratchCostume costume = getCostume();
        final double leftX = x - costume.centerX;
        final double rightX = leftX + costume.width;
        if (rightX < -230) {
            x = -230 + costume.centerX - costume.width;
        } else if (leftX > 230) {
            x = 230 + costume.centerX;
        }
        this.x = x;
    }

    public final void setY(double y) {
        final ScratchCostume costume = getCostume();
        final double topY = y + costume.centerY;
        final double bottomY = topY - costume.height;
        if (topY < -170) {
            y = -170 - costume.centerY;
        } else if (bottomY > 170) {
            y = 170 - costume.centerY + costume.height;
        }
        this.y = y;
    }

    public final void setDirection(double direction) {
        direction = ScratchABI.mod(direction, 360);
        this.direction = direction > 180 ? direction - 360 : direction;
    }

    public final void pointTowardsMouse() {
        final double[] xBuf = new double[1];
        final double[] yBuf = new double[1];
        ScratchABI.RENDERER.getMousePos(xBuf, yBuf);
        direction = Math.toDegrees(Math.atan2(xBuf[0] - x, yBuf[0] - y));
    }

    public final void gotoMousePosition() {
        final double[] xBuf = new double[1];
        final double[] yBuf = new double[1];
        ScratchABI.RENDERER.getMousePos(xBuf, yBuf);
        setXY(xBuf[0], yBuf[0]);
    }

    public final void gotoSprite(Sprite sprite) {
        setXY(sprite.x, sprite.y);
    }

    public final ScheduledJob glideToMousePosition(double secs) {
        final double[] xBuf = new double[1];
        final double[] yBuf = new double[1];
        ScratchABI.RENDERER.getMousePos(xBuf, yBuf);
        return glideTo(secs, xBuf[0], yBuf[0]);
    }

    public final ScheduledJob glideToSprite(double secs, Sprite sprite) {
        return glideTo(secs, sprite.x, sprite.y);
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

    public final boolean touchingEdge() {
        final ScratchCostume costume = costumes.get(this.costume);
        final double leftX = x - costume.centerX;
        if (leftX <= -240 || leftX + costume.width >= 240) {
            return true;
        }
        final double topY = y + costume.centerY;
        return topY >= 180 || topY - costume.height <= -180;
    }

    public final boolean touchingMouse() {
        final double[] xBuf = new double[1];
        final double[] yBuf = new double[1];
        ScratchABI.RENDERER.getMousePos(xBuf, yBuf);
        return getBounds().contains(xBuf[0], yBuf[0]);
    }
}

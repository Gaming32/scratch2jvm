package io.github.gaming32.scratch2jvm.runtime.extensions;

import io.github.gaming32.scratch2jvm.runtime.ScratchABI;
import org.joml.Math;

import java.awt.*;

@SuppressWarnings("unused")
public final class PenState implements Cloneable {
    public boolean penDown;
    public double color = 66.66;
    public double saturation = 100;
    public double brightness = 100;
    public double transparency = 0;
    public final float[] color4f = {0, 0, 1, 1};
    public double diameter = 1;

    private void updatePenColor() {
        final int rgb = Color.HSBtoRGB(
            (float)(color / 100.0),
            (float)(saturation / 100.0),
            (float)(brightness / 100.0)
        );
        color4f[0] = (rgb >> 16 & 0xff) / 255f;
        color4f[1] = (rgb >> 8 & 0xff) / 255f;
        color4f[2] = (rgb & 0xff) / 255f;
        color4f[3] = 1f - (float)(transparency / 100.0);
    }

    public void setColorRgb(int rgb) {
        final float[] hsv = Color.RGBtoHSB(rgb >> 16 & 0xff, rgb >> 8 & 0xff, rgb & 0xff, null);
        color = hsv[0] * 100.0;
        saturation = hsv[1] * 100.0;
        brightness = hsv[2] * 100.0;
        updatePenColor();
    }

    public void setColor(double color) {
        this.color = ScratchABI.wrapClamp(color, 0, 100);
        updatePenColor();
    }

    public void setSaturation(double saturation) {
        this.saturation = Math.clamp(0, 100, saturation);
        updatePenColor();
    }

    public void setBrightness(double brightness) {
        this.brightness = Math.clamp(0, 100, brightness);
        updatePenColor();
    }

    public void setTransparency(double transparency) {
        this.transparency = Math.clamp(0, 100, transparency);
        updatePenColor();
    }

    public void setParameter(double value, String name, boolean change) {
        switch (name) {
            case "color": setColor(value + (change ? color : 0)); break;
            case "saturation": setSaturation(value + (change ? saturation : 0)); break;
            case "brightness": setBrightness(value + (change ? brightness : 0)); break;
            case "transparency": setTransparency(value + (change ? transparency : 0)); break;
            default:
                System.err.println("[WARN] Tried to set or change unknown color parameter: " + name);
        }
    }

    public void setSize(double size) {
        diameter = Math.clamp(1, 1200, size);
    }

    @Override
    public PenState clone() throws CloneNotSupportedException {
        return (PenState)super.clone();
    }
}

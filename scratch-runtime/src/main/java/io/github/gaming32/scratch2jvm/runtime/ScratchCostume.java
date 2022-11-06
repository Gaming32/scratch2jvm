package io.github.gaming32.scratch2jvm.runtime;

public final class ScratchCostume {
    @SuppressWarnings("unused")
    public enum Format {
        SVG, PNG
    }

    public final String name;
    public final String path;
    public final Format format;
    public final double centerX, centerY;
    public final double coordinateScale;
    public double width, height;

    public ScratchCostume(
        String name,
        String path,
        Format format,
        double centerX,
        double centerY,
        double coordinateScale
    ) {
        this.name = name;
        this.path = path;
        this.format = format;
        this.centerX = centerX;
        this.centerY = centerY;
        this.coordinateScale = coordinateScale;
    }
}

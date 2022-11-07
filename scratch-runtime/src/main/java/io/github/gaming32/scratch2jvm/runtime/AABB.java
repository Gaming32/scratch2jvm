package io.github.gaming32.scratch2jvm.runtime;

@SuppressWarnings("unused")
public final class AABB {
    public final double leftX, bottomY, rightX, topY;

    public AABB(double leftX, double bottomY, double rightX, double topY) {
        this.leftX = leftX;
        this.bottomY = bottomY;
        this.rightX = rightX;
        this.topY = topY;
    }

    public boolean contains(double x, double y) {
        return leftX <= x && x <= rightX && bottomY <= y && y <= topY;
    }

    public boolean intersects(AABB other) {
        return contains(other.leftX, other.bottomY) ||
            contains(other.leftX, other.topY) ||
            contains(other.rightX, other.bottomY) ||
            contains(other.rightX, other.topY);
    }

    public boolean contains(AABB other) {
        return contains(other.leftX, other.bottomY) &&
            contains(other.leftX, other.topY) &&
            contains(other.rightX, other.bottomY) &&
            contains(other.rightX, other.topY);
    }

    @Override
    public String toString() {
        return "AABB{" +
            "leftX=" + leftX +
            ", bottomY=" + bottomY +
            ", rightX=" + rightX +
            ", topY=" + topY +
            '}';
    }
}

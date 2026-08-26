package me.pindour.catppuccin.api.render.style;

public enum Corners {
    NONE(false, false, false, false),
    ALL(true, true, true, true),
    TOP(true, true, false, false),
    BOTTOM(false, false, true, true),
    LEFT(true, false, true, false),
    RIGHT(false, true, false, true);

    public final boolean topLeft;
    public final boolean topRight;
    public final boolean bottomLeft;
    public final boolean bottomRight;

    Corners(boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }
}

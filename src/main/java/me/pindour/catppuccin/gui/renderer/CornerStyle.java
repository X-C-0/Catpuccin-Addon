package me.pindour.catppuccin.gui.renderer;

public enum CornerStyle {
    NONE(false, false, false, false),
    ALL(true, true, true, true),
    TOP(true, true, false, false),
    BOTTOM(false, false, true, true),
    LEFT(true, false, true, false),
    RIGHT(false, true, false, true),
    TOP_LEFT(true, false, false, false),
    TOP_RIGHT(false, true, false, false),
    BOTTOM_LEFT(false, false, true, false),
    BOTTOM_RIGHT(false, false, false, true);

    public final boolean topLeft;
    public final boolean topRight;
    public final boolean bottomLeft;
    public final boolean bottomRight;

    CornerStyle(boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }
}

package me.pindour.catppuccin.api.render.style;

import meteordevelopment.meteorclient.utils.render.color.Color;

public class Outline {
    private static final Outline NONE = new Outline(0, new Color(0, 0, 0, 0));

    public final double width;
    public final Color color;

    private Outline(double width, Color color) {
        this.width = width;
        this.color = color;
    }

    public static Outline of(double width, Color color) {
        return new Outline(width, color);
    }

    public static Outline none() {
        return NONE;
    }

    public boolean isVisible() {
        return color.a > 0 && width > 0;
    }
}

package me.pindour.catppuccin.utils;

import meteordevelopment.meteorclient.utils.render.color.Color;

public class ColorUtils {

    public static Color darker(Color color, float factor) {
        int r = Math.max((int) (color.r * factor), 0);
        int g = Math.max((int) (color.g * factor), 0);
        int b = Math.max((int) (color.b * factor), 0);

        Color darkerColor = new Color(r, g, b, color.a);
        darkerColor.validate();

        return darkerColor;
    }

    public static Color darker(Color color) {
        return darker(color, 0.7f);
    }

    public static Color brighter(Color color, float factor) {
        int r = Math.min((int) (color.r * factor), 255);
        int g = Math.min((int) (color.g * factor), 255);
        int b = Math.min((int) (color.b * factor), 255);

        Color brighterColor = new Color(r, g, b, color.a);
        brighterColor.validate();

        return brighterColor;
    }

    public static Color brighter(Color color) {
        return brighter(color, 1.2f);
    }

    public static Color interpolateColor(Color from, Color to, double t) {
        t = Math.max(0, Math.min(1, t));

        if (t == 1) return to;
        if (t == 0) return from;

        int r = (int) (from.r + (to.r - from.r) * t);
        int g = (int) (from.g + (to.g - from.g) * t);
        int b = (int) (from.b + (to.b - from.b) * t);
        int a = (int) (from.a + (to.a - from.a) * t);

        Color color = new Color(r, g, b, a);
        color.validate();

        return color;
    }

    public static Color withAlpha(Color color, int alpha) {
        Color c = color.copy().a(alpha);
        c.validate();
        return c;
    }

    public static Color withAlpha(Color color, double alphaMultiplier) {
        Color c = color.copy().a((int) (255 * alphaMultiplier));
        c.validate();
        return c;
    }
}
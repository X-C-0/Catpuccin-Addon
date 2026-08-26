package me.pindour.catppuccin.utils;

import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import org.jetbrains.annotations.Range;

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

    /**
     * Also interpolates the Alpha value, as opposed to the {@link meteordevelopment.meteorclient.utils.Utils#lerp(Color, Color, float)} implementation.
     */
    public static Color lerp(Color first, Color second, @Range(from = 0, to = 1) double v) {
        return new Color(
                (int) (first.r * (1 - v) + second.r * v),
                (int) (first.g * (1 - v) + second.g * v),
                (int) (first.b * (1 - v) + second.b * v),
                (int) (first.a * (1 - v) + second.a * v)
        );
    }

    public static Color withAlpha(Color color, @Range(from = 0, to = 255) int alpha) {
        Color c = new Color(color.r, color.g, color.b, alpha);
        c.validate();
        return c;
    }

    public static Color withAlpha(Color color, @Range(from = 0, to = 1) double alphaMultiplier) {
        Color c = new Color(color.r, color.g, color.b, (int) (255 * alphaMultiplier));
        c.validate();
        return c;
    }

    public static double alphaToPercent(@Range(from = 0, to = 255) int alpha) {
        return alpha * 100.0 / 255.0;
    }

    public static int percentToAlpha(@Range(from = 0, to = 100) double percent) {
        return (int) (percent * 255 / 100.0);
    }

    /**
     * Perceived brightness of the color (0-255), weighted by ITU-R BT.601 luma coefficients.
     */
    public static int luma(Color color) {
        return (color.r * 299 + color.g * 587 + color.b * 114) / 1000;
    }

    public static SettingColor parseRGBA(String string) {
        String[] rgba = string.replaceAll("[^0-9|,]", "").split(",");

        if (rgba.length < 3 || rgba.length > 4) return null;

        SettingColor color;
        try {
            color = new SettingColor(
                    Integer.parseInt(rgba[0]),
                    Integer.parseInt(rgba[1]),
                    Integer.parseInt(rgba[2])
            );

            if (rgba.length == 4)
                color.a = Integer.parseInt(rgba[3]);

        } catch (NumberFormatException ignored) {
            return null;
        }

        return color;
    }

    public static SettingColor parseHex(String string) {
        if (!string.startsWith("#")) return null;

        String hex = string.toLowerCase().replaceAll("[^0-9a-f]", "");

        if (hex.length() != 6 && hex.length() != 8) return null;

        SettingColor color;
        try {
            color = new SettingColor(
                    Integer.parseInt(hex.substring(0, 2), 16),
                    Integer.parseInt(hex.substring(2, 4), 16),
                    Integer.parseInt(hex.substring(4, 6), 16)
            );

            if (hex.length() == 8)
                color.a = Integer.parseInt(hex.substring(6, 8), 16);

        } catch (NumberFormatException ignored) {
            return null;
        }

        return color;
    }

    /**
     * Hexagonal HSV to RGB conversion; hue in degrees (0-360), saturation and value in range 0-1.
     */
    public static void hsvToRgb(Color out, double hue, double saturation, double value) {
        // saturation == 0 yields r == g == b == value: p, q and t all equal value in the switch below
        double h = hue >= 360.0 ? 0.0 : hue;
        h /= 60.0;

        int section = (int) h;
        double fraction = h - section;
        double p = value * (1.0 - saturation);
        double q = value * (1.0 - saturation * fraction);
        double t = value * (1.0 - saturation * (1.0 - fraction));

        double r, g, b;
        switch (section) {
            case 0 -> { r = value; g = t; b = p; }
            case 1 -> { r = q; g = value; b = p; }
            case 2 -> { r = p; g = value; b = t; }
            case 3 -> { r = p; g = q; b = value; }
            case 4 -> { r = t; g = p; b = value; }
            default -> { r = value; g = p; b = q; }
        }

        out.r = (int) (r * 255);
        out.g = (int) (g * 255);
        out.b = (int) (b * 255);
    }

    public static Color hueToColor(double hue) {
        Color color = new Color(0, 0, 0);
        hsvToRgb(color, hue, 1, 1);
        color.validate();

        return color;
    }

    public static double hueFromColor(Color c) {
        double min = Math.min(c.r, c.g);
        min = min < c.b ? min : c.b;

        double max = Math.max(c.r, c.g);
        max = max > c.b ? max : c.b;

        double delta = max - min;
        if (delta < 0.00001) return 0;
        if (max <= 0.0) return 0;

        double hue;
        if (c.r >= max) hue = (c.g - c.b) / delta; // between yellow & magenta
        else if (c.g >= max) hue = 2.0 + (c.b - c.r) / delta; // between cyan & yellow
        else hue = 4.0 + (c.r - c.g) / delta; // between magenta & cyan

        hue *= 60.0; // degrees
        if (hue < 0.0) hue += 360.0;
        return hue;
    }

    public static double saturationFromColor(Color c) {
        double min = Math.min(Math.min(c.r, c.g), c.b);
        double max = Math.max(Math.max(c.r, c.g), c.b);
        double delta = max - min;

        return delta == 0 ? 0 : delta / max;
    }

    public static double valueFromColor(Color c) {
        return Math.max(Math.max(c.r, c.g), c.b) / 255.0;
    }
}
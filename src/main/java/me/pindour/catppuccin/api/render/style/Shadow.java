package me.pindour.catppuccin.api.render.style;

import meteordevelopment.meteorclient.utils.render.color.Color;

public class Shadow {
    private static final Shadow NONE = new Shadow(0, 0, 0, 0, new Color(0, 0, 0, 0));

    public final double offsetX;
    public final double offsetY;
    public final double blur;
    public final double spread;
    public final Color color;

    private Shadow(double offsetX, double offsetY, double blur, double spread, Color color) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.blur = Math.max(blur, 0);
        this.spread = spread;
        this.color = color;
    }

    public static Shadow of(double offsetX, double offsetY, double blur, double spread, Color color) {
        return new Shadow(offsetX, offsetY, blur, spread, color);
    }

    public static Shadow none() {
        return NONE;
    }

    public boolean isVisible() {
        return color.a > 0 && (offsetX != 0 || offsetY != 0 || blur != 0 || spread != 0);
    }

    /**
     * The horizontal padding a quad must be expanded by so the shadow is not clipped
     */
    public double padX() {
        return blur + Math.max(spread, 0) + Math.abs(offsetX);
    }

    /**
     * The vertical padding a quad must be expanded by so the shadow is not clipped
     */
    public double padY() {
        return blur + Math.max(spread, 0) + Math.abs(offsetY);
    }
}

package me.pindour.catppuccin.api.text;

public enum TextScale {
    SMALL(0.85),
    NORMAL(1),
    LARGE(1.15);

    private final double scale;

    TextScale(double scale) {
        this.scale = scale;
    }

    public double get() {
        return scale;
    }
}

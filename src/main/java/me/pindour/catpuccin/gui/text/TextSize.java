package me.pindour.catpuccin.gui.text;

public enum TextSize {
    SMALL(0.85),
    NORMAL(1),
    LARGE(1.15);

    private final double scale;

    TextSize(double scale) {
        this.scale = scale;
    }

    public double get() {
        return scale;
    }
}

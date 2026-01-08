package me.pindour.catppuccin.gui.text;

public class RichTextSegment {
    private final String text;
    private FontStyle style;
    private boolean shadow;
    private double scale;

    public RichTextSegment(String text) {
        this.text = text == null ? "" : text;
        this.style = FontStyle.REGULAR;
        this.shadow = false;
        this.scale = TextSize.NORMAL.get();
    }

    public String getText() {
        return text;
    }

    public FontStyle getStyle() {
        return style;
    }

    public void setStyle(FontStyle style) {
        this.style = style;
    }

    public boolean hasShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}

package me.pindour.catppuccin.api.render.shape;

import me.pindour.catppuccin.api.render.style.Corners;
import me.pindour.catppuccin.api.render.style.Outline;
import me.pindour.catppuccin.api.render.RoundedRectRenderer;
import me.pindour.catppuccin.api.render.style.Shadow;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.jetbrains.annotations.ApiStatus;

public class RoundedRect {
    private static final RoundedRect INSTANCE = new RoundedRect();

    private RoundedRectRenderer renderer;

    private double x, y, width, height;
    private float rTopLeft, rTopRight, rBottomLeft, rBottomRight;
    private Color fillColor;
    private Outline outline;
    private Shadow shadow;

    private RoundedRect() {}

    public static RoundedRect get() {
        INSTANCE.reset();
        return INSTANCE;
    }

    /**
     * Internal use only. Do not call this manually.
     */
    @ApiStatus.Internal
    public void registerRenderer(RoundedRectRenderer renderer) {
        this.renderer = renderer;
    }

    private void reset() {
        this.fillColor = Color.BLACK;
        this.rTopLeft = this.rTopRight = this.rBottomLeft = this.rBottomRight = 0;
        this.outline = Outline.none();
        this.shadow = Shadow.none();
    }

    public RoundedRect pos(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public RoundedRect size(double width, double height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public RoundedRect bounds(WWidget widget) {
        return pos(widget.x, widget.y).size(widget.width, widget.height);
    }

    public RoundedRect radius(double radius) {
        this.rTopLeft = this.rTopRight = this.rBottomLeft = this.rBottomRight = (float) radius;
        return this;
    }

    public RoundedRect radius(float radius, Corners corners) {
        return this.radii(
                corners.topLeft ? radius : 0,
                corners.topRight ? radius : 0,
                corners.bottomLeft ? radius : 0,
                corners.bottomRight ? radius : 0
        );
    }

    public RoundedRect radii(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        this.rTopLeft = topLeft;
        this.rTopRight = topRight;
        this.rBottomLeft = bottomLeft;
        this.rBottomRight = bottomRight;
        return this;
    }

    public RoundedRect color(Color fill) {
        this.fillColor = fill.copy();
        return this;
    }

    public RoundedRect outline(Color color, float width) {
        this.outline = Outline.of(width, color.copy());
        return this;
    }

    public RoundedRect shadow(Shadow shadow) {
        this.shadow = shadow;
        return this;
    }

    public RoundedRect shadow(double offsetX, double offsetY, double blur, double spread, Color color) {
        this.shadow = Shadow.of(offsetX, offsetY, blur, spread, color.copy());
        return this;
    }

    public RoundedRect shadow(double blur, double spread, Color color) {
        this.shadow = Shadow.of(0, 0, blur, spread, color.copy());
        return this;
    }

    public void render() {
        if (width <= 0 || height <= 0) return;
        if (fillColor.a == 0 && !outline.isVisible() && !shadow.isVisible()) return;

        if (renderer != null) {
            renderer.renderRoundedRect(
                    x, y,
                    width, height,
                    rTopLeft, rTopRight,
                    rBottomLeft, rBottomRight,
                    fillColor,
                    outline,
                    shadow
            );
        }
    }
}
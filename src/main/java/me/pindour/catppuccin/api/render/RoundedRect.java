package me.pindour.catppuccin.api.render;

import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.jetbrains.annotations.ApiStatus;

public class RoundedRect {
    private static final RoundedRect INSTANCE = new RoundedRect();

    private RoundedRectRenderer renderer;

    private double x, y, width, height;
    private float rTopLeft, rTopRight, rBottomLeft, rBottomRight;
    private Color fillColor;
    private Color outlineColor;
    private float outlineWidth;

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
        this.outlineColor = Color.BLACK;
        this.outlineWidth = 0;
        this.rTopLeft = this.rTopRight = this.rBottomLeft = this.rBottomRight = 0;
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
        this.outlineColor = color.copy();
        this.outlineWidth = width;
        return this;
    }

    public void render() {
        if (width <= 0 || height <= 0) return;
        if (fillColor.a == 0 && (outlineColor.a == 0 || outlineWidth <= 0)) return;

        if (renderer != null) {
            renderer.renderRoundedRect(
                    x, y,
                    width, height,
                    rTopLeft, rTopRight,
                    rBottomLeft, rBottomRight,
                    fillColor, outlineColor, outlineWidth
            );
        }
    }
}
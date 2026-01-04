package me.pindour.catpuccin.gui.themes.catpuccin;

import me.pindour.catpuccin.gui.renderer.CatpuccinRenderer;
import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.utils.BaseWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;

public interface CatpuccinWidget extends BaseWidget {

    // Getters

    default CatpuccinGuiTheme theme() {
        return (CatpuccinGuiTheme) getTheme();
    }

    default CatpuccinRenderer renderer() {
        return CatpuccinRenderer.get();
    }

    // Styling

    default int radius() {
        return theme().cornerRadius.get();
    }

    default int smallRadius() {
        return theme().smallCornerRadius.get();
    }

    default CornerStyle cornerStyle() {
        return CornerStyle.ALL;
    }

    default int outlineWidth() {
        return 2;
    }

    // Outline

    default void drawOutline(WWidget widget, Color color) {
        drawOutline(widget, color, smallRadius(), cornerStyle());
    }

    default void drawOutline(WWidget widget, Color outlineColor, int radius, CornerStyle style) {
        renderer().roundedRect(
                widget,
                radius,
                outlineColor,
                style
        );
    }

    // Background

    default void drawBackground(WWidget widget, int inset, Color color) {
        renderer().roundedRect(
                widget.x + inset,
                widget.y + inset,
                widget.width - inset * 2,
                widget.height - inset * 2,
                smallRadius() - inset,
                color,
                cornerStyle()
        );
    }

    // Rendering

    default void renderBackground(WWidget widget, Color outlineColor, Color backgroundColor, boolean forceOutline) {
        boolean drawOutline = forceOutline || (theme().widgetOutline.get() && outlineColor.a > 0);
        int inset = drawOutline ? outlineWidth() : 0;

        if (drawOutline) drawOutline(widget, outlineColor);
        drawBackground(widget, inset, backgroundColor);
    }

    default void renderBackground(WWidget widget, Color outlineColor, Color backgroundColor) {
        renderBackground(widget, outlineColor, backgroundColor, false);
    }

    default void renderBackground(WWidget widget, boolean pressed, boolean mouseOver) {
        renderBackground(widget, getOutlineColor(pressed, mouseOver), getBackgroundColor(pressed, mouseOver));
    }

    // Colors

    default Color getBackgroundColor(boolean pressed, boolean mouseOver) {
        CatpuccinGuiTheme theme = theme();

        return ColorUtils.withAlpha(
                theme.backgroundColor.get(pressed, mouseOver),
                theme.backgroundOpacity()
        );
    }

    default Color getOutlineColor(boolean pressed, boolean mouseOver) {
        CatpuccinGuiTheme theme = theme();

        return ColorUtils.withAlpha(
                theme.outlineColor.get(pressed, mouseOver),
                theme.backgroundOpacity() * 0.4
        );
    }
}
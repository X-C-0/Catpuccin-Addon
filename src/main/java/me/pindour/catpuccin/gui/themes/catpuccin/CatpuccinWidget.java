package me.pindour.catpuccin.gui.themes.catpuccin;

import me.pindour.catpuccin.gui.renderer.CatpuccinRenderer;
import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.utils.BaseWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;

public interface CatpuccinWidget extends BaseWidget {
    default CatpuccinGuiTheme theme() {
        return (CatpuccinGuiTheme) getTheme();
    }

    default CatpuccinRenderer catpuccinRenderer() {
        return CatpuccinRenderer.get();
    }

    default int cornerRadius() {
        return theme().cornerRadius.get();
    }

    default int smallCornerRadius() {
        return theme().smallCornerRadius.get();
    }

    default void drawOutline(WWidget widget, Color outlineColor) {
        catpuccinRenderer().roundedRect(
                widget,
                smallCornerRadius(),
                outlineColor,
                CornerStyle.ALL
        );
    }

    default void drawBackground(WWidget widget, int outlineOffset, Color backgroundColor) {
        catpuccinRenderer().roundedRect(
                widget.x + outlineOffset,
                widget.y + outlineOffset,
                widget.width - outlineOffset * 2,
                widget.height - outlineOffset * 2,
                smallCornerRadius() - outlineOffset,
                backgroundColor,
                CornerStyle.ALL
        );
    }

    default void renderBackground(WWidget widget, Color outlineColor, Color backgroundColor, boolean forceOutline) {
        CatpuccinGuiTheme theme = theme();
        boolean drawOutline = forceOutline || (theme.widgetOutline.get() && outlineColor.a > 0);
        int outlineOffset = drawOutline ? 2 : 0;

        if (drawOutline) drawOutline(widget, outlineColor);
        drawBackground(widget, outlineOffset, backgroundColor);
    }

    default void renderBackground(WWidget widget, Color outlineColor, Color backgroundColor) {
        renderBackground(widget, outlineColor, backgroundColor, false);
    }

    default void renderBackground(WWidget widget, boolean pressed, boolean mouseOver) {
        CatpuccinGuiTheme theme = theme();

        Color outlineColor = ColorUtils.withAlpha(
                theme.outlineColor.get(pressed, mouseOver),
                theme.backgroundOpacity() * 0.4
        );

        Color backgroundColor = ColorUtils.withAlpha(
                theme.backgroundColor.get(pressed, mouseOver),
                theme.backgroundOpacity()
        );

        renderBackground(widget, outlineColor, backgroundColor);
    }
}
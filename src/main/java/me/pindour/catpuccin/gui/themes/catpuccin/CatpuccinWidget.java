package me.pindour.catpuccin.gui.themes.catpuccin;

import me.pindour.catpuccin.gui.renderer.CatpuccinRenderer;
import me.pindour.catpuccin.gui.renderer.CornerStyle;
import meteordevelopment.meteorclient.gui.utils.BaseWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;

public interface CatpuccinWidget extends BaseWidget {
    int cornerRadius = 10;
    int smallCornerRadius = 6;

    default CatpuccinGuiTheme theme() {
        return (CatpuccinGuiTheme) getTheme();
    }

    default CatpuccinRenderer catpuccinRenderer() {
        return CatpuccinRenderer.get();
    }

    default void renderBackground(WWidget widget, Color outlineColor, Color backgroundColor) {
        CatpuccinGuiTheme theme = theme();
        boolean drawOutline = theme.widgetOutline.get();
        double s = drawOutline ? theme.scale(2) : 0;

        // Outline
        if (drawOutline)
            catpuccinRenderer().roundedRect(
                    widget,
                    smallCornerRadius,
                    outlineColor,
                    CornerStyle.ALL
            );

        // Background
        catpuccinRenderer().roundedRect(
                widget.x + s,
                widget.y + s,
                widget.width - s * 2,
                widget.height - s * 2,
                smallCornerRadius - s,
                backgroundColor.copy().a(theme.backgroundOpacity()),
                CornerStyle.ALL
        );
    }

    default void renderBackground(WWidget widget, boolean pressed, boolean mouseOver) {
        renderBackground(
                widget,
                theme().outlineColor.get(pressed, mouseOver),
                theme().backgroundColor.get(pressed, mouseOver).copy().a(theme().backgroundOpacity())
        );
    }
}

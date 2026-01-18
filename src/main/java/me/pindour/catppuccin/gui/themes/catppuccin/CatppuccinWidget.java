package me.pindour.catppuccin.gui.themes.catppuccin;

import me.pindour.catppuccin.api.render.RoundedRect;
import me.pindour.catppuccin.renderer.CatppuccinRenderer;
import me.pindour.catppuccin.api.render.Corners;
import me.pindour.catppuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.utils.BaseWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;

public interface CatppuccinWidget extends BaseWidget {

    // Getters

    default CatppuccinGuiTheme theme() {
        return (CatppuccinGuiTheme) getTheme();
    }

    default CatppuccinRenderer renderer() {
        return CatppuccinRenderer.get();
    }

    default RoundedRect roundedRect() {
        return RoundedRect.get();
    }

    // Styling

    default int radius() {
        return theme().cornerRadius.get();
    }

    default int smallRadius() {
        return theme().smallCornerRadius.get();
    }

    default Corners corners() {
        return Corners.ALL;
    }

    default float outlineWidth() {
        return 2f;
    }

    // Rendering

    default RoundedRect background(Color backgroundColor, Color outlineColor) {
        return roundedRect().bounds((WWidget) this)
                            .radius(smallRadius(), corners())
                            .color(backgroundColor)
                            .outline(outlineColor, outlineWidth());
    }

    default RoundedRect background(boolean pressed, boolean mouseOver) {
        return background(getBackgroundColor(pressed, mouseOver), getOutlineColor(pressed, mouseOver));
    }

    // Colors

    default Color getBackgroundColor(boolean pressed, boolean mouseOver) {
        CatppuccinGuiTheme theme = theme();

        return ColorUtils.withAlpha(
                theme.backgroundColor.get(pressed, mouseOver),
                theme.backgroundOpacity()
        );
    }

    default Color getOutlineColor(boolean pressed, boolean mouseOver) {
        CatppuccinGuiTheme theme = theme();

        return ColorUtils.withAlpha(
                theme.outlineColor.get(pressed, mouseOver),
                theme.backgroundOpacity() * 0.5
        );
    }
}
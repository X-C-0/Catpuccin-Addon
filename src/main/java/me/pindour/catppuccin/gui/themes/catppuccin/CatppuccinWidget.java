package me.pindour.catppuccin.gui.themes.catppuccin;

import me.pindour.catppuccin.api.render.shape.RoundedRect;
import me.pindour.catppuccin.api.render.style.Shadow;
import me.pindour.catppuccin.renderer.CatppuccinRenderer;
import me.pindour.catppuccin.api.render.style.Corners;
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

    default float radius() {
        return (float) (theme().scale(theme().cornerRadius.get()));
    }

    default float smallRadius() {
        return (float) (theme().scale(theme().smallCornerRadius.get()));
    }

    default Corners corners() {
        return Corners.ALL;
    }

    default float outlineWidth() {
        return 2f;
    }

    default Shadow shadow() {
        CatppuccinGuiTheme theme = theme();
        Color shadowColor = ColorUtils.withAlpha(Color.BLACK, theme.windowOpacity());

        return Shadow.of(
                0, 0,
                theme.shadowBlur.get(),
                theme.shadowSpread.get(),
                shadowColor
        );
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
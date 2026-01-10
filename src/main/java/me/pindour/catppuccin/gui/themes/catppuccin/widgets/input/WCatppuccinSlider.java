package me.pindour.catppuccin.gui.themes.catppuccin.widgets.input;

import me.pindour.catppuccin.renderer.CornerStyle;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatppuccinSlider extends WSlider implements CatppuccinWidget {

    public WCatppuccinSlider(double value, double min, double max) {
        super(value, min, max);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatppuccinGuiTheme theme = theme();

        renderBar(theme);
        renderHandle(renderer, theme);
    }

    private void renderBar(CatppuccinGuiTheme theme) {
        double backgroundHeight = handleSize() / 3;
        double backgroundY = y + height / 2 - backgroundHeight / 2;

        // Left
        renderer().roundedRect(
                x,
                backgroundY,
                valueWidth(),
                backgroundHeight,
                smallRadius(),
                theme.surface2Color(),
                CornerStyle.LEFT
        );

        // Right
        renderer().roundedRect(
                x + valueWidth(),
                backgroundY,
                width - valueWidth(),
                backgroundHeight,
                smallRadius(),
                theme.surface0Color(),
                CornerStyle.RIGHT
        );
    }

    private void renderHandle(GuiRenderer renderer, CatppuccinGuiTheme theme) {
        Color color = (handleMouseOver ? theme.accentColor() : theme.surface2Color());
        double size = handleSize();

        // TODO: FIX vlevo a vpravo neni gunguje drag
        double valueWidth = valueWidth();
        double handleX = x + valueWidth;
        double handleY = y + height / 2 - size / 2;

        if (theme.roundedCorners.get()) renderer.quad(handleX, handleY, size, size, GuiRenderer.CIRCLE, color);
        else renderer.quad(handleX, handleY, size, size, color);
    }
}

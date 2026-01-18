package me.pindour.catppuccin.gui.themes.catppuccin.widgets.input;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.api.render.Corners;
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
        roundedRect().pos(x, backgroundY)
                     .size(valueWidth(), backgroundHeight)
                     .radius(smallRadius(), Corners.LEFT)
                     .color(theme.surface2Color())
                     .render();

        // Right
        roundedRect().pos(x + valueWidth(), backgroundY)
                     .size(width - valueWidth(), backgroundHeight)
                     .radius(smallRadius(), Corners.RIGHT)
                     .color(theme.surface0Color())
                     .render();
    }

    private void renderHandle(GuiRenderer renderer, CatppuccinGuiTheme theme) {
        Color color = (handleMouseOver ? theme.accentColor() : theme.surface2Color());
        double size = handleSize();
        
        double valueWidth = valueWidth();
        double handleX = x + valueWidth;
        double handleY = y + height / 2 - size / 2;

        renderer.quad(handleX, handleY, size, size, GuiRenderer.CIRCLE, color);
    }
}

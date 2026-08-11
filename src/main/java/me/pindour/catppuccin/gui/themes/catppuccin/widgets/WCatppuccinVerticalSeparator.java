package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WVerticalSeparator;

public class WCatppuccinVerticalSeparator extends WVerticalSeparator implements CatppuccinWidget {
    public double size = 2;

    @Override
    protected void onCalculateSize() {
        width = theme.scale(size);
        height = 1;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatppuccinGuiTheme theme = theme();

        roundedRect().bounds(this)
                    .radius(smallRadius())
                    .color(theme.surface0Color())
                    .render();
    }
}

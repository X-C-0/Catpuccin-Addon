package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WVerticalSeparator;

public class WCatppuccinVerticalSeparator extends WVerticalSeparator implements CatppuccinWidget {

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatppuccinGuiTheme theme = theme();
        double s = theme.scale(1);

        renderer.quad(
                x,
                y,
                s,
                height,
                theme.surface0Color()
        );
    }
}

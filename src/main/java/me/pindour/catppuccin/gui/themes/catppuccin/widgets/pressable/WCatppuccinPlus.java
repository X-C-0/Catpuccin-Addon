package me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;

public class WCatppuccinPlus extends WPlus implements CatppuccinWidget {

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatppuccinGuiTheme theme = theme();
        double pad = pad();
        double s = theme.scale(3);

        background(pressed, mouseOver).render();
        renderer.quad(x + pad, y + height / 2 - s / 2, width - pad * 2, s, theme.greenColor());
        renderer.quad(x + width / 2 - s / 2, y + pad, s, height - pad * 2, theme.greenColor());
    }
}

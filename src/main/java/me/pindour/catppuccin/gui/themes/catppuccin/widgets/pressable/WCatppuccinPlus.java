package me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.themes.catppuccin.icons.CatppuccinBuiltinIcons;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;

public class WCatppuccinPlus extends WPlus implements CatppuccinWidget {

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatppuccinGuiTheme theme = theme();
        double pad = pad();
        double s = theme.textHeight();

        background(pressed, mouseOver).render();

        renderer.quad(
                x + pad,
                y + pad,
                s,
                s,
                CatppuccinBuiltinIcons.PLUS.texture(),
                theme.greenColor()
        );
    }
}

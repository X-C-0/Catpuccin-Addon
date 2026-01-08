package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WTooltip;

public class WCatppuccinTooltip extends WTooltip implements CatppuccinWidget {

    public WCatppuccinTooltip(String text) {
        super(text);
    }

    @Override
    public void init() {
        add(theme.label(text)).padVertical(4).padHorizontal(6);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderBackground(this, false, false);
    }
}

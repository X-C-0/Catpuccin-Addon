package me.pindour.catpuccin.gui.themes.catpuccin.widgets;

import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WTooltip;

public class WCatpuccinTooltip extends WTooltip implements CatpuccinWidget {

    public WCatpuccinTooltip(String text) {
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

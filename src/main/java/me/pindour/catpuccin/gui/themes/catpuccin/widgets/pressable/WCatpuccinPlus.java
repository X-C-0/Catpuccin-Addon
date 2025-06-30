package me.pindour.catpuccin.gui.themes.catpuccin.widgets.pressable;

import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;

public class WCatpuccinPlus extends WPlus implements CatpuccinWidget {

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatpuccinGuiTheme theme = theme();
        double pad = pad();
        double s = theme.scale(3);

        renderBackground(this, pressed, mouseOver);
        renderer.quad(x + pad, y + height / 2 - s / 2, width - pad * 2, s, theme.greenColor());
        renderer.quad(x + width / 2 - s / 2, y + pad, s, height - pad * 2, theme.greenColor());
    }
}

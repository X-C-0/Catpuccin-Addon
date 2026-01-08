package me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.widgets.pressable.WOpenIndicator;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;

public class WCatppuccinOpenIndicator extends WOpenIndicator implements CatppuccinWidget {

    public WCatppuccinOpenIndicator(boolean open) {
        super(open);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double pad = pad() * 1.5;
        double s = theme.scale(1.5);

        if (open) renderOpen(renderer, pad, s);
        else renderClosed(renderer, pad, s);
    }

    private void renderOpen(GuiRenderer renderer, double pad, double s) {
        renderer.quad(x + pad, y + height / 2 - s / 2, width - pad * 2, s, theme().overlay2Color());
    }

    private void renderClosed(GuiRenderer renderer, double pad, double s) {
        renderer.quad(x + pad, y + height / 2 - s / 2, width - pad * 2, s, theme().overlay2Color());
        renderer.quad(x + width / 2 - s / 2, y + pad, s, height - pad * 2, theme().overlay2Color());
    }
}

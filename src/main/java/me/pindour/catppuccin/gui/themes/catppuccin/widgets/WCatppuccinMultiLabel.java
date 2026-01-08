package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.gui.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WMultiLabel;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatppuccinMultiLabel extends WMultiLabel implements CatppuccinWidget {

    public WCatppuccinMultiLabel(RichText text, double maxWidth) {
        super(text.getPlainText(), false, maxWidth);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double h = theme.textHeight(title);
        Color defaultColor = theme().textColor();

        for (int i = 0; i < lines.size(); i++) {
            renderer().text(
                    RichText.of(lines.get(i)).boldIf(title),
                    x,
                    y + h * i,
                    color != null ? color : defaultColor
            );
        }
    }
}

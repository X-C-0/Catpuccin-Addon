package me.pindour.catpuccin.gui.themes.catpuccin.widgets;

import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WMultiLabel;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatpuccinMultiLabel extends WMultiLabel implements CatpuccinWidget {

    public WCatpuccinMultiLabel(RichText text, double maxWidth) {
        super(text.getPlainText(), false, maxWidth);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double h = theme.textHeight(title);
        Color defaultColor = theme().textColor();

        for (int i = 0; i < lines.size(); i++) {
            catpuccinRenderer().text(
                    RichText.of(lines.get(i)).boldIf(title),
                    x,
                    y + h * i,
                    color != null ? color : defaultColor
            );
        }
    }
}

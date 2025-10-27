package me.pindour.catpuccin.gui.themes.catpuccin.widgets.pressable;

import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.themes.catpuccin.icons.CatpuccinIcons;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WFavorite;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatpuccinFavorite extends WFavorite implements CatpuccinWidget {
    public WCatpuccinFavorite(boolean checked) {
        super(checked);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double pad = pad();
        double s = theme.textHeight();

        renderer.quad(
                x + pad,
                y + pad,
                s,
                s,
                checked ? CatpuccinIcons.BOOKMARK_YES.texture() : CatpuccinIcons.BOOKMARK_NO.texture(),
                getColor()
        );
    }

    @Override
    protected Color getColor() {
        return theme().textColor();
    }
}

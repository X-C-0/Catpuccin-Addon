package me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.themes.catppuccin.icons.CatppuccinBuiltinIcons;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WFavorite;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatppuccinFavorite extends WFavorite implements CatppuccinWidget {
    double size;

    public WCatppuccinFavorite(boolean checked) {
        super(checked);
    }

    @Override
    public void init() {
        size = theme.textHeight();
    }

    @Override
    protected void onCalculateSize() {
        width = size;
        height = size;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quad(
                x,
                y,
                size,
                size,
                checked ? CatppuccinBuiltinIcons.BOOKMARK_YES.texture() : CatppuccinBuiltinIcons.BOOKMARK_NO.texture(),
                getColor()
        );
    }

    @Override
    protected Color getColor() {
        return checked
                ? theme().accentColor()
                : mouseOver
                    ? theme().textSecondaryColor()
                    : theme().textColor();
    }
}

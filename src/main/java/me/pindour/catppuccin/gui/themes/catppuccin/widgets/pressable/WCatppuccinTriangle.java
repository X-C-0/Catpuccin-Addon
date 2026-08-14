package me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.themes.catppuccin.icons.CatppuccinBuiltinIcons;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;

public class WCatppuccinTriangle extends WTriangle implements CatppuccinWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double s = theme.textHeight();

        renderer.rotatedQuad(
                x,
                y,
                s,
                s,
                rotation,
                CatppuccinBuiltinIcons.ARROW.texture(),
                theme.textColor()
        );
    }
}

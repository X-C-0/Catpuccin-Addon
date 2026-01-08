package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.widgets.WGuiTexture;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;

public class WCatppuccinGuiTexture extends WGuiTexture implements CatppuccinWidget {

    public WCatppuccinGuiTexture(GuiTexture texture, double size) {
        super(texture, size);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quad(x, y, size, size, texture, theme().textColor());
    }
}

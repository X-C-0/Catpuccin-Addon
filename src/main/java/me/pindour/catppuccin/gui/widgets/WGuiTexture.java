package me.pindour.catppuccin.gui.widgets;

import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.widgets.WWidget;

public abstract class WGuiTexture extends WWidget {
    public GuiTexture texture;
    public double size;

    public WGuiTexture(GuiTexture texture, double size) {
        this.texture = texture;
        this.size = size;
    }

    @Override
    protected void onCalculateSize() {
        width = size;
        height = size;
    }
}

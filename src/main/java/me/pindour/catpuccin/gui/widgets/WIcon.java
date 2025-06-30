package me.pindour.catpuccin.gui.widgets;

import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.widgets.WWidget;

public abstract class WIcon extends WWidget {
    public GuiTexture texture;
    public double size;

    public WIcon(double size, GuiTexture texture) {
        this.texture = texture;
        this.size = size;
    }

    @Override
    protected void onCalculateSize() {
        width = size;
        height = size;
    }
}

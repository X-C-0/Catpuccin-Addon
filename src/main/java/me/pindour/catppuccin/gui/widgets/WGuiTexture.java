package me.pindour.catppuccin.gui.widgets;

import me.pindour.catppuccin.utils.WidgetUtils;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;

public abstract class WGuiTexture extends WWidget {
    public GuiTexture texture;
    public Color color;
    public double size;

    public WGuiTexture(GuiTexture texture, double size) {
        this.texture = texture;
        this.size = size;
        WidgetUtils.enableInstantTooltips(this);
    }

    @Override
    public void init() {
        if (color == null) color = theme.textColor();
    }

    @Override
    protected void onCalculateSize() {
        width = size;
        height = size;
    }

    public WGuiTexture color(Color color) {
        this.color = color;
        return this;
    }
}

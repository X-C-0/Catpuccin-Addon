package me.pindour.catppuccin.gui.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.utils.render.color.Color;

public abstract class WColorPicker extends WPressable {
    protected GuiTexture overlayTexture;
    protected Color color;

    public WColorPicker(Color color, GuiTexture overlayTexture) {
        this.color = color;
        this.overlayTexture = overlayTexture;
    }

    @Override
    protected void onCalculateSize() {
        double s = theme.textHeight();

        width = s * 3;
        height = s * 1.5;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

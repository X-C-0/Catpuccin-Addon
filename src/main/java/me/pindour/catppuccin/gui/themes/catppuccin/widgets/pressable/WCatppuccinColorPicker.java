package me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable;

import me.pindour.catppuccin.renderer.CornerStyle;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.widgets.pressable.WColorPicker;
import me.pindour.catppuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatppuccinColorPicker extends WColorPicker implements CatppuccinWidget {

    public WCatppuccinColorPicker(Color color, GuiTexture overlayTexture) {
        super(color, overlayTexture);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer().roundedRect(
                x, y,
                width, height,
                smallRadius(),
                mouseOver ? ColorUtils.darker(color) : color,
                CornerStyle.ALL
        );

        if (mouseOver) {
            double s = theme.textHeight();

            renderer.quad(
                    x + width / 2 - s / 2,
                    y + height / 2 - s / 2,
                    s,
                    s,
                    overlayTexture,
                    theme().textColor()
            );
        }
    }
}

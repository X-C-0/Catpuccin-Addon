package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.api.render.style.Corners;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WQuad;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatppuccinQuad extends WQuad implements CatppuccinWidget {
    public Corners corners;
    public float radius;
    public double minHeight;

    public WCatppuccinQuad(Color color) {
        super(color);
    }

    @Override
    protected void onCalculateSize() {
        double s = theme.scale(32);

        width = s;
        height = Math.max(minHeight, s);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (corners != null && radius > 0)
            roundedRect().bounds(this)
                         .radius(radius, corners)
                         .color(color)
                         .render();

        else renderer.quad(x, y, width, height, color);
    }


}

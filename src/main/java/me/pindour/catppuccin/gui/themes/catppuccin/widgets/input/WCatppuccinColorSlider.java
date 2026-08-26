package me.pindour.catppuccin.gui.themes.catppuccin.widgets.input;

import me.pindour.catppuccin.api.render.style.Corners;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatppuccinColorSlider extends WCatppuccinSlider implements CatppuccinWidget {
    private final Color[] colors;

    public WCatppuccinColorSlider(double value, double min, double max, Color[] colors) {
        super(value, min, max);
        this.colors = colors;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderGradientBar(renderer);

        // Uh yeah this works I guess
        renderer.post(() -> {
            renderer.beginRender();
            renderHandle();
            renderer.endRender();
        });
    }

    private void renderGradientBar(GuiRenderer renderer) {
        double halfHandle = handleSize() / 2;
        double bgHeight = handleSize() * 0.7;
        double backgroundY = y + height / 2 - bgHeight / 2;

        // Gradient as N-1 quads across the bar width with gaps on both ends for rounded corners.
        double sectionWidth = (width - halfHandle) / (colors.length - 1);
        double sectionX = x + halfHandle / 2;

        for (int i = 0; i < colors.length - 1; i++) {
            renderer.quad(
                    sectionX,
                    backgroundY,
                    sectionWidth,
                    bgHeight,
                    colors[i], colors[i + 1],
                    colors[i + 1], colors[i]
            );

            sectionX += sectionWidth;
        }

        // Left
        roundedRect().pos(x, backgroundY)
                     .size(halfHandle, bgHeight)
                     .radius(smallRadius(), Corners.LEFT)
                     .color(colors[0])
                     .render();

        // Right
        roundedRect().pos(x + width - halfHandle, backgroundY)
                     .size(halfHandle, bgHeight)
                     .radius(smallRadius(), Corners.RIGHT)
                     .color(colors[colors.length - 1])
                     .render();
    }

    @Override
    protected Color handleColor() {
        return theme().textColor();
    }
}

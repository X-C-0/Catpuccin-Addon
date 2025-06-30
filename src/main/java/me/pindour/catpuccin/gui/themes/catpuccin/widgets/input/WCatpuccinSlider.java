package me.pindour.catpuccin.gui.themes.catpuccin.widgets.input;

import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;

public class WCatpuccinSlider extends WSlider implements CatpuccinWidget {
    private double handlePadding;
    private double handleTextureSize;

    public WCatpuccinSlider(double value, double min, double max) {
        super(value, min, max);
    }

    @Override
    public void init() {
        super.init();
        handlePadding = theme().scale(1);
        handleTextureSize = handleSize() * 0.7;
    }

    @Override
    protected void onCalculateSize() {
        height = handleSize() + handlePadding * 2;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double valueWidth = valueWidth();

        renderBar(valueWidth);
        renderHandle(renderer, valueWidth);
    }

    private void renderBar(double valueWidth) {
        CatpuccinGuiTheme theme = theme();

        double handleSize = handleSize();
        double backgroundHeight = handleSize / 2;

        double bgX = x + handleSize / 2;
        double bgY = y + height / 2 - backgroundHeight / 2;

        // Background
        catpuccinRenderer().roundedRect(
                bgX + valueWidth,
                bgY,
                width - valueWidth - handleSize,
                backgroundHeight,
                smallCornerRadius,
                theme.accentColor().copy().a(120),
                CornerStyle.RIGHT
        );

        // Fill
        catpuccinRenderer().roundedRect(
                x,
                y + height / 2 - handleTextureSize / 2 - handlePadding,
                handlePadding * 2 + valueWidth + handleTextureSize,
                handleTextureSize + handlePadding * 2,
                smallCornerRadius,
                theme.accentColor(),
                CornerStyle.ALL
        );
    }

    private void renderHandle(GuiRenderer renderer, double valueWidth) {
        renderer.quad(
                x + valueWidth + handlePadding,
                y + height / 2 - handleTextureSize / 2,
                handleTextureSize,
                handleTextureSize,
                GuiRenderer.CIRCLE,
                theme().baseColor()
        );
    }
}

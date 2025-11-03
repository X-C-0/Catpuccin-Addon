package me.pindour.catpuccin.gui.themes.catpuccin.widgets.input;

import me.pindour.catpuccin.gui.animation.Animation;
import me.pindour.catpuccin.gui.animation.Direction;
import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.Click;

public class WCatpuccinSlider extends WSlider implements CatpuccinWidget {
    private double handlePadding;
    private double handleTextureSize;

    private Animation animation;

    public WCatpuccinSlider(double value, double min, double max) {
        super(value, min, max);
    }

    @Override
    public void init() {
        super.init();
        handlePadding = theme().scale(1);
        handleTextureSize = handleSize() * 0.6;

        animation = new Animation(theme().uiAnimationType(), theme().uiAnimationSpeed());
    }

    @Override
    protected void onCalculateSize() {
        height = handleSize() + handlePadding * 2;
    }

    @Override
    public boolean onMouseClicked(Click click, boolean used) {
        boolean clicked = super.onMouseClicked(click, used);
        if (clicked) animation.start(Direction.FORWARDS);
        return clicked;
    }

    @Override
    public boolean onMouseReleased(Click click) {
        boolean released = super.onMouseReleased(click);
        if (released) animation.start(Direction.BACKWARDS);
        return released;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        double valueWidth = valueWidth();

        renderBar(valueWidth);
        if (dragging || handleMouseOver) renderHandle(renderer, valueWidth);
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
                smallCornerRadius(),
                theme.accentColor().copy().a(120),
                CornerStyle.RIGHT
        );

        // Fill
        catpuccinRenderer().roundedRect(
                x,
                y + height / 2 - handleTextureSize / 2 - handlePadding,
                handlePadding * 2 + valueWidth + handleTextureSize,
                handleTextureSize + handlePadding * 2,
                smallCornerRadius(),
                theme.accentColor(),
                CornerStyle.ALL
        );
    }

    private void renderHandle(GuiRenderer renderer, double valueWidth) {
        double size = handleTextureSize * animation.getProgress();
        Color color = theme().baseColor();

        double handleX = x + valueWidth + handlePadding + handleTextureSize / 2 - size / 2;
        double handleY = y + height / 2 - size / 2;

        if (theme().roundedCorners.get()) renderer.quad(handleX, handleY, size, size, GuiRenderer.CIRCLE, color);
        else renderer.quad(handleX, handleY, size, size, color);
    }
}

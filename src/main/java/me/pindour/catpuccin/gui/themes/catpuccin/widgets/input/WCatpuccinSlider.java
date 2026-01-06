package me.pindour.catpuccin.gui.themes.catpuccin.widgets.input;

import me.pindour.catpuccin.gui.animation.Animation;
import me.pindour.catpuccin.gui.animation.Direction;
import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import meteordevelopment.meteorclient.utils.render.color.Color;

#if MC_VER >= MC_1_21_10
import net.minecraft.client.gui.Click;
#endif

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
#if MC_VER >= MC_1_21_10
    public boolean onMouseClicked(Click click, boolean used) {
        boolean clicked = super.onMouseClicked(click, used);
        handleClick(clicked);
        return clicked;
    }

    @Override
    public boolean onMouseReleased(Click click) {
        boolean released = super.onMouseReleased(click);
        handleRelease(released);
        return released;
    }
#else
    public boolean onMouseClicked(double mouseX, double mouseY, int button, boolean used) {
        boolean clicked = super.onMouseClicked(mouseX, mouseY, button, used);
        handleClick(clicked);
        return clicked;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        boolean released = super.onMouseReleased(mouseX, mouseY, button);
        handleRelease(released);
        return released;
    }
#endif

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
        double size = handleTextureSize * animation.getProgress();
        Color color = theme().baseColor();

        double handleX = x + valueWidth + handlePadding + handleTextureSize / 2 - size / 2;
        double handleY = y + height / 2 - size / 2;

        if (theme().roundedCorners.get()) {
            catpuccinRenderer().roundedRect(handleX, handleY, size, size, size * 0.5, color, CornerStyle.ALL);
        } else {
            renderer.quad(handleX, handleY, size, size, color);
        }
    }

    private void handleClick(boolean clicked) {
        if (clicked) animation.start(Direction.FORWARDS);
    }

    private void handleRelease(boolean released) {
        if (released) animation.start(Direction.BACKWARDS);
    }
}

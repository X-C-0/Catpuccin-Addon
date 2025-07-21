package me.pindour.catpuccin.gui.themes.catpuccin.widgets;

import me.pindour.catpuccin.gui.animation.Animation;
import me.pindour.catpuccin.gui.animation.Direction;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class WCatpuccinModule extends WPressable implements CatpuccinWidget {
    private final Module module;

    private double titleWidth;
    private boolean wasHovered = false;
    private boolean wasActive = false;

    private Animation glowAnimation;
    private Animation hoverAnimation;

    private Color highlightedColor;
    private Color semiTransparentColor;
    private Color transparentColor;

    public WCatpuccinModule(Module module) {
        this.module = module;
        this.tooltip = module.description;
    }

    @Override
    public void init() {
        boolean isActive = module.isActive();
        wasActive = isActive;

        glowAnimation = new Animation(theme().uiAnimationType(), theme().uiAnimationSpeed());
        glowAnimation.finishedAt(isActive ? Direction.FORWARDS : Direction.BACKWARDS);

        hoverAnimation = new Animation(theme().uiAnimationType(), theme().uiAnimationSpeed());

        highlightedColor = theme().accentColor().copy();
        transparentColor = highlightedColor.copy().a(10);
        semiTransparentColor = highlightedColor.copy().a(80);
    }

    @Override
    public double pad() {
        return theme.scale(4);
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();

        if (titleWidth == 0) titleWidth = theme.textWidth(module.title);

        width = pad + titleWidth + pad;
        height = pad + theme.textHeight() + pad;
    }

    @Override
    protected void onPressed(int button) {
        if (button == GLFW_MOUSE_BUTTON_LEFT)
            module.toggle();

        else if (button == GLFW_MOUSE_BUTTON_RIGHT)
            mc.setScreen(theme.moduleScreen(module));
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatpuccinGuiTheme theme = theme();
        boolean moduleActive = module.isActive();
        double pad = pad();

        // Glow animation handling
        if (moduleActive != wasActive) {
            wasActive = moduleActive;

            glowAnimation.start(moduleActive ? Direction.FORWARDS : Direction.BACKWARDS);
        }

        // Hover animation handling
        if (mouseOver != wasHovered) {
            wasHovered = mouseOver;

            // Start animation forwards when hovered
            if (mouseOver) hoverAnimation.start();
            // Set animation as finished when no longer hovered to prevent mouse tracing effect
            else hoverAnimation.finishedAt(Direction.BACKWARDS);
        }

        double hoverProgress = hoverAnimation.getProgress();
        double glowProgress = glowAnimation.getProgress();
        double highlightProgress = Math.min(glowProgress + hoverProgress, 1.0);

        if (highlightProgress > 0) {

            // Highlight rectangle when active or hovered
            int alpha = (int) (255 * highlightProgress);
            renderer.quad(x, y, width, height, highlightedColor.a(alpha));

            // Fake glow effect when active
            if (glowProgress > 0) {
                double glowLength = 6 * glowProgress;

                // Upper glow
                renderer.quad(
                        x,
                        y - glowLength,
                        width,
                        glowLength,
                        transparentColor,
                        transparentColor,
                        semiTransparentColor,
                        semiTransparentColor
                );

                // Lower glow
                renderer.quad(
                        x,
                        y + height,
                        width,
                        glowLength,
                        semiTransparentColor,
                        semiTransparentColor,
                        transparentColor,
                        transparentColor
                );
            }
        }

        // Text alignment
        double x = this.x + pad * 2;
        double w = width - pad * 2;

        switch (theme.moduleAlignment.get()) {
            case Center -> x += w / 2 - titleWidth / 2;
            case Right -> x += w - titleWidth - pad * 2;
        }

        // Text
        Color textColor = ColorUtils.interpolateColor(theme.textColor(), theme.baseColor(), highlightProgress);
        catpuccinRenderer().text(RichText.of(module.title), x, y + pad, textColor);
    }
}

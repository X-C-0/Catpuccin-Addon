package me.pindour.catpuccin.gui.themes.catpuccin.widgets;

import me.pindour.catpuccin.gui.animation.Animation;
import me.pindour.catpuccin.gui.animation.Direction;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
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

    private Animation glowAnimation;
    private Animation animation;

    private Color highlightedColor;
    private Color semiTransparentColor;
    private Color transparentColor;

    public WCatpuccinModule(Module module) {
        this.module = module;
        this.tooltip = module.description;
    }

    @Override
    public void init() {
        glowAnimation = new Animation(theme().uiAnimationType(), theme().uiAnimationSpeed());
        glowAnimation.setInitialState(module.isActive() ? Direction.FORWARDS : Direction.BACKWARDS);

        animation = new Animation(theme().uiAnimationType(), theme().uiAnimationSpeed());
        animation.setInitialState(module.isActive() ? Direction.FORWARDS : Direction.BACKWARDS);

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
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            module.toggle();
            glowAnimation.reverse();
        }
        else if (button == GLFW_MOUSE_BUTTON_RIGHT) mc.setScreen(theme.moduleScreen(module));
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatpuccinGuiTheme theme = theme();
        boolean moduleActive = module.isActive();
        double pad = pad();

        // Animation handling
        if (mouseOver != wasHovered) {
            if (mouseOver && !moduleActive)
                animation.start();

            else if (!mouseOver && !moduleActive)
                animation.reverse();

            wasHovered = mouseOver;
        }

        double progress = animation.getProgress();
        double glowProgress = glowAnimation.getProgress();

        if (progress > 0 || glowProgress > 0) {

            // Fake glow effect when active
            if (moduleActive || glowAnimation.isRunning()) {
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

            // Highlight rectangle
            int alpha = (int) (255 * (mouseOver ? progress : glowProgress));
            renderer.quad(x, y, width, height, highlightedColor.a(alpha));
        }

        // Text alignment
        double x = this.x + pad;
        double w = width - pad * 2;

        switch (theme.moduleAlignment.get()) {
            case Center -> x += w / 2 - titleWidth / 2;
            case Right -> x += w - titleWidth;
        }

        // Text
        Color textColor = interpolateColor(theme.textColor(), theme.baseColor(), progress);
        catpuccinRenderer().text(RichText.of(module.title), x, y + pad, textColor);
    }

    private Color interpolateColor(Color from, Color to, double t) {
        t = Math.max(0, Math.min(1, t));

        if (t == 1) return to;
        if (t == 0) return from;

        int r = (int) (from.r + (to.r - from.r) * t);
        int g = (int) (from.g + (to.g - from.g) * t);
        int b = (int) (from.b + (to.b - from.b) * t);
        int a = (int) (from.a + (to.a - from.a) * t);

        return new Color(r, g, b, a);
    }
}

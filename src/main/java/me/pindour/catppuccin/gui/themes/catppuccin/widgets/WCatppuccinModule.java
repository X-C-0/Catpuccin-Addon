package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.api.animation.Animation;
import me.pindour.catppuccin.api.animation.Direction;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class WCatppuccinModule extends WPressable implements CatppuccinWidget {
    private final Module module;
    private final String title;

    private double titleWidth;
    private boolean wasHovered = false;
    private boolean wasActive = false;
    private boolean isFirstInCategory = false;
    private boolean isLastInCategory = false;

    private Animation glowAnimation;
    private Animation hoverAnimation;

    private Color highlightedColor;
    private Color semiTransparentColor;
    private Color transparentColor;

    public WCatppuccinModule(Module module, String title) {
        this.module = module;
        this.title = title;
        this.tooltip = module.description;
    }

    @Override
    public void init() {
        boolean isActive = module.isActive();
        wasActive = isActive;

        glowAnimation = new Animation(
                theme().guiAnimationEasing(),
                theme().guiAnimationDuration(),
                isActive ? Direction.FORWARDS : Direction.BACKWARDS
        );

        hoverAnimation = new Animation(theme().guiAnimationEasing(), theme().guiAnimationDuration());

        highlightedColor = theme().accentColor().copy();
        transparentColor = highlightedColor.copy().a(10);
        semiTransparentColor = highlightedColor.copy().a(80);

        List<Module> modules = Modules.get().getGroup(module.category);
        isFirstInCategory = modules.getFirst().equals(module);
        isLastInCategory = modules.getLast().equals(module);
    }

    @Override
    public double pad() {
        return theme.scale(4);
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();

        if (titleWidth == 0) titleWidth = theme.textWidth(title);

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
        CatppuccinGuiTheme theme = theme();
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
            int alpha = (int) (255 * highlightProgress);

            // Highlight rectangle when active or hovered
            // Note: Should be rounded at the bottom when module is last in the category,
            // but the corners from window background are clipping into this rectangle,
            // so for now we will have to live with sharp corners until I find a way to fix the clipping.
            renderer.quad(x, y, width, height, highlightedColor.copy().a(alpha));

            // Fake glow effect when active
            if (glowProgress > 0) {
                double glowLength = 6 * glowProgress;

                // Upper glow
                if (!isFirstInCategory)
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
                if (!isLastInCategory)
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
        renderer().text(RichText.of(title), x, y + pad, textColor);
    }
}

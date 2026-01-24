package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.api.animation.Animation;
import me.pindour.catppuccin.api.animation.Direction;
import me.pindour.catppuccin.api.animation.Easing;
import me.pindour.catppuccin.api.render.Corners;
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
    private final RichText title;

    private double titleWidth;
    private boolean wasHovered = false;
    private boolean wasActive = false;

    private Module prevModule;
    private Module nextModule;

    private Animation highlightAnimation;
    private Animation hoverAnimation;

    public WCatppuccinModule(Module module, String title) {
        this.module = module;
        this.title = RichText.of(title);
        this.tooltip = module.description;
    }

    @Override
    public void init() {
        boolean isActive = module.isActive();
        wasActive = isActive;

        List<Module> categoryModules = Modules.get().getGroup(module.category);
        int index = categoryModules.indexOf(module);

        if (index > 0) prevModule = categoryModules.get(index - 1);
        if (index < categoryModules.size() - 1) nextModule = categoryModules.get(index + 1);

        highlightAnimation = new Animation(
                Easing.QUART_OUT,
                300,
                isActive ? Direction.FORWARDS : Direction.BACKWARDS
        );

        hoverAnimation = new Animation(Easing.LINEAR, 200);
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();
        if (titleWidth == 0) titleWidth = theme().textWidth(title);

        width = pad + pad + titleWidth + pad;
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

        // Highlight animation handling
        if (moduleActive != wasActive) {
            wasActive = moduleActive;

            highlightAnimation.start(moduleActive ? Direction.FORWARDS : Direction.BACKWARDS);
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
        double highlightProgress = highlightAnimation.getProgress();

        // Background rectangle when active or hovered
        if (hoverProgress > 0 || highlightProgress > 0) {
            // Mix the alpha to prevent blinking
            int baseAlpha = 60;
            double hoverMultiplier = (mouseOver && moduleActive) ? 1.3f : 1.0f;
            double mix = Math.min(1.0, highlightProgress + hoverProgress);
            double alpha = baseAlpha * mix * hoverMultiplier;

            Color color = ColorUtils.withAlpha(theme.accentColor(), (int) alpha);
            Corners bgCorners = (mouseOver && !moduleActive) ? Corners.ALL : corners();

            double maxOffset = 1.5;
            double offset = maxOffset * (1.0 - highlightProgress);

            roundedRect().pos(x + offset, y + offset)
                         .size(width - (offset * 2), height - (offset * 2)).
                         color(color)
                         .radius(smallRadius(), bgCorners)
                         .render();
        }

        // Cool ass line when active
        if (highlightProgress > 0) {
            Corners corners = corners();

            double offset = theme.scale(3);
            double offsetTop = isPrevActive() ? 0 : offset;
            double offsetBottom = isNextActive() ? 0 : offset;

            double lineWidth = theme.scale(4);
            double lineHeight = height - offsetTop - offsetBottom;

            double finalTop = y + offsetTop;
            double centerY = finalTop + lineHeight / 2;

            double lineX = x + pad;
            double lineY = centerY - (lineHeight * highlightProgress)/ 2;

            roundedRect().pos(lineX, lineY)
                         .size(lineWidth, lineHeight * highlightProgress)
                         .color(ColorUtils.withAlpha(theme.accentColor(), highlightProgress))
                         .radius(smallRadius(), corners)
                         .render();
        }

        double x = this.x + pad * 2;
        double w = width - pad * 2;

        switch (theme.moduleAlignment.get()) {
            case Center -> x += w / 2 - titleWidth / 2;
            case Right -> x += w - titleWidth - pad * 2;
            default -> x += pad;
        }

        Color color = ColorUtils.interpolateColor(
                theme.textColor(),
                theme.accentColor(),
                highlightProgress
        );

        renderer().text(title, x, y + pad, color);
    }

    @Override
    public Corners corners() {
        boolean prev = isPrevActive(), next = isNextActive();
        return prev && next ? Corners.NONE :
                prev ? Corners.BOTTOM :
                next ? Corners.TOP : Corners.ALL;
    }

    private boolean isPrevActive() {
        return prevModule != null && prevModule.isActive();
    }

    private boolean isNextActive() {
        return nextModule != null && nextModule.isActive();
    }
}
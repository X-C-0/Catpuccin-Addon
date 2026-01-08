package me.pindour.catppuccin.gui.themes.catppuccin.widgets.container;

import me.pindour.catppuccin.gui.animation.Animation;
import me.pindour.catppuccin.gui.animation.Direction;
import me.pindour.catppuccin.gui.renderer.CornerStyle;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.themes.catppuccin.icons.CatppuccinIcons;
import me.pindour.catppuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatppuccinSection extends WSection implements CatppuccinWidget {
    private double actualHeight;
    private double forcedHeight = -1;

    private WHeader header;

    private Animation animation;

    public WCatppuccinSection(String title, boolean expanded, WWidget headerWidget) {
        super(title, expanded, headerWidget);
    }

    @Override
    public void init() {
        super.init();

        animation = new Animation(
                theme().guiAnimationEasing(),
                theme().guiAnimationDuration(),
                expanded ? Direction.FORWARDS : Direction.BACKWARDS
        );
    }

    @Override
    protected void onCalculateSize() {
        super.onCalculateSize();

        actualHeight = height;

        if (animation.isRunning() || animation.getProgress() < 1) {
            forcedHeight = (actualHeight - header.height) * animation.getProgress() + header.height;
            height = forcedHeight;
        }

        if (headerWidget != null) {
            headerWidget.height = header.height * 0.8;
            headerWidget.width = header.height * 0.8;
        }
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (expanded || animation.isRunning()) renderBackground(this, false, false);
    }

    @Override
    public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        boolean isAnimationRunning = animation.isRunning();
        double progress = animation.getProgress();

        animProgress = expanded ? 1 : 0; // Small hack to cancel out WSection scissors, so we can use our animation

        if (isAnimationRunning) {
            forcedHeight = (actualHeight - header.height) * progress + header.height;
            invalidate();
        }

        if (isAnimationRunning) renderer.scissorStart(x, y, width, (height - header.height) * progress + header.height);
        boolean toReturn = super.render(renderer, mouseX, mouseY, delta);
        if (isAnimationRunning) renderer.scissorEnd();

        return toReturn;
    }

    @Override
    protected void renderWidget(WWidget widget, GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (expanded || animation.getProgress() > 0 || widget instanceof WHeader) {
            widget.render(renderer, mouseX, mouseY, delta);
        }
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        animation.reverse();
    }

    @Override
    protected WHeader createHeader() {
        header = new WCatppuccinHeader(title);
        return header;
    }

    @Override
    public CornerStyle cornerStyle() {
        return CornerStyle.BOTTOM;
    }

    @Override
    public void drawOutline(WWidget widget, Color color, int radius, CornerStyle style) {
        Color outlineColor = ColorUtils.withAlpha(
                theme().surface0Color(),
                theme().backgroundOpacity()
        );

        renderer().roundedRect(
                x,
                y + header.height,
                width,
                height - header.height,
                radius(),
                outlineColor,
                style
        );
    }

    @Override
    public void drawBackground(WWidget widget, int inset, Color color) {
        Color backgroundColor = ColorUtils.withAlpha(
                theme().baseColor(),
                theme().backgroundOpacity()
        );

        renderer().roundedRect(
                x + inset,
                y + header.height,
                width - inset * 2,
                height - header.height - inset,
                radius() - inset,
                backgroundColor,
                cornerStyle()
        );
    }

    protected class WCatppuccinHeader extends WHeader {

        public WCatppuccinHeader(String title) {
            super(title);
        }

        @Override
        public void init() {
            add(theme.horizontalSeparator(title)).expandX();

            if (headerWidget != null) add(headerWidget);
        }

        @Override
        protected void onCalculateSize() {
            super.onCalculateSize();
            height = theme.textHeight() * 1.5;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            CatppuccinGuiTheme theme = theme();
            double pad = pad();
            double s = theme.textHeight() * 0.75;

            Color bgColor = ColorUtils.withAlpha(
                    mouseOver ? theme.surface1Color() : theme.surface0Color(),
                    theme.backgroundOpacity()
            );

            // Background
            renderer().roundedRect(
                    this,
                    radius(),
                    bgColor,
                    expanded || animation.isRunning() ? CornerStyle.TOP : CornerStyle.ALL
            );

            if (headerWidget != null) return;

            // Expanded indicator
            renderer.rotatedQuad(
                    x + width - pad - s,
                    y + height / 2 - s / 2,
                    s,
                    s,
                    90 + 90 * animation.getProgress(),
                    CatppuccinIcons.ARROW.texture(),
                    theme.textColor()
            );
        }
    }
}

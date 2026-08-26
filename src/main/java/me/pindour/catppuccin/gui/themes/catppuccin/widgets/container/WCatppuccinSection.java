package me.pindour.catppuccin.gui.themes.catppuccin.widgets.container;

import me.pindour.catppuccin.api.animation.Animation;
import me.pindour.catppuccin.api.animation.Direction;
import me.pindour.catppuccin.api.animation.Easing;
import me.pindour.catppuccin.api.render.style.Corners;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatppuccinSection extends WSection implements CatppuccinWidget {
    private double actualHeight;
    private double forcedHeight = -1;
    private double contentOffsetY;

    private WHeader header;

    private Animation animation;
    private Animation cornerAnimation;

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
        cornerAnimation = new Animation(
                Easing.QUART_OUT,
                200,
                expanded ? Direction.FORWARDS : Direction.BACKWARDS
        );
    }

    @Override
    protected void onCalculateSize() {
        super.onCalculateSize();

        actualHeight = height;

        if (animation.isRunning() || animation.getProgress() < 1) {
            double contentHeight = actualHeight - header.height;
            double animatedHeight = Math.max(contentHeight * animation.getProgress(), 0);
            forcedHeight = animatedHeight + header.height;
            height = forcedHeight;
        }
    }

    @Override
    protected void onCalculateWidgetPositions() {
        super.onCalculateWidgetPositions();

        if (contentOffsetY != 0) {
            for (Cell<?> cell : cells) {
                if (cell.widget() != header) cell.move(0, contentOffsetY);
            }

            contentOffsetY = 0;
        }
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (!expanded && !animation.isRunning()) return;

        Color backgroundColor = ColorUtils.withAlpha(
                theme().baseColor(),
                theme().backgroundOpacity()
        );

        Color outlineColor = ColorUtils.withAlpha(
                theme().surface0Color(),
                theme().backgroundOpacity()
        );

        roundedRect().pos(x, y + header.height)
                     .size(width, height - header.height)
                     .radius(radius(), Corners.BOTTOM)
                     .color(backgroundColor)
                     .outline(outlineColor, outlineWidth())
                     .render();
    }

    @Override
    public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (!visible) return true;

        double progress = animation.getProgress();
        boolean isAnimating = animation.isRunning();
        double contentHeight = height - header.height;

        animProgress = expanded ? 1 : 0; // Small hack to cancel out WSection scissors, so we can use our animation

        if (isAnimating) {
            forcedHeight = (actualHeight - header.height) * progress + header.height;

            // Calculate overshot
            if (progress > 1.0) {
                double overshot = Math.max(progress - 1, 0) * contentHeight;
                double shift = overshot / 2;

                if (contentOffsetY != shift)
                    contentOffsetY = shift;
            }

            invalidate();

            renderer.scissorStart(x, y, width, contentHeight * progress + header.height);
        }

        boolean toReturn = super.render(renderer, mouseX, mouseY, delta);

        if (isAnimating) renderer.scissorEnd();

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
        if (expanded) cornerAnimation.finishedAt(Direction.FORWARDS);
    }

    @Override
    protected WHeader createHeader() {
        header = new WCatppuccinHeader(title);
        return header;
    }

    protected class WCatppuccinHeader extends WHeader {
        private WHorizontalList list;
        private WTriangle openIndicator;

        public WCatppuccinHeader(String title) {
            super(title);
        }

        @Override
        public void init() {
            list = add(theme.horizontalList())
                    .padHorizontal(theme.scale(6))
                    .padVertical(theme.scale(4))
                    .expandX()
                    .widget();

            WWidget widget = headerWidget;

            if (widget == null) {
                openIndicator = theme.triangle();
                widget = openIndicator;
            }

            // Calculate left offset to make the title centered
            widget.calculateSize();
            double pad = widget.width;

            add(theme.horizontalSeparator(title))
                    .expandX()
                    .padLeft(pad);

            add(widget);
        }

        @Override
        public <T extends WWidget> Cell<T> add(T widget) {
            if (list != null) return list.add(widget);
            return super.add(widget);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            CatppuccinGuiTheme theme = theme();
            double progress = animation.getProgress();
            double cornerProgress = cornerAnimation.getProgress();

            // Start corner animation if we're collapsing,
            // and we're almost at the end of the main animation
            if (!expanded
                && progress <= 0.1
                && !cornerAnimation.isRunning()
                && cornerProgress > 0) {
                cornerAnimation.start(Direction.BACKWARDS);
            }

            Color bgColor = ColorUtils.withAlpha(
                    mouseOver ? theme.surface1Color() : theme.surface0Color(),
                    theme.backgroundOpacity()
            );

            // Background
            roundedRect().bounds(this)
                         .radii(radius(),
                                radius(),
                                (float) (radius() * (1 - cornerProgress)),
                                (float) (radius() * (1 - cornerProgress)))
                         .color(bgColor)
                         .render();

            // Update open indicator
            if (openIndicator != null)
                openIndicator.rotation = 90 + 90 * animation.getProgress();
        }
    }
}

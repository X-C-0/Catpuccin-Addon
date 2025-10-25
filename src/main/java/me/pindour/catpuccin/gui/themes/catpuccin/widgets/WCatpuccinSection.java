package me.pindour.catpuccin.gui.themes.catpuccin.widgets;

import me.pindour.catpuccin.gui.animation.Animation;
import me.pindour.catpuccin.gui.animation.Direction;
import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.themes.catpuccin.icons.CatpuccinIcons;
import me.pindour.catpuccin.mixininterface.IWContainer;
import me.pindour.catpuccin.utils.WidgetUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatpuccinSection extends WSection implements CatpuccinWidget {
    private double actualHeight;
    private double forcedHeight = -1;

    private WHeader header;

    private Animation animation;

    public WCatpuccinSection(String title, boolean expanded, WWidget headerWidget) {
        super(title, expanded, headerWidget);
    }

    @Override
    public void init() {
        super.init();

        animation = new Animation(theme().uiAnimationType(), theme().uiAnimationSpeed());
        animation.finishedAt(expanded ? Direction.FORWARDS : Direction.BACKWARDS);
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
        if (expanded || animation.isRunning())
            catpuccinRenderer().roundedRect(
                    x, y + header.height,
                    width, height - header.height,
                    cornerRadius,
                    theme().baseColor().copy().a(theme().backgroundOpacity()),
                    CornerStyle.BOTTOM
            );
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
    protected boolean propagateEvents(WWidget widget) {
        WView parentView = ((IWContainer)this).catpuccin$getParentView();

        if (parentView == null) parentView = WidgetUtils.findParentView(this);

        return (expanded || widget instanceof WHeader) && WidgetUtils.isWidgetInside(widget, parentView);
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        animation.reverse();
    }

    @Override
    protected WHeader createHeader() {
        header = new WCatpuccinHeader(title);
        return header;
    }

    protected class WCatpuccinHeader extends WHeader {

        public WCatpuccinHeader(String title) {
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
            CatpuccinGuiTheme theme = theme();
            double pad = pad();
            double s = theme.textHeight() * 0.75;
            Color bgColor = (mouseOver
                    ? theme().surface1Color()
                    : theme().surface0Color())
                    .copy().a(theme.backgroundOpacity());

            // Background
            catpuccinRenderer().roundedRect(
                    this,
                    cornerRadius,
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
                    CatpuccinIcons.ARROW.texture(),
                    theme.textColor()
            );
        }
    }
}

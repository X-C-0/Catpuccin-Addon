package me.pindour.catpuccin.gui.themes.catpuccin.widgets;

import me.pindour.catpuccin.gui.animation.Animation;
import me.pindour.catpuccin.gui.animation.Direction;
import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.widgets.pressable.WOpenIndicator;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatpuccinWindow extends WWindow implements CatpuccinWidget {
    private final int shadowOffset = 2;

    private Animation animation;
    private Color shadowColor;

    public WCatpuccinWindow(WWidget icon, String title) {
        super(icon, title);
    }

    @Override
    public void init() {
        super.init();

        shadowColor = theme().baseColor().copy().a(80);

        animation = new Animation(theme().uiAnimationType(), theme().uiAnimationSpeed());
        animation.finishedAt(expanded ? Direction.FORWARDS : Direction.BACKWARDS);

        if (header instanceof WChlamydieHeader chlamydieHeader)
            chlamydieHeader.setIndicator(expanded);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        // Shadow rectangle
        catpuccinRenderer().roundedRect(
                x - shadowOffset,
                y - shadowOffset,
                width + shadowOffset * 2,
                (expanded || animation.isRunning() ? height : header.height) + shadowOffset * 2,
                cornerRadius,
                shadowColor,
                CornerStyle.ALL
        );

        // Inner rectangle
        if (expanded || animation.isRunning())
            catpuccinRenderer().roundedRect(
                    x,
                    y + header.height,
                    width,
                    height - header.height,
                    cornerRadius - shadowOffset,
                    theme().baseColor(),
                    CornerStyle.BOTTOM
            );
    }

    @Override
    public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (!visible) return true;

        double progress = animation.getProgress();
        boolean useScissor = animation.isRunning();

        if (useScissor)
            renderer.scissorStart(
                    x - shadowOffset,
                    y - shadowOffset,
                    width + shadowOffset * 2,
                    (height - header.height) * progress + header.height + shadowOffset * 2
            );

        boolean toReturn = super.render(renderer, mouseX, mouseY, delta);

        if (useScissor) renderer.scissorEnd();

        return toReturn;
    }

    @Override
    protected void renderWidget(WWidget widget, GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (expanded || animation.isRunning() || widget instanceof WHeader)
            widget.render(renderer, mouseX, mouseY, delta);
    }

    @Override
    protected boolean propagateEvents(WWidget widget) {
        return widget instanceof WHeader || expanded;
    }

    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);

        if (header != null && header instanceof WChlamydieHeader chlamydieHeader)
            chlamydieHeader.setIndicator(expanded);

        if (animation != null)
            animation.reverse();
    }

    @Override
    protected WHeader header(WWidget icon) {
        return new WChlamydieHeader(icon);
    }

    private class WChlamydieHeader extends WHeader {
        private WHorizontalList list;
        private WOpenIndicator indicator;

        private Color semiTransparentColor;
        private Color transparentColor;

        public WChlamydieHeader(WWidget icon) {
            super(icon);
        }

        @Override
        public void init() {
            if (icon != null) {
                createList();
                add(icon).centerY().pad(4);
            }

            if (beforeHeaderInit != null) {
                createList();
                beforeHeaderInit.accept(this);
            }

            boolean hasIcon = beforeHeaderInit != null || icon != null;
            add(theme.label(title, true)).expandCellX().centerY().pad(hasIcon ? 0 : 12);

            indicator = add(theme().openIndicator(expanded)).pad(4).right().centerY().widget();
            indicator.action = () -> setExpanded(!expanded);

            transparentColor = theme().crustColor().copy().a(0);
            semiTransparentColor = theme().crustColor().copy().a(120);
        }

        private void createList() {
            list = add(theme.horizontalList()).expandX().widget();
            list.spacing = 0;
        }

        @Override
        public <T extends WWidget> Cell<T> add(T widget) {
            if (list != null) return list.add(widget);
            return super.add(widget);
        }

        @Override
        protected void onCalculateSize() {
            super.onCalculateSize();
            minWidth = 200;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            catpuccinRenderer().roundedRect(
                    this,
                    cornerRadius,
                    theme().crustColor(),
                    !expanded && animation.isFinished() ? CornerStyle.ALL : CornerStyle.TOP
            );

            // Shadow under the header
            if (expanded || animation.isRunning())
                renderer.quad(
                        x,
                        y + height,
                        width,
                        12,
                        semiTransparentColor,
                        semiTransparentColor,
                        transparentColor,
                        transparentColor
                );
        }

        @Override
        public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            boolean render = super.render(renderer, mouseX, mouseY, delta);
            animProgress = 1; // Small hack to cancel out WWindow scissors, so we can use our animation
            return render;
        }

        public void setIndicator(boolean open) {
            indicator.open = open;
        }
    }
}

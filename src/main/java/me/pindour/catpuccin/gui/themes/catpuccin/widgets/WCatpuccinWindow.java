package me.pindour.catpuccin.gui.themes.catpuccin.widgets;

import me.pindour.catpuccin.gui.animation.Animation;
import me.pindour.catpuccin.gui.animation.Direction;
import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.screens.CatpuccinModulesScreen;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.widgets.pressable.WOpenIndicator;
import me.pindour.catpuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.utils.WindowConfig;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.Click;

public class WCatpuccinWindow extends WWindow implements CatpuccinWidget {
    private final int shadowOffset = 2;

    private CatpuccinModulesScreen modulesScreen;
    private boolean shouldSnap = false;
    private int gridSize;

    private double mouseOffsetX;
    private double mouseOffsetY;

    private Animation animation;

    public WCatpuccinWindow(WWidget icon, String title) {
        super(icon, title);
    }

    @Override
    public void init() {
        super.init();

        animation = new Animation(theme().uiAnimationType(), theme().uiAnimationSpeed());
        animation.finishedAt(expanded ? Direction.FORWARDS : Direction.BACKWARDS);

        if (header instanceof WCatpuccinHeader catpuccinHeader)
            catpuccinHeader.setIndicator(expanded);
    }

    public void initSnapping(CatpuccinModulesScreen modulesScreen, int gridSize) {
        this.modulesScreen = modulesScreen;
        this.gridSize = gridSize;
        shouldSnap = true;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatpuccinGuiTheme theme = theme();
        Color shadowColor = ColorUtils.withAlpha(theme.baseColor(), 0.3);
        Color backgroundColor = ColorUtils.withAlpha(theme.mantleColor(), theme.windowOpacity());

        // Shadow rectangle
        renderer().roundedRect(
                x - shadowOffset,
                y - shadowOffset,
                width + shadowOffset * 2,
                (expanded || animation.isRunning() ? height : header.height) + shadowOffset * 2,
                radius() + shadowOffset,
                shadowColor,
                CornerStyle.ALL
        );

        // Inner rectangle
        if (expanded || animation.isRunning())
            renderer().roundedRect(
                    x,
                    y + header.height,
                    width,
                    height - header.height,
                    radius() - shadowOffset,
                    backgroundColor,
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

        if (header != null && header instanceof WCatpuccinHeader catpuccinHeader)
            catpuccinHeader.setIndicator(expanded);

        if (animation != null)
            animation.reverse();
    }

    @Override
    protected WHeader header(WWidget icon) {
        return new WCatpuccinHeader(icon);
    }

    private class WCatpuccinHeader extends WHeader {
        private WHorizontalList list;
        private WOpenIndicator indicator;

        public WCatpuccinHeader(WWidget icon) {
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
            CatpuccinGuiTheme theme = theme();

            renderer().roundedRect(
                    this,
                    radius(),
                    theme.crustColor(),
                    !expanded && animation.isFinished() ? CornerStyle.ALL : CornerStyle.TOP
            );

            // Shadow under the header
            if (expanded || animation.isRunning()) {
                Color transparentColor = ColorUtils.withAlpha(theme.baseColor(), 0);
                Color semiTransparentColor = ColorUtils.withAlpha(theme.baseColor(), 0.5);

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
        }

        @Override
        public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            boolean render = super.render(renderer, mouseX, mouseY, delta);
            animProgress = 1; // Small hack to cancel out WWindow scissors, so we can use our animation
            return render;
        }

        @Override
        public boolean onMouseClicked(Click click, boolean used) {
            boolean clicked = super.onMouseClicked(click, used);

            if (clicked && shouldSnap) {
                mouseOffsetX = click.x() - x;
                mouseOffsetY = click.y() - y;
            }

            return clicked;
        }

        @Override
        public boolean mouseReleased(Click click) {
            if (shouldSnap) modulesScreen.showGrid(false);
            return super.mouseReleased(click);
        }

        @Override
        public void onMouseMoved(double mouseX, double mouseY, double lastMouseX, double lastMouseY) {
            if (!dragging) return;

            double deltaX = shouldSnap ? snapToGrid(mouseX - mouseOffsetX) - x : mouseX - lastMouseX;
            double deltaY = shouldSnap ? snapToGrid(mouseY - mouseOffsetY) - y : mouseY - lastMouseY;

            WCatpuccinWindow.this.move(deltaX, deltaY);

            moved = true;
            movedX = x;
            movedY = y;

            if (id != null) {
                WindowConfig config = theme.getWindowConfig(id);

                config.x = x;
                config.y = y;
            }

            if (shouldSnap && !modulesScreen.showGrid()) modulesScreen.showGrid(true);
            dragged = true;
        }

        public void setIndicator(boolean open) {
            indicator.open = open;
        }
    }

    private double snapToGrid(double value) {
        return (Math.round(value / gridSize) * gridSize);
    }
}
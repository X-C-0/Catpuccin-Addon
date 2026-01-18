package me.pindour.catppuccin.gui.themes.catppuccin.widgets.input;

import me.pindour.catppuccin.api.render.Corners;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.widgets.input.WMultiSelect;
import me.pindour.catppuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.util.List;

public class WCatppuccinMultiSelect<T> extends WMultiSelect<T> implements CatppuccinWidget {
    public WCatppuccinMultiSelect(String title, List<T> items) {
        super(title, items);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (expanded || animation.isRunning())
            roundedRect().pos(x, y + header.height)
                         .size(width, height - header.height)
                         .radius(radius(), Corners.BOTTOM)
                         .color(ColorUtils.withAlpha(theme().baseColor(), theme().backgroundOpacity()))
                         .render();
    }

    @Override
    protected WHeader createHeader() {
        return new WCatppuccinHeader(title);
    }

    @Override
    protected WItem createItem(T item) {
        return new WCatppuccinItem(item);
    }

    protected class WCatppuccinHeader extends WHeader {

        public WCatppuccinHeader(String title) {
            super(title);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            CatppuccinGuiTheme theme = theme();
            Color bgColor = ColorUtils.withAlpha(
                    mouseOver ? theme.surface1Color() : theme.surface0Color(),
                    theme.backgroundOpacity()
            );

            // Background
            roundedRect().bounds(this)
                         .radius(radius(), expanded || animation.isRunning() ? Corners.TOP : Corners.ALL)
                         .color(bgColor)
                         .render();
        }
    }

    protected class WCatppuccinItem extends WItem {

        public WCatppuccinItem(T item) {
            super(item);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            if (!mouseOver || checkbox.mouseOver) return;

            roundedRect().bounds(this)
                         .radius(smallRadius())
                         .color(theme().surface0Color())
                         .render();
        }
    }
}

package me.pindour.catppuccin.gui.themes.catppuccin.widgets.input;

import me.pindour.catppuccin.renderer.CornerStyle;
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
            renderer().roundedRect(
                    x, y + header.height,
                    width, height - header.height,
                    radius(),
                    ColorUtils.withAlpha(theme().baseColor(), theme().backgroundOpacity()),
                    CornerStyle.BOTTOM
            );
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
            renderer().roundedRect(
                    this,
                    radius(),
                    bgColor,
                    expanded || animation.isRunning() ? CornerStyle.TOP : CornerStyle.ALL
            );
        }
    }

    protected class WCatppuccinItem extends WItem {

        public WCatppuccinItem(T item) {
            super(item);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            if (!mouseOver || checkbox.mouseOver) return;

            renderer().roundedRect(
                    this,
                    smallRadius(),
                    theme().surface0Color(),
                    CornerStyle.ALL
            );
        }
    }
}

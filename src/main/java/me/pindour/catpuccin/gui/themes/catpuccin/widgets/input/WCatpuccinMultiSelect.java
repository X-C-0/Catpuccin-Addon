package me.pindour.catpuccin.gui.themes.catpuccin.widgets.input;

import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.widgets.input.WMultiSelect;
import me.pindour.catpuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.util.List;

public class WCatpuccinMultiSelect<T> extends WMultiSelect<T> implements CatpuccinWidget {
    public WCatpuccinMultiSelect(String title, List<T> items) {
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
        return new WCatpuccinHeader(title);
    }

    @Override
    protected WItem createItem(T item) {
        return new WCatpuccinItem(item);
    }

    protected class WCatpuccinHeader extends WHeader {

        public WCatpuccinHeader(String title) {
            super(title);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            CatpuccinGuiTheme theme = theme();
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

    protected class WCatpuccinItem extends WItem {

        public WCatpuccinItem(T item) {
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

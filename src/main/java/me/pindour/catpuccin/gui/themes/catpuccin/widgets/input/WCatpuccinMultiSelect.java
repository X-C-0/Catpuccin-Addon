package me.pindour.catpuccin.gui.themes.catpuccin.widgets.input;

import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.widgets.input.WMultiSelect;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.util.List;

public class WCatpuccinMultiSelect<T> extends WMultiSelect<T> implements CatpuccinWidget {
    public WCatpuccinMultiSelect(String title, List<ItemInfo<T>> items, WTextBox searchBox) {
        super(title, items, searchBox);
    }

    @Override
    protected WHeader createHeader() {
        return new WCatpuccinHeader(title);
    }

    @Override
    protected WItem createItem(ItemInfo<T> itemInfo) {
        return new WCatpuccinItem(itemInfo);
    }

    protected class WCatpuccinHeader extends WHeader {

        public WCatpuccinHeader(String title) {
            super(title);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            CatpuccinGuiTheme theme = theme();

            // Background
            catpuccinRenderer().roundedRect(
                    this,
                    smallCornerRadius,
                    theme.backgroundColor.get(mouseOver).copy().a(theme.backgroundOpacity()),
                    expanded || animation.isRunning() ? CornerStyle.TOP : CornerStyle.ALL
            );

            // Shadow under the header
            if (expanded || animation.getProgress() > 0) {
                Color semiTransparentColor = theme.mantleColor().copy().a(160);
                Color transparentColor = theme.mantleColor().copy().a(0);

                renderer.quad(
                        x,
                        y + height,
                        width,
                        8 * animation.getProgress(),
                        semiTransparentColor,
                        semiTransparentColor,
                        transparentColor,
                        transparentColor
                );
            }
        }
    }

    protected class WCatpuccinItem extends WItem {

        public WCatpuccinItem(ItemInfo<T> itemInfo) {
            super(itemInfo);
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            if (!mouseOver || checkbox.mouseOver) return;

            catpuccinRenderer().roundedRect(
                    this,
                    smallCornerRadius,
                    theme().surface0Color(),
                    CornerStyle.ALL
            );
        }
    }
}

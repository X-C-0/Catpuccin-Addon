package me.pindour.catppuccin.gui.themes.catppuccin.widgets.container;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.utils.Utils;

public class WCatppuccinView extends WView implements CatppuccinWidget {

    @Override
    public void init() {
        maxHeight = Utils.getWindowHeight() - theme.scale(200);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (canScroll && hasScrollBar) {
            roundedRect().pos(handleX(), handleY())
                         .size(handleWidth(), handleHeight())
                         .radius(smallRadius())
                         .color(theme().scrollbarColor.get(handlePressed, handleMouseOver))
                         .render();
        }
    }
}

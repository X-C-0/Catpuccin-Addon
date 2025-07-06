package me.pindour.catpuccin.gui.themes.catpuccin.widgets;

import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.utils.Utils;

public class WCatpuccinView extends WView implements CatpuccinWidget {

    @Override
    public void init() {
        maxHeight = Utils.getWindowHeight() - theme.scale(150);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (canScroll && hasScrollBar) {
            renderer.quad(
                    handleX(),
                    handleY(),
                    handleWidth(),
                    handleHeight(),
                    theme().scrollbarColor.get(handlePressed, handleMouseOver)
            );
        }
    }
}

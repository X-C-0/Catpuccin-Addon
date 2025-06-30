package me.pindour.catpuccin.gui.themes.catpuccin.widgets.pressable;

import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.themes.catpuccin.icons.CatpuccinIcons;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;

public class WCatpuccinTriangle extends WTriangle implements CatpuccinWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.rotatedQuad(
                x,
                y,
                width,
                height,
                rotation,
                CatpuccinIcons.ARROW.texture(),
                theme().backgroundColor.get(pressed, mouseOver)
        );
    }
}

package me.pindour.catpuccin.gui.themes.catpuccin.widgets.pressable;

#if MC_VER >= MC_1_21_8
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WConfirmedMinus;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatpuccinConfirmedMinus extends WConfirmedMinus implements CatpuccinWidget {

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatpuccinGuiTheme theme = theme();
        double pad = pad();
        double s = theme.scale(3);

        Color outline = theme.outlineColor.get(pressed, mouseOver);
        Color fg = pressedOnce ? theme.backgroundColor.get(pressed, mouseOver) : theme.redColor();
        Color bg = pressedOnce ? theme.redColor() : theme.backgroundColor.get(pressed, mouseOver);

        renderBackground(this, outline, bg);
        renderer.quad(x + pad, y + height / 2 - s / 2, width - pad * 2, s, fg);
    }
}
#endif

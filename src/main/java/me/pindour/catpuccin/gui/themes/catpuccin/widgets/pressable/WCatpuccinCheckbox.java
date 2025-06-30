package me.pindour.catpuccin.gui.themes.catpuccin.widgets.pressable;

import me.pindour.catpuccin.gui.animation.Animation;
import me.pindour.catpuccin.gui.animation.Direction;
import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;

public class WCatpuccinCheckbox extends WCheckbox implements CatpuccinWidget {
    private Animation animation;

    public WCatpuccinCheckbox(boolean checked) {
        super(checked);
    }

    @Override
    public void init() {
        super.init();

        animation = new Animation(theme().uiAnimationType(), theme().uiAnimationSpeed());
        animation.setInitialState(checked ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    protected void onPressed(int button) {
        super.onPressed(button);
        animation.reverse();
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatpuccinGuiTheme theme = theme();
        double s = theme.scale(3);

        if (!checked || animation.isRunning()) renderBackground(this, pressed, mouseOver);

        double size = width * animation.getProgress();

        if ((checked || animation.isRunning()) && size > s * 2) {
            double offsetX = width / 2 - size / 2;
            double offsetY = height / 2 - size / 2;

            // Outline rectangle
            catpuccinRenderer().roundedRect(
                    x + offsetX,
                    y + offsetY,
                    size,
                    size,
                    smallCornerRadius,
                    theme.accentColor().copy().a(80),
                    CornerStyle.ALL
            );

            // Inner rectangle
            catpuccinRenderer().roundedRect(
                    x + s + offsetX,
                    y + s + offsetY,
                    size - s * 2,
                    size - s * 2,
                    smallCornerRadius - s,
                    theme.accentColor(),
                    CornerStyle.ALL
            );
        }
    }
}

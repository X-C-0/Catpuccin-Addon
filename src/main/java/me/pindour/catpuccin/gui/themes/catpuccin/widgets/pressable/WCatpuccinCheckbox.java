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
        animation.finishedAt(checked ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    protected void onCalculateSize() {
        super.onCalculateSize();

        height *= 0.9;
        width *= 0.9;
    }

    @Override
    protected void onPressed(int button) {
        super.onPressed(button);
        startAnimation();
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        // Render background only when it could be visible (when not checked or when animation is still running)
        if (!checked || animation.isRunning()) renderBackground(this, pressed, mouseOver);

        // Early exit if there is nothing to render
        if (!checked && animation.isFinished()) return;

        CatpuccinGuiTheme theme = theme();
        double size = width * animation.getProgress();
        double outlineSize = theme.scale(3);
        double minSize = theme.scale(6);

        if (size <= minSize) return;

        double offsetX = width / 2 - size / 2;
        double offsetY = height / 2 - size / 2;
        double innerSize = size - outlineSize * 2;
        double innerOffset = outlineSize + offsetY;

        // Outline rectangle
        catpuccinRenderer().roundedRect(
                x + offsetX,
                y + offsetY,
                size,
                size,
                smallCornerRadius,
                theme.accentColor().copy().a(mouseOver ? 140 : 80),
                CornerStyle.ALL
        );

        // Inner rectangle
        catpuccinRenderer().roundedRect(
                x + innerOffset,
                y + innerOffset,
                innerSize,
                innerSize,
                smallCornerRadius - outlineSize / 2,
                theme.accentColor(),
                CornerStyle.ALL
        );
    }

    private void startAnimation() {
        animation.start(checked ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    public void setChecked(boolean checked) {
        if (this.checked == checked) return;

        this.checked = checked;
        startAnimation();
    }
}

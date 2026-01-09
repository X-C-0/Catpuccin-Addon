package me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable;

import me.pindour.catppuccin.gui.animation.Animation;
import me.pindour.catppuccin.gui.animation.Direction;
import me.pindour.catppuccin.renderer.rounded.CornerStyle;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;

public class WCatppuccinCheckbox extends WCheckbox implements CatppuccinWidget {
    private Animation animation;

    public WCatppuccinCheckbox(boolean checked) {
        super(checked);
    }

    @Override
    public void init() {
        super.init();

        animation = new Animation(
                theme().guiAnimationEasing(),
                theme().guiAnimationDuration(),
                checked ? Direction.FORWARDS : Direction.BACKWARDS
        );
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
        animation.start(checked ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        // Background is only visible when unchecked or animating
        if (!checked || animation.isRunning()) renderBackground(this, pressed, mouseOver);

        // Skip checkmark if unchecked and animation finished
        if (!checked && animation.isFinished()) return;

        renderCheckmark();
    }

    private void renderCheckmark() {
        CatppuccinGuiTheme theme = theme();
        double progress = animation.getProgress();
        double size = width * progress;
        double minSize = theme.scale(6);

        if (size <= minSize) return;

        double outlineSize = theme.scale(3);
        double centerOffset = (width - size) / 2;

        // Outline
        renderer().roundedRect(
                x + centerOffset,
                y + centerOffset,
                size,
                size,
                smallRadius(),
                theme.accentColor().copy().a(mouseOver ? 140 : 80),
                CornerStyle.ALL
        );

        // Inner fill
        double innerSize = size - outlineSize * 2;
        double innerOffset = centerOffset + outlineSize;

        renderer().roundedRect(
                x + innerOffset,
                y + innerOffset,
                innerSize,
                innerSize,
                smallRadius() - outlineSize / 2,
                theme.accentColor(),
                CornerStyle.ALL
        );
    }

    public void setChecked(boolean checked) {
        if (this.checked == checked) return;
        this.checked = checked;
        animation.start(checked ? Direction.FORWARDS : Direction.BACKWARDS);
    }
}
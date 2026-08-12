package me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable;

import me.pindour.catppuccin.api.animation.Animation;
import me.pindour.catppuccin.api.animation.Direction;
import me.pindour.catppuccin.api.animation.Easing;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.themes.catppuccin.icons.CatppuccinBuiltinIcons;
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
                Easing.BACK_IN_OUT,
                300,
                checked ? Direction.FORWARDS : Direction.BACKWARDS
        );
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        // Background is only visible when unchecked or animating
        if (!checked || animation.isRunning()) background(false, mouseOver).render();

        // Skip checkmark if unchecked and animation finished
        if (!checked && animation.isFinished()) return;

        renderCheckmark(renderer);
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

    private void renderCheckmark(GuiRenderer renderer) {
        CatppuccinGuiTheme theme = theme();
        double progress = animation.getProgress();
        double size = width * progress;
        double tickSize = size * 0.6;
        double minSize = theme.scale(6);

        if (size <= minSize) return;

        double centerOffset = (width - size) / 2;

        roundedRect().pos(x + centerOffset, y + centerOffset)
                     .size(size, size)
                     .radius(smallRadius())
                     .color(theme.accentColor())
                     .outline(theme.accentColor().copy().a(mouseOver ? 140 : 80), 3f)
                     .render();

        if (tickSize <= minSize) return;

        centerOffset = (width - tickSize) / 2;

        renderer.rotatedQuad(
                x + centerOffset,
                y + centerOffset,
                tickSize,
                tickSize,
                0,
                CatppuccinBuiltinIcons.TICK.texture(),
                theme.backgroundColor.get(160)
        );
    }

    public void setChecked(boolean checked) {
        if (this.checked == checked) return;
        this.checked = checked;
        animation.start(checked ? Direction.FORWARDS : Direction.BACKWARDS);
    }
}
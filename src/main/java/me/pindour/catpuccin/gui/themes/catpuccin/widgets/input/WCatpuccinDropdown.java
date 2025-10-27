package me.pindour.catpuccin.gui.themes.catpuccin.widgets.input;

import me.pindour.catpuccin.gui.animation.Animation;
import me.pindour.catpuccin.gui.animation.Direction;
import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.themes.catpuccin.icons.CatpuccinIcons;
import me.pindour.catpuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatpuccinDropdown<T> extends WDropdown<T> implements CatpuccinWidget {
    private final RichText titleText;
    private RichText valueText;

    private Animation animation;

    public WCatpuccinDropdown(String title, T[] values, T value) {
        super(values, value);
        titleText = RichText.bold(title);
        valueText = RichText.of(getNameFor(value));
    }

    @Override
    public void init() {
        double pad = 6;

        root = createRootWidget();
        root.theme = theme;
        root.spacing = pad;
        maxValueWidth = 0;

        for (int i = 0; i < values.length; i++) {
            WValue widget = new WValue(values[i]);
            widget.theme = theme;

            double valueWidth = theme().textWidth(widget.valueName);
            maxValueWidth = Math.max(maxValueWidth, valueWidth);

            Cell<?> cell = root.add(widget).padHorizontal(pad).expandWidgetX();
            if (i == 0) cell.padTop(pad);
            if (i == values.length - 1) cell.padBottom(pad);
        }

        animation = new Animation(theme().uiAnimationType(), theme().uiAnimationSpeed());
        animation.finishedAt(Direction.BACKWARDS);
    }

    @Override
    protected WDropdownRoot createRootWidget() {
        return new WRoot();
    }

    @Override
    protected WDropdownValue createValueWidget() {
        return null;
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();

        root.calculateSize();

        double titleWidth = pad + theme().textWidth(titleText) + pad;
        double valueWidth = pad + maxValueWidth + pad;
        double arrowWidth = pad + theme.textHeight() + pad;

        width = titleWidth + valueWidth + arrowWidth;
        height = pad + theme.textHeight() + pad;

        root.width = width;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatpuccinGuiTheme theme = theme();
        double pad = pad();
        double s = theme.textHeight() * 0.75;

        renderBackground(this, pressed, mouseOver);

        // Title text
        catpuccinRenderer().text(
                titleText,
                x + pad,
                y + pad,
                theme.textColor()
        );

        // Value text
        catpuccinRenderer().text(
                valueText,
                x + pad + theme.textWidth(titleText) + pad,
                y + pad,
                theme.accentColor()
        );

        // Open indicator
        renderer.rotatedQuad(
                x + width - pad - s,
                y + height / 2 - s / 2,
                s,
                s,
                180 * (1 - animation.getProgress()),
                CatpuccinIcons.ARROW.texture(),
                theme.textColor()
        );
    }

    @Override
    public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        animProgress = -8008135; // Small hack to cancel out WDropdown scissors, so we can use our animation

        boolean render = super.render(renderer, mouseX, mouseY, delta);
        double progress = animation.getProgress();

        if (!render && progress > 0) {
            renderer.absolutePost(() -> {
                renderer.scissorStart(root.x, root.y, root.width, root.height * progress);
                root.render(renderer, mouseX, mouseY, delta);
                renderer.scissorEnd();
            });
        }

        return render;
    }

    @Override
    protected void onPressed(int button) {
        super.onPressed(button);
        handlePressed();
    }

    @Override
    public void set(T value) {
        super.set(value);

        if (animation == null) return;

        animation.reset();
        handlePressed();
    }

    private void handlePressed() {
        valueText = RichText.of(getNameFor(value));
        animation.reverse();
    }

    private String getNameFor(T value) {
        String name = value.toString();

        // ENUM_NAME -> Enum Name
        if (name.contains("_")) return Utils.nameToTitle(name.toLowerCase().replace("_", "-"));

        // EnumName -> Enum Name
        return Utils.nameToTitle(name.replaceAll("(?<=[a-z])([A-Z])", "-$1").toLowerCase());
    }

    private static class WRoot extends WDropdownRoot implements CatpuccinWidget {
        private static final int DROPDOWN_Y_OFFSET = 6;

        @Override
        protected void onCalculateWidgetPositions() {
            this.y += DROPDOWN_Y_OFFSET;
            super.onCalculateWidgetPositions();
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderBackground(this, theme().accentColor(), theme().baseColor(), true);
        }
    }

    private class WValue extends WDropdownValue implements CatpuccinWidget {
        private final RichText valueName;

        public WValue(T value) {
            this.value = value;
            this.valueName = RichText.of(getNameFor(value));
        }

        @Override
        protected void onCalculateSize() {
            double pad = pad();

            width = pad + theme().textWidth(valueName) + pad;
            height = pad + theme.textHeight() + pad;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            CatpuccinGuiTheme theme = theme();

            if (mouseOver)
                catpuccinRenderer().roundedRect(
                        x, y,
                        width, height,
                        smallCornerRadius,
                        ColorUtils.withAlpha(theme.accentColor(), 0.4),
                        CornerStyle.ALL
                );

            boolean isSelected = get() == this.value;
            RichText text = valueName.boldIf(isSelected);
            Color textColor = isSelected ? theme.accentColor() : theme.textColor();

            catpuccinRenderer().text(
                    text,
                    x + width / 2 - theme.textWidth(text) / 2,
                    y + pad(),
                    textColor
            );
        }

        @Override
        protected void onPressed(int button) {
            super.onPressed(button);
            handlePressed();
        }
    }
}

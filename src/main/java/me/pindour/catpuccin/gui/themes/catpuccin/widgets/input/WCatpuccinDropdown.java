package me.pindour.catpuccin.gui.themes.catpuccin.widgets.input;

import me.pindour.catpuccin.gui.animation.Animation;
import me.pindour.catpuccin.gui.animation.Direction;
import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.themes.catpuccin.icons.CatpuccinIcons;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WCatpuccinDropdown<T> extends WDropdown<T> implements CatpuccinWidget {
    private final String title;
    private RichText richText;

    private Animation animation;

    public WCatpuccinDropdown(String title, T[] values, T value) {
        super(values, value);
        this.title = title;
        richText = getTitleFor(getNameFor(value));
    }

    @Override
    public void init() {
        super.init();

        animation = new Animation(theme().uiAnimationType(), theme().uiAnimationSpeed());
        animation.setInitialState(Direction.BACKWARDS);
    }

    @Override
    protected WDropdownRoot createRootWidget() {
        return new WRoot();
    }

    @Override
    protected WDropdownValue createValueWidget() {
        return new WValue();
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();

        maxValueWidth = 0;
        for (T value : values) {
            double valueWidth = theme().textWidth(getTitleFor(getNameFor(value)));
            maxValueWidth = Math.max(maxValueWidth, valueWidth);
        }

        root.calculateSize();

        width = pad + maxValueWidth + pad + theme.textHeight() + pad;
        height = pad + theme.textHeight() + pad;

        root.width = width;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatpuccinGuiTheme theme = theme();
        double pad = pad();
        double s = theme.textHeight() * 0.75;

        boolean drawOutline = theme.widgetOutline.get();
        double outlineWidth = drawOutline ? theme.scale(2) : 0;

        // Outline
        if (drawOutline)
            catpuccinRenderer().roundedRect(
                    x,
                    y,
                    width,
                    height,
                    smallCornerRadius,
                    theme.outlineColor.get(pressed, mouseOver),
                    expanded || animation.isRunning() ? CornerStyle.TOP : CornerStyle.ALL
            );

        // Background
        catpuccinRenderer().roundedRect(
                x + outlineWidth,
                y + outlineWidth,
                width - outlineWidth * 2,
                height - outlineWidth * 2,
                smallCornerRadius - outlineWidth,
                theme.backgroundColor.get(pressed, mouseOver),
                expanded || animation.isRunning() ? CornerStyle.TOP : CornerStyle.ALL
        );

        catpuccinRenderer().text(richText, x + pad * 2, y + pad, theme.textColor());

        // Open indicator
        renderer.rotatedQuad(
                x + width - pad - s,
                y + height / 2 - s / 2,
                s,
                s,
                90 + 90 * animation.getProgress(),
                CatpuccinIcons.ARROW.texture(),
                theme.textColor()
        );
    }

    @Override
    public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        animProgress = -8008135; // Fuck you meteor (boobies)

        boolean render = super.render(renderer, mouseX, mouseY, delta);
        double progress = animation.getProgress();

        if (!render && progress > 0) {
            renderer.absolutePost(() -> {
                renderer.scissorStart(x, y + height, width, root.height * progress);
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

    private void handlePressed() {
        richText = getTitleFor(getNameFor(value));
        animation.reverse();
    }

    private RichText getTitleFor(String name) {
        if (title == null || title.isEmpty()) return RichText.of(name);

        return RichText.bold(title).append(" - ").append(name);
    }

    private String getNameFor(T value) {
        String name = value.toString();

        // ENUM_NAME -> Enum Name
        if (name.contains("_")) return Utils.nameToTitle(name.toLowerCase().replace("_", "-"));

        // EnumName -> Enum Name
        return Utils.nameToTitle(name.replaceAll("(?<=[a-z])([A-Z])", "-$1").toLowerCase());
    }

    private static class WRoot extends WDropdownRoot implements CatpuccinWidget {
        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            CatpuccinGuiTheme theme = theme();
            double s = theme.scale(2);
            Color c = theme.accentColor();

            renderer.quad(x, y + height - s, width, s, c);
            renderer.quad(x, y, s, height - s, c);
            renderer.quad(x + width - s, y, s, height - s, c);
        }
    }

    private class WValue extends WDropdownValue implements CatpuccinWidget {
        private String name;

        @Override
        public void init() {
            super.init();
            name = getNameFor(value);
        }

        @Override
        protected void onCalculateSize() {
            double pad = pad();

            width = pad + theme().textWidth(getTitleFor(name)) + pad;
            height = pad + theme.textHeight() + pad;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            CatpuccinGuiTheme theme = theme();

            Color color = theme.backgroundColor.get(pressed, mouseOver, true).copy();
            int preA = color.a;
            color.a += color.a / 2;
            color.validate();

            renderer.quad(this, color);

            color.a = preA;

            boolean isSelected = get() == this.value;
            RichText text = RichText.of(name).boldIf(isSelected);
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

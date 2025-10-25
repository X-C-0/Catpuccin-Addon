package me.pindour.catpuccin.gui.themes.catpuccin.widgets.input;

import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.WCatpuccinLabel;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.util.math.MathHelper;

import static org.lwjgl.glfw.GLFW.*;

public class WCatpuccinTextBox extends WTextBox implements CatpuccinWidget {
    private boolean cursorVisible;
    private double cursorTimer;

    private double animProgress;
    private boolean renderBackground;
    private boolean dynamicWidth;

    private Color customColor;

    public WCatpuccinTextBox(String text, String placeholder, CharFilter filter, Class<? extends Renderer> renderer) {
        super(text, placeholder, filter, renderer);
        renderBackground = true;
        dynamicWidth = false;
    }

    @Override
    protected void onCalculateSize() {
        super.onCalculateSize();
        if (dynamicWidth) {
            double pad = pad();
            double textWidth = textWidths.isEmpty() ? 0 : textWidths.getDouble(textWidths.size() - 1);

            if (width == textWidth) return;

            width = pad + textWidth + pad;
            parent.invalidate();
        }
    }

    @Override
    public boolean onMouseClicked(Click click, boolean used) {
        boolean clicked =  super.onMouseClicked(click, used);

        // Update widget width when text is cleared
        if (clicked && dynamicWidth && click.button() == GLFW_MOUSE_BUTTON_RIGHT && !text.isEmpty()) onCalculateSize();

        return clicked;
    }

    @Override
    public boolean onKeyRepeated(KeyInput input) {
        boolean repeated = super.onKeyRepeated(input);

        if (repeated && dynamicWidth) {
            boolean control = MacWindowUtil.IS_MAC ? input.modifiers() == GLFW_MOD_SUPER : input.modifiers() == GLFW_MOD_CONTROL;

            // Update widget width when text is updated
            if ((control && input.key() == GLFW_KEY_V)
                    || input.key() == GLFW_KEY_BACKSPACE
                    || input.key() == GLFW_KEY_DELETE)
                onCalculateSize();
        }

        return repeated;
    }

    @Override
    public boolean onCharTyped(CharInput input) {
        boolean typed = super.onCharTyped(input);
        if (dynamicWidth) onCalculateSize();
        return typed;
    }

    @Override
    public void set(String text) {
        super.set(text);
        if (dynamicWidth) onCalculateSize();
    }

    @Override
    protected WContainer createCompletionsRootWidget() {
        return new WVerticalList() {
            @Override
            protected void onRender(GuiRenderer renderer1, double mouseX, double mouseY, double delta) {
                CatpuccinGuiTheme theme = theme();
                double s = theme.scale(2);
                Color c = theme.outlineColor.get();

                Color col = theme.backgroundColor.get();
                int preA = col.a;
                col.a += col.a / 2;
                col.validate();
                renderer1.quad(this, col);
                col.a = preA;

                renderer1.quad(x, y + height - s, width, s, c);
                renderer1.quad(x, y, s, height - s, c);
                renderer1.quad(x + width - s, y, s, height - s, c);
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends WWidget & ICompletionItem> T createCompletionsValueWidth(String completion, boolean selected) {
        return (T) new CompletionItem(completion, false, selected);
    }

    @Override
    protected double getOverflowWidthForRender() {
        return dynamicWidth ? 0 : super.getOverflowWidthForRender();
    }

    private static class CompletionItem extends WCatpuccinLabel implements ICompletionItem {
        private static final Color SELECTED_COLOR = new Color(255, 255, 255, 15);

        private boolean selected;

        public CompletionItem(String text, boolean title, boolean selected) {
            super(RichText.of(text).boldIf(title));
            this.selected = selected;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            super.onRender(renderer, mouseX, mouseY, delta);

            if (selected) renderer.quad(this, SELECTED_COLOR);
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public String getCompletion() {
            return text;
        }
    }

    @Override
    protected void onCursorChanged() {
        cursorVisible = true;
        cursorTimer = 0;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        CatpuccinGuiTheme theme = theme();

        if (cursorTimer >= 1) {
            cursorVisible = !cursorVisible;
            cursorTimer = 0;
        }
        else {
            cursorTimer += delta * 1.75;
        }

        if (renderBackground) {
            int bottomSize = 2;
            Color highlightColor = focused ? theme.accentColor() :
                    mouseOver ? theme.surface2Color() : theme.surface1Color();

            // Background
            catpuccinRenderer().roundedRect(
                    x, y,
                    width, height,
                    smallCornerRadius,
                    theme.surface0Color().copy().a(theme.backgroundOpacity()),
                    CornerStyle.ALL
            );

            renderer.scissorStart(x, y + height - bottomSize, width, bottomSize);

            // Bottom highlight
            catpuccinRenderer().roundedRect(
                    x, y,
                    width, height,
                    smallCornerRadius,
                    highlightColor,
                    CornerStyle.BOTTOM
            );

            renderer.scissorEnd();
        }

        double pad = pad();
        double overflowWidth = getOverflowWidthForRender();

        renderer.scissorStart(x + pad, y + pad, width - pad * 2, height - pad * 2);

        // Text content
        if (!text.isEmpty()) {
            this.renderer.render(renderer, x + pad - overflowWidth, y + pad, text, customColor != null ? customColor : theme.textColor());
        }
        else if (placeholder != null) {
            this.renderer.render(renderer, x + pad - overflowWidth, y + pad, placeholder, theme.textSecondaryColor());
        }

        // Text highlighting
        if (focused && (cursor != selectionStart || cursor != selectionEnd)) {
            double selStart = x + pad + getTextWidth(selectionStart) - overflowWidth;
            double selEnd = x + pad + getTextWidth(selectionEnd) - overflowWidth;

            renderer.quad(selStart, y + pad, selEnd - selStart, theme.textHeight(), theme.textHighlightColor().copy().a(120));
        }

        // Cursor
        animProgress += delta * 10 * (focused && cursorVisible ? 1 : -1);
        animProgress = MathHelper.clamp(animProgress, 0, 1);

        if ((focused && cursorVisible) || animProgress > 0) {
            renderer.setAlpha(animProgress);
            renderer.quad(x + pad + getTextWidth(cursor) - overflowWidth, y + pad, theme.scale(1), theme.textHeight(), theme.textColor());
            renderer.setAlpha(1);
        }

        renderer.scissorEnd();
    }

    public int getCursor() {
        return cursor;
    }

    public void setRenderBackground(boolean renderBackground) {
        this.renderBackground = renderBackground;
    }

    public void setDynamicWidth(boolean dynamicWidth) {
        this.dynamicWidth = dynamicWidth;
    }

    public void color(Color color) {
        this.customColor = color;
    }
}

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
import net.minecraft.util.math.MathHelper;

#if MC_VER >= MC_1_21_10
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.MacWindowUtil;
#else
import static net.minecraft.client.MinecraftClient.IS_SYSTEM_MAC;
#endif

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
#if MC_VER >= MC_1_21_10
    public boolean onMouseClicked(Click click, boolean used) {
        boolean clicked =  super.onMouseClicked(click, used);

        handleDynamicWidthClick(clicked, click.button() == GLFW_MOUSE_BUTTON_LEFT);

        return clicked;
    }

    @Override
    public boolean onKeyRepeated(KeyInput input) {
        boolean repeated = super.onKeyRepeated(input);

        boolean control = MacWindowUtil.IS_MAC ? input.modifiers() == GLFW_MOD_SUPER : input.modifiers() == GLFW_MOD_CONTROL;
        handleDynamicWidthKey(repeated, control, input.key());

        return repeated;
    }

    @Override
    public boolean onCharTyped(CharInput input) {
        boolean typed = super.onCharTyped(input);
        if (dynamicWidth) onCalculateSize();
        return typed;
    }
#else
    public boolean onMouseClicked(double mouseX, double mouseY, int button, boolean used) {
        boolean clicked = super.onMouseClicked(mouseX, mouseY, button, used);

        handleDynamicWidthClick(clicked, button == GLFW_MOUSE_BUTTON_LEFT);

        return clicked;
    }

    @Override
    public boolean onKeyRepeated(int key, int mods) {
        boolean repeated = super.onKeyRepeated(key, mods);

        boolean control = IS_SYSTEM_MAC ? mods == GLFW_MOD_SUPER : mods == GLFW_MOD_CONTROL;
        handleDynamicWidthKey(repeated, control, key);

        return repeated;
    }

    @Override
    public boolean onCharTyped(char input) {
        boolean typed = super.onCharTyped(input);
        if (dynamicWidth) onCalculateSize();
        return typed;
    }
#endif

    @Override
    public void set(String text) {
        super.set(text);
        if (dynamicWidth) onCalculateSize();
    }

    private void handleDynamicWidthClick(boolean clicked, boolean leftButton) {
        if (clicked && dynamicWidth && !text.isEmpty() && leftButton) {
            onCalculateSize();
        }
    }

    private void handleDynamicWidthKey(boolean repeated, boolean control, int key) {
        if (!repeated || !dynamicWidth) return;
        if ((control && key == GLFW_KEY_V)
            || key == GLFW_KEY_BACKSPACE
            || key == GLFW_KEY_DELETE) {
            onCalculateSize();
        }
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
                    mouseOver ? theme.surface2Color() : theme.surface0Color();

            // Background
            catpuccinRenderer().roundedRect(x, y, width, height, smallCornerRadius, theme.mantleColor().copy().a(theme.backgroundOpacity()), CornerStyle.ALL);

            renderer.scissorStart(x, y + height - bottomSize, width, bottomSize);

            // Bottom highlight
            catpuccinRenderer().roundedRect(x, y, width, height, smallCornerRadius, highlightColor, CornerStyle.BOTTOM);

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

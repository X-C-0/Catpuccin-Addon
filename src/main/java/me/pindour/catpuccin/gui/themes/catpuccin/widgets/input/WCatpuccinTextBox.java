package me.pindour.catpuccin.gui.themes.catpuccin.widgets.input;

import me.pindour.catpuccin.gui.renderer.CornerStyle;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.WCatpuccinLabel;
import me.pindour.catpuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.MathHelper;

public class WCatpuccinTextBox extends WTextBox implements CatpuccinWidget {
    private final String title;
    private Color customColor;

    private boolean cursorVisible;
    private double cursorTimer;

    private double animProgress;
    private boolean renderBackground;

    public WCatpuccinTextBox(String text, String placeholder, String title, CharFilter filter, Class<? extends Renderer> renderer) {
        super(text, placeholder, filter, renderer);
        this.title = title;
        this.renderBackground = true;
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
        int HORIZONTAL_LIST_SPACING = 3;
        double pad = pad();
        double titleWidth = (hasTitle() ? pad + theme.textWidth(title) + HORIZONTAL_LIST_SPACING : 0);

        if (cursorTimer >= 1) {
            cursorVisible = !cursorVisible;
            cursorTimer = 0;
        }
        else {
            cursorTimer += delta * 1.75;
        }

        if (renderBackground) {
            // Title
            if (hasTitle()) {
                renderer().roundedRect(
                        x - titleWidth, y,
                        titleWidth, height,
                        smallRadius(),
                        theme.surface0Color(),
                        CornerStyle.LEFT
                );
            }

            // Outline
            renderBackground(this, theme.surface0Color(), theme.baseColor(), true);
        }

        double overflowWidth = getOverflowWidthForRender();

        renderer.scissorStart(x + pad, y + pad, width - pad * 2, height - pad * 2);

        // Text content
        if (!text.isEmpty()) {
            Color textColor = customColor != null
                    ? customColor
                    : (focused ? theme.textColor() : ColorUtils.darker(theme.textSecondaryColor()));

            this.renderer.render(renderer, x + pad - overflowWidth, y + pad, text, textColor);
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

    @Override
    public CornerStyle cornerStyle() {
        return hasTitle() ? CornerStyle.RIGHT : CornerStyle.ALL;
    }

    private boolean hasTitle() {
        return title != null && !title.isEmpty();
    }

    public int getCursor() {
        return cursor;
    }

    public void shouldRenderBackground(boolean renderBackground) {
        this.renderBackground = renderBackground;
    }

    public void color(Color color) {
        this.customColor = color;
    }
}

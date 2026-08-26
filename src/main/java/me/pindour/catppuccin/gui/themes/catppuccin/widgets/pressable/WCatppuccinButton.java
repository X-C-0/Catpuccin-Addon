package me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable;

import me.pindour.catppuccin.api.animation.Animation;
import me.pindour.catppuccin.api.animation.Easing;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.widgets.IConditionalWidget;
import me.pindour.catppuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.util.function.BooleanSupplier;

public class WCatppuccinButton extends WButton implements IConditionalWidget, CatppuccinWidget {
    protected static final double ICON_GAP = 4;

    protected Animation hoverAnimation;
    protected RichText richText;

    private BooleanSupplier visibilityCondition;

    public WCatppuccinButton(RichText text, GuiTexture texture) {
        super(text.getPlainText(), texture);
        this.richText = text;
    }

    public WCatppuccinButton(GuiTexture texture) {
        super(null, texture);
    }

    @Override
    public void init() {
        hoverAnimation = new Animation(Easing.QUAD_OUT, 250);
    }

    @Override
    protected void onCalculateSize() {
        Content content = content();

        if (content.hasText()) {
            double pad = theme.scale(12);

            textWidth = theme().textWidth(richText);
            width = pad + contentWidth(content) + pad;

        } else {
            width = theme.scale(32);
        }

        height = theme.scale(32);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (!shouldRender(mouseOver)) return;

        double progress = hoverAnimation.getProgress();

        if (mouseOver && progress == 0) {
            hoverAnimation.start();
        }
        else if (!mouseOver && progress > 0) {
            hoverAnimation.reset();
        }

        CatppuccinGuiTheme theme = theme();
        double pad = pad();

        Color bg = theme.backgroundColor.get(pressed, mouseOver);
        Color accent = ColorUtils.withAlpha(theme.accentColor(), 0.8);
        Color outline = ColorUtils.lerp(bg, accent, hoverAnimation.getProgress());

        background(bg, outline).render();

        renderContent(renderer, pad);
    }

    protected void renderContent(GuiRenderer renderer, double pad) {
        Content content = content();
        double size = iconSize();
        double contentX = x + (width - contentWidth(content)) / 2;

        if (content.hasIcon()) {
            renderer.quad(contentX, y + pad, size, size, texture, textColor());
            contentX += size + iconGap();
        }

        if (content.hasText()) {
            renderer().text(richText, contentX, y + pad, textColor());
        }
    }

    protected Content content() {
        return Content.of(richText, texture);
    }

    protected double iconSize() {
        return theme.textHeight();
    }

    protected double iconGap() {
        return theme.scale(ICON_GAP);
    }

    protected double contentWidth(Content content) {
        double width = content.hasIcon() ? iconSize() : 0;

        if (content.hasText()) {
            width += content.hasIcon() ? iconGap() : 0;
            width += textWidth;
        }

        return width;
    }

    protected Color textColor() {
        return theme().textColor();
    }

    public void set(RichText text) {
        if (richText == null || Math.round(theme().textWidth(richText)) != textWidth) invalidate();

        richText = text;
    }

    @Override
    public void set(String text) {
        set(RichText.of(text));
    }

    @Override
    public BooleanSupplier getVisibilityCondition() {
        return visibilityCondition;
    }

    @Override
    public void setVisibilityCondition(BooleanSupplier condition) {
        visibilityCondition = condition;
    }

    protected enum Content {
        TEXT, ICON, ICON_AND_TEXT;

        static Content of(RichText text, GuiTexture texture) {
            if (text == null) return ICON;
            return texture == null ? TEXT : ICON_AND_TEXT;
        }

        public boolean hasIcon() {
            return this != TEXT;
        }

        public boolean hasText() {
            return this != ICON;
        }
    }
}

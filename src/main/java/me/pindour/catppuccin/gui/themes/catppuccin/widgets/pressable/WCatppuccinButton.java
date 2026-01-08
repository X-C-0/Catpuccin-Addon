package me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable;

import me.pindour.catppuccin.gui.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.widgets.IConditionalWidget;
import me.pindour.catppuccin.utils.ColorUtils;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;

import java.util.function.BooleanSupplier;

public class WCatppuccinButton extends WButton implements IConditionalWidget, CatppuccinWidget {
    private BooleanSupplier visibilityCondition;
    private RichText richText;

    public WCatppuccinButton(RichText text, GuiTexture texture) {
        super(text.getPlainText(), texture);
        this.richText = text;
    }

    public WCatppuccinButton(GuiTexture texture) {
        super(null, texture);
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();

        if (richText != null) {
            textWidth = theme().textWidth(richText);

            width = pad + textWidth + pad;
            height = pad + theme.textHeight() + pad;
        }
        else {
            double s = theme.textHeight();

            width = pad + s + pad;
            height = pad + s + pad;
        }
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (!shouldRender(mouseOver)) return;

        CatppuccinGuiTheme theme = theme();
        double pad = pad();

        renderBackground(
                this,
                ColorUtils.withAlpha(theme.accentColor(), mouseOver ? 0.8 : 0),
                theme().backgroundColor.get(pressed, mouseOver)
        );

        if (richText != null) {
            renderer().text(
                    richText,
                    x + width / 2 - textWidth / 2,
                    y + pad, theme.textColor()
            );
        }
        else {
            double ts = theme.textHeight();
            renderer.quad(x + width / 2 - ts / 2, y + pad, ts, ts, texture, theme.textColor());
        }
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
}

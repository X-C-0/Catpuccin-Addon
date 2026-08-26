package me.pindour.catppuccin.gui.themes.catppuccin.widgets;

import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.AlignmentX;
import meteordevelopment.meteorclient.gui.widgets.WLabel;

public class WCatppuccinLabel extends WLabel implements CatppuccinWidget {
    protected RichText richText;
    private AlignmentX alignX = AlignmentX.Left;

    public WCatppuccinLabel(RichText text) {
        super(text.getPlainText(), false);
        richText = text;
    }

    @Override
    protected void onCalculateSize() {
        width = theme().textWidth(richText);
        height = theme().textHeight(richText);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (text.isEmpty()) return;

        double textWidth = theme().textWidth(text);
        double offsetX = 0;

        switch (alignX) {
            case Center -> offsetX = width / 2 - textWidth / 2;
            case Right -> offsetX = width - textWidth;
        }

        renderer().text(
                richText,
                x + offsetX,
                y,
                color != null ? color : theme().textColor()
        );
    }

    public void textLeft() {
        alignX = AlignmentX.Left;
    }

    public void textCenter() {
        alignX = AlignmentX.Center;
    }

    public void textRight() {
        alignX = AlignmentX.Right;
    }

    public void set(RichText text) {
        if (Math.round(theme().textWidth(text)) != width) invalidate();

        this.text = text.getPlainText();
        richText = text;
    }

    @Override
    public void set(String text) {
        set(RichText.of(text));
    }
}

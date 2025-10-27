package me.pindour.catpuccin.gui.themes.catpuccin.widgets;

import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WLabel;

public class WCatpuccinLabel extends WLabel implements CatpuccinWidget {
    private RichText richText;

    public WCatpuccinLabel(RichText text) {
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

        catpuccinRenderer().text(
                richText,
                x,
                y,
                color != null ? color : theme().textColor()
        );
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

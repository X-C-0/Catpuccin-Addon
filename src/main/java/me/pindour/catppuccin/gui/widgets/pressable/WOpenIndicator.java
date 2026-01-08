package me.pindour.catppuccin.gui.widgets.pressable;

import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;

public abstract class WOpenIndicator extends WPressable {
    public boolean open;

    public WOpenIndicator(boolean open) {
        this.open = open;
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();
        double s = theme.textHeight();

        width = pad + s + pad;
        height = pad + s + pad;
    }

    @Override
    protected void onPressed(int button) {
        open = !open;
    }
}

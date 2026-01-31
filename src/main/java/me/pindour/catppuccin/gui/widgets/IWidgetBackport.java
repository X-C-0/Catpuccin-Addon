package me.pindour.catppuccin.gui.widgets;

import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;

@SuppressWarnings("unused")
public interface IWidgetBackport {
    boolean catppuccin$isFocused();
    void catppuccin$setFocused(boolean focused);
    WView catppuccin$getView();
    boolean catppuccin$isWidgetInView(WWidget widget);
}

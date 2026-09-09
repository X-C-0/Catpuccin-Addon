package me.pindour.catppuccin.gui.widgets;

import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;

@SuppressWarnings("unused")
public interface IWidgetBackport {
    boolean catppuccin$isFocused();
    boolean catppuccin$isSelfFocused();

    void catppuccin$setFocused(boolean focused);

    boolean catppuccin$hasInstantTooltips();
    void catppuccin$setInstantTooltips(boolean instant);

    WView catppuccin$getView();
    boolean catppuccin$isWidgetInView(WWidget widget);
}

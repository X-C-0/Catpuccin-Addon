package me.pindour.catpuccin.utils;

import me.pindour.catpuccin.mixininterface.IWContainer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import org.jetbrains.annotations.Nullable;

public class WidgetUtils {
    public static boolean isWidgetInside(WWidget inner, WWidget outer) {
        if (inner == null || outer == null) return true;
        return ((inner.y >= outer.y && inner.y <= outer.y + outer.height) ||
                (inner.y + inner.height >= outer.y && inner.y + inner.height <= outer.y + outer.height)) ||
                ((outer.y >= inner.y && outer.y <= inner.y + inner.height) ||
                        (outer.y + outer.height >= inner.y && outer.y + outer.height <= inner.y + inner.height));
    }

    @Nullable
    public static WView findParentView(WWidget widget) {
        return switch (widget) {
            case null -> null;
            case WView view -> view;
            case WWindow window -> window.view;
            case WContainer container -> {
                WView view = ((IWContainer)container).catpuccin$getParentView();
                yield  view != null ? view : findParentView(widget.parent);
            }
            default -> findParentView(widget.parent);
        };
    }
}

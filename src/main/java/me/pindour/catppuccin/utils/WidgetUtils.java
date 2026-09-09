package me.pindour.catppuccin.utils;

import me.pindour.catppuccin.gui.themes.catppuccin.icons.CatppuccinBuiltinIcons;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable.WCatppuccinButton;
import me.pindour.catppuccin.gui.widgets.IWidgetBackport;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.settings.Setting;

import java.util.function.BooleanSupplier;

public class WidgetUtils {

    public static Cell<WCatppuccinButton> reset(WContainer c, Setting<?> setting, Runnable action) {
        return reset(c, setting, action, null);
    }

    public static Cell<WCatppuccinButton> reset(WContainer c, Setting<?> setting, Runnable action, BooleanSupplier visibilityCondition) {
        WCatppuccinButton button = (WCatppuccinButton) c.getTheme().button(CatppuccinBuiltinIcons.RESET.texture());

        button.setVisibilityCondition(visibilityCondition);
        button.tooltip = "Reset";
        button.action = () -> {
            if (setting != null) setting.reset();
            if (action != null) action.run();
        };

        return c.add(button);
    }

    public static void enableInstantTooltips(WWidget widget) {
        ((IWidgetBackport) widget).catppuccin$setInstantTooltips(true);
    }
}

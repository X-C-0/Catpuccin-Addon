package me.pindour.catpuccin.utils;

import me.pindour.catpuccin.gui.themes.catpuccin.icons.CatpuccinIcons;
import me.pindour.catpuccin.gui.themes.catpuccin.widgets.pressable.WCatpuccinButton;
import meteordevelopment.meteorclient.gui.utils.Cell;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.settings.Setting;

import java.util.function.BooleanSupplier;

public class WidgetUtils {

    public static Cell<WCatpuccinButton> reset(WContainer c, Setting<?> setting, Runnable action) {
        return reset(c, setting, action, null);
    }

    public static Cell<WCatpuccinButton> reset(WContainer c, Setting<?> setting, Runnable action, BooleanSupplier visibilityCondition) {
        WCatpuccinButton button = (WCatpuccinButton) c.getTheme().button(CatpuccinIcons.RESET.texture());

        button.setVisibilityCondition(visibilityCondition);
        button.tooltip = "Reset";
        button.action = () -> {
            setting.reset();
            if (action != null) action.run();
        };

        return c.add(button);
    }
}

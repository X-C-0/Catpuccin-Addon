package me.pindour.catppuccin.gui.themes.catppuccin.widgets.settings;

import me.pindour.catppuccin.gui.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable.WCatppuccinButton;
import meteordevelopment.meteorclient.gui.widgets.WKeybind;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.Keybind;

public class WCatppuccinKeybind extends WKeybind implements CatppuccinWidget {
    public Runnable action;
    public Runnable actionOnSet;

    private WCatppuccinButton button;

    private final String title;
    private final Keybind keybind;
    private final Keybind defaultValue;

    private final String listeningText = "Press any key";
    private boolean listening;

    public WCatppuccinKeybind(String title, Keybind keybind, Keybind defaultValue) {
        super(keybind, defaultValue);
        this.title = title != null && !title.isEmpty() ? title : "Bind";
        this.keybind = keybind;
        this.defaultValue = defaultValue;
    }

    @Override
    protected void onCalculateSize() {
        button.width = Math.max(theme.textWidth(listeningText), button.width);
        super.onCalculateSize();
    }

    @Override
    public void init() {
        button = add(theme().button(RichText.of(""))).widget();
        button.action = () -> {
            listening = true;
            button.set(listeningText);

            if (actionOnSet != null) actionOnSet.run();
        };

        refreshLabel();
    }

    @Override
    public boolean onClear() {
        if (listening) {
            keybind.reset();
            reset();

            return true;
        }

        return false;
    }

    @Override
    public boolean onAction(boolean isKey, int value, int modifiers) {
        if (listening && keybind.canBindTo(isKey, value, modifiers)) {
            keybind.set(isKey, value, modifiers);
            reset();

            return true;
        }

        return false;
    }

    @Override
    public void resetBind() {
        keybind.set(defaultValue);
        reset();
    }

    @Override
    public void reset() {
        listening = false;
        refreshLabel();
        if (Modules.get().isBinding()) {
            Modules.get().setModuleToBind(null);
        }
    }

    private void refreshLabel() {
        button.set(RichText.bold(title + ": ").append(keybind.toString()));
    }
}

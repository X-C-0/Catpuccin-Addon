package me.pindour.catppuccin.gui.screens;

import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.utils.Utils;

public class CatppuccinSearchScreen extends WidgetScreen {
    private final CatppuccinGuiTheme theme;

    public CatppuccinSearchScreen(GuiTheme theme) {
        super(theme, "Search");
        this.theme = (CatppuccinGuiTheme) theme;
    }

    @Override
    public void initWidgets() {
        double margin = Utils.getWindowHeight() / 8.0;
        add(theme.search()).marginTop(margin).top().centerX();
    }
}

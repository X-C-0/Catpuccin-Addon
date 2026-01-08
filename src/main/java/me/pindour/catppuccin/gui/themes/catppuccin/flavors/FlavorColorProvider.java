package me.pindour.catppuccin.gui.themes.catppuccin.flavors;

import me.pindour.catppuccin.gui.themes.catppuccin.colors.CatppuccinColor;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public interface FlavorColorProvider {
    CatppuccinColor getType();
    SettingColor getColor();
}

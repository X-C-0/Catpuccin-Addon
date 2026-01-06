package me.pindour.catpuccin.gui.themes.catpuccin.flavors;

import me.pindour.catpuccin.gui.themes.catpuccin.colors.CatppuccinColor;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public interface FlavorColorProvider {
    CatppuccinColor getType();
    SettingColor getColor();
}

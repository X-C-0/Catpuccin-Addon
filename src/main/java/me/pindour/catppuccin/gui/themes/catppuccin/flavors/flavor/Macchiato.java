package me.pindour.catppuccin.gui.themes.catppuccin.flavors.flavor;

import me.pindour.catppuccin.gui.themes.catppuccin.colors.CatppuccinColor;
import me.pindour.catppuccin.gui.themes.catppuccin.flavors.FlavorColorProvider;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public enum Macchiato implements FlavorColorProvider {
    Rosewater(new SettingColor(244, 219, 214), CatppuccinColor.Rosewater),
    Flamingo(new SettingColor(240, 198, 198), CatppuccinColor.Flamingo),
    Pink(new SettingColor(245, 189, 230), CatppuccinColor.Pink),
    Mauve(new SettingColor(198, 160, 246), CatppuccinColor.Mauve),
    Red(new SettingColor(237, 135, 150), CatppuccinColor.Red),
    Maroon(new SettingColor(238, 153, 160), CatppuccinColor.Maroon),
    Peach(new SettingColor(245, 169, 127), CatppuccinColor.Peach),
    Yellow(new SettingColor(238, 212, 159), CatppuccinColor.Yellow),
    Green(new SettingColor(166, 218, 149), CatppuccinColor.Green),
    Teal(new SettingColor(139, 213, 202), CatppuccinColor.Teal),
    Sky(new SettingColor(145, 215, 227), CatppuccinColor.Sky),
    Sapphire(new SettingColor(125, 196, 228), CatppuccinColor.Sapphire),
    Blue(new SettingColor(138, 173, 244), CatppuccinColor.Blue),
    Lavender(new SettingColor(183, 189, 248), CatppuccinColor.Lavender),
    Text(new SettingColor(202, 211, 245), CatppuccinColor.Text),
    Subtext1(new SettingColor(184, 192, 224), CatppuccinColor.Subtext1),
    Subtext0(new SettingColor(165, 173, 203), CatppuccinColor.Subtext0),
    Overlay2(new SettingColor(147, 154, 183), CatppuccinColor.Overlay2),
    Overlay1(new SettingColor(128, 135, 162), CatppuccinColor.Overlay1),
    Overlay0(new SettingColor(110, 115, 141), CatppuccinColor.Overlay0),
    Surface2(new SettingColor(91, 96, 120), CatppuccinColor.Surface2),
    Surface1(new SettingColor(73, 77, 100), CatppuccinColor.Surface1),
    Surface0(new SettingColor(54, 58, 79), CatppuccinColor.Surface0),
    Base(new SettingColor(36, 39, 58), CatppuccinColor.Base),
    Mantle(new SettingColor(30, 32, 48), CatppuccinColor.Mantle),
    Crust(new SettingColor(24, 25, 38), CatppuccinColor.Crust);

    private final SettingColor color;
    private final CatppuccinColor type;

    Macchiato(SettingColor color, CatppuccinColor type) {
        this.color = color;
        this.type = type;
    }

    @Override
    public SettingColor getColor() {
        return color;
    }

    @Override
    public CatppuccinColor getType() {
        return type;
    }
}

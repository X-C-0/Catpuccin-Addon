package me.pindour.catppuccin.gui.themes.catppuccin.flavors.flavor;

import me.pindour.catppuccin.gui.themes.catppuccin.colors.CatppuccinColor;
import me.pindour.catppuccin.gui.themes.catppuccin.flavors.FlavorColorProvider;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public enum Frappe implements FlavorColorProvider {
    Rosewater(new SettingColor(242, 213, 207), CatppuccinColor.Rosewater),
    Flamingo(new SettingColor(238, 190, 190), CatppuccinColor.Flamingo),
    Pink(new SettingColor(244, 184, 228), CatppuccinColor.Pink),
    Mauve(new SettingColor(202, 158, 230), CatppuccinColor.Mauve),
    Red(new SettingColor(231, 130, 132), CatppuccinColor.Red),
    Maroon(new SettingColor(234, 153, 156), CatppuccinColor.Maroon),
    Peach(new SettingColor(239, 159, 118), CatppuccinColor.Peach),
    Yellow(new SettingColor(229, 200, 144), CatppuccinColor.Yellow),
    Green(new SettingColor(166, 209, 137), CatppuccinColor.Green),
    Teal(new SettingColor(129, 200, 190), CatppuccinColor.Teal),
    Sky(new SettingColor(153, 209, 219), CatppuccinColor.Sky),
    Sapphire(new SettingColor(133, 193, 220), CatppuccinColor.Sapphire),
    Blue(new SettingColor(140, 170, 238), CatppuccinColor.Blue),
    Lavender(new SettingColor(186, 187, 241), CatppuccinColor.Lavender),
    Text(new SettingColor(198, 208, 245), CatppuccinColor.Text),
    Subtext1(new SettingColor(181, 191, 226), CatppuccinColor.Subtext1),
    Subtext0(new SettingColor(165, 173, 206), CatppuccinColor.Subtext0),
    Overlay2(new SettingColor(148, 156, 187), CatppuccinColor.Overlay2),
    Overlay1(new SettingColor(131, 139, 167), CatppuccinColor.Overlay1),
    Overlay0(new SettingColor(115, 121, 148), CatppuccinColor.Overlay0),
    Surface2(new SettingColor(98, 104, 128), CatppuccinColor.Surface2),
    Surface1(new SettingColor(81, 87, 109), CatppuccinColor.Surface1),
    Surface0(new SettingColor(65, 69, 89), CatppuccinColor.Surface0),
    Base(new SettingColor(48, 52, 70), CatppuccinColor.Base),
    Mantle(new SettingColor(41, 44, 60), CatppuccinColor.Mantle),
    Crust(new SettingColor(35, 38, 52), CatppuccinColor.Crust);

    private final SettingColor color;
    private final CatppuccinColor type;

    Frappe(SettingColor color, CatppuccinColor type) {
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

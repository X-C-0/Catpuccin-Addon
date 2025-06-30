package me.pindour.catpuccin.gui.themes.catpuccin.flavor.flavors;

import me.pindour.catpuccin.gui.themes.catpuccin.colors.CatppuccinColor;
import me.pindour.catpuccin.gui.themes.catpuccin.flavor.FlavorColorProvider;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public enum Latte implements FlavorColorProvider {
    Rosewater(new SettingColor(220, 138, 120), CatppuccinColor.Rosewater),
    Flamingo(new SettingColor(221, 120, 120), CatppuccinColor.Flamingo),
    Pink(new SettingColor(234, 118, 203), CatppuccinColor.Pink),
    Mauve(new SettingColor(136, 57, 239), CatppuccinColor.Mauve),
    Red(new SettingColor(210, 15, 57), CatppuccinColor.Red),
    Maroon(new SettingColor(230, 69, 83), CatppuccinColor.Maroon),
    Peach(new SettingColor(254, 100, 11), CatppuccinColor.Peach),
    Yellow(new SettingColor(223, 142, 29), CatppuccinColor.Yellow),
    Green(new SettingColor(64, 160, 43), CatppuccinColor.Green),
    Teal(new SettingColor(23, 146, 153), CatppuccinColor.Teal),
    Sky(new SettingColor(4, 165, 229), CatppuccinColor.Sky),
    Sapphire(new SettingColor(32, 159, 181), CatppuccinColor.Sapphire),
    Blue(new SettingColor(30, 102, 245), CatppuccinColor.Blue),
    Lavender(new SettingColor(114, 135, 253), CatppuccinColor.Lavender),
    Text(new SettingColor(76, 79, 105), CatppuccinColor.Text),
    Subtext1(new SettingColor(92, 95, 119), CatppuccinColor.Subtext1),
    Subtext0(new SettingColor(108, 111, 133), CatppuccinColor.Subtext0),
    Overlay2(new SettingColor(124, 127, 147), CatppuccinColor.Overlay2),
    Overlay1(new SettingColor(140, 143, 161), CatppuccinColor.Overlay1),
    Overlay0(new SettingColor(156, 160, 176), CatppuccinColor.Overlay0),
    Surface2(new SettingColor(172, 176, 190), CatppuccinColor.Surface2),
    Surface1(new SettingColor(188, 192, 204), CatppuccinColor.Surface1),
    Surface0(new SettingColor(204, 208, 218), CatppuccinColor.Surface0),
    Base(new SettingColor(239, 241, 245), CatppuccinColor.Base),
    Mantle(new SettingColor(230, 233, 239), CatppuccinColor.Mantle),
    Crust(new SettingColor(220, 224, 232), CatppuccinColor.Crust);

    private final SettingColor color;
    private final CatppuccinColor type;

    Latte(SettingColor color, CatppuccinColor type) {
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

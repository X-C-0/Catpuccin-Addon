package me.pindour.catpuccin.gui.themes.catpuccin.flavor.flavors;

import me.pindour.catpuccin.gui.themes.catpuccin.colors.CatppuccinColor;
import me.pindour.catpuccin.gui.themes.catpuccin.flavor.FlavorColorProvider;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public enum Mocha implements FlavorColorProvider {
    Rosewater(new SettingColor(245, 224, 220), CatppuccinColor.Rosewater),
    Flamingo(new SettingColor(242, 205, 205), CatppuccinColor.Flamingo),
    Pink(new SettingColor(245, 194, 231), CatppuccinColor.Pink),
    Mauve(new SettingColor(203, 166, 247), CatppuccinColor.Mauve),
    Red(new SettingColor(243, 139, 168), CatppuccinColor.Red),
    Maroon(new SettingColor(235, 160, 172), CatppuccinColor.Maroon),
    Peach(new SettingColor(250, 179, 135), CatppuccinColor.Peach),
    Yellow(new SettingColor(249, 226, 175), CatppuccinColor.Yellow),
    Green(new SettingColor(166, 227, 161), CatppuccinColor.Green),
    Teal(new SettingColor(148, 226, 213), CatppuccinColor.Teal),
    Sky(new SettingColor(137, 220, 235), CatppuccinColor.Sky),
    Sapphire(new SettingColor(116, 199, 236), CatppuccinColor.Sapphire),
    Blue(new SettingColor(137, 180, 250), CatppuccinColor.Blue),
    Lavender(new SettingColor(180, 190, 254), CatppuccinColor.Lavender),
    Text(new SettingColor(205, 214, 244), CatppuccinColor.Text),
    Subtext1(new SettingColor(186, 194, 222), CatppuccinColor.Subtext1),
    Subtext0(new SettingColor(166, 173, 200), CatppuccinColor.Subtext0),
    Overlay2(new SettingColor(147, 153, 178), CatppuccinColor.Overlay2),
    Overlay1(new SettingColor(127, 132, 156), CatppuccinColor.Overlay1),
    Overlay0(new SettingColor(108, 112, 134), CatppuccinColor.Overlay0),
    Surface2(new SettingColor(88, 91, 112), CatppuccinColor.Surface2),
    Surface1(new SettingColor(69, 71, 90), CatppuccinColor.Surface1),
    Surface0(new SettingColor(49, 50, 68), CatppuccinColor.Surface0),
    Base(new SettingColor(30, 30, 46), CatppuccinColor.Base),
    Mantle(new SettingColor(24, 24, 37), CatppuccinColor.Mantle),
    Crust(new SettingColor(17, 17, 27), CatppuccinColor.Crust);

    private final SettingColor color;
    private final CatppuccinColor type;

    Mocha(SettingColor color, CatppuccinColor type) {
        this.color = color;
        this.type = type;
    }

    public SettingColor getColor() {
        return color;
    }

    public CatppuccinColor getType() {
        return type;
    }
}

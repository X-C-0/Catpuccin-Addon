package me.pindour.catppuccin.gui.themes.catppuccin.flavors;

import me.pindour.catppuccin.CatppuccinAddon;
import me.pindour.catppuccin.gui.themes.catppuccin.colors.CatppuccinColor;
import me.pindour.catppuccin.gui.themes.catppuccin.flavors.flavor.Frappe;
import me.pindour.catppuccin.gui.themes.catppuccin.flavors.flavor.Latte;
import me.pindour.catppuccin.gui.themes.catppuccin.flavors.flavor.Macchiato;
import me.pindour.catppuccin.gui.themes.catppuccin.flavors.flavor.Mocha;
import me.pindour.catppuccin.gui.themes.catppuccin.flavors.flavor.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public enum CatppuccinFlavors {
    Latte(Latte.class),
    Frappe(Frappe.class),
    Macchiato(Macchiato.class),
    Mocha(Mocha.class);


    private final Class<? extends FlavorColorProvider> colorProviderClass;

    CatppuccinFlavors(Class<? extends FlavorColorProvider> providerClass) {
        this.colorProviderClass = providerClass;
    }

    public SettingColor getColor(CatppuccinColor color) {
        try {
            for (FlavorColorProvider provider : colorProviderClass.getEnumConstants()) {
                if (provider.getType() == color) {
                    return provider.getColor();
                }
            }
            return new SettingColor(255, 255, 255); // fallback
        } catch (Exception e) {
            CatppuccinAddon.LOG.error(e.getMessage());
            return new SettingColor(255, 255, 255);
        }
    }
}

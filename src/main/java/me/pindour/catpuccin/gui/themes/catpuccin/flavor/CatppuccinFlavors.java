package me.pindour.catpuccin.gui.themes.catpuccin.flavor;

import me.pindour.catpuccin.CatpuccinAddon;
import me.pindour.catpuccin.gui.themes.catpuccin.colors.CatppuccinColor;
import me.pindour.catpuccin.gui.themes.catpuccin.flavor.flavors.*;
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
            CatpuccinAddon.LOG.error(e.getMessage());
            return new SettingColor(255, 255, 255);
        }
    }
}

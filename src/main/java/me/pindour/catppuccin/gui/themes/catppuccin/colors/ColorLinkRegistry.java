package me.pindour.catppuccin.gui.themes.catppuccin.colors;

import meteordevelopment.meteorclient.settings.ColorSetting;
import net.minecraft.nbt.CompoundTag;

import java.util.IdentityHashMap;
import java.util.Map;

public class ColorLinkRegistry {
    public static final String KEY_LINK = "catppuccin-link";

    private static final Map<ColorSetting, ColorLink> links = new IdentityHashMap<>();

    public static void link(ColorSetting setting, CatppuccinColor color) {
        ColorLink link = new ColorLink(color);
        links.put(setting, link);
        link.apply(setting);
    }

    public static void unlink(ColorSetting setting) {
        links.remove(setting);
    }

    public static boolean isLinked(ColorSetting setting) {
        return links.containsKey(setting);
    }

    public static ColorLink getLink(ColorSetting setting) {
        return links.get(setting);
    }

    public static void linkFromTag(ColorSetting setting, CompoundTag settingTag) {
        //? if <=1.21.4 {
        /*CompoundTag linkTag = settingTag.get(KEY_LINK) instanceof CompoundTag tag ? tag : null;
        *///? } else {
        CompoundTag linkTag = settingTag.getCompound(KEY_LINK).orElse(null);
        //? }

        ColorLink link = linkTag == null ? null : new ColorLink().fromTag(linkTag);

        if (link == null) {
            links.remove(setting);
            return;
        }

        links.put(setting, link);
        link.apply(setting);
    }

    public static void applyAll() {
        links.forEach((setting, link) -> link.apply(setting));
    }
}
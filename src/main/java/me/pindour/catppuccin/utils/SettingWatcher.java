package me.pindour.catppuccin.utils;

import meteordevelopment.meteorclient.settings.Setting;

import java.util.HashSet;
import java.util.Set;

public class SettingWatcher {
    private static boolean watching = false;
    private static final Set<Setting<?>> touched = new HashSet<>();

    public static void start() {
        watching = true;
        touched.clear();
    }

    public static Set<Setting<?>> stop() {
        watching = false;
        return new HashSet<>(touched);
    }

    public static void touch(Setting<?> setting) {
        if (watching) {
            touched.add(setting);
        }
    }
}

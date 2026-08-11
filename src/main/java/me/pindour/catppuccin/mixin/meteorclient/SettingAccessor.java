package me.pindour.catppuccin.mixin.meteorclient;

import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Setting.class, remap = false)
public interface SettingAccessor {
    @Accessor("visible")
    IVisible catppuccin$getVisible();
}

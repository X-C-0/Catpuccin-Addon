package me.pindour.catppuccin.mixin.meteorclient;

import me.pindour.catppuccin.gui.themes.catppuccin.colors.ColorLinkRegistry;
import me.pindour.catppuccin.utils.SettingWatcher;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Setting.class)
public class SettingMixin {
    @Inject(method = "get", at = @At("HEAD"))
    private void catppuccin$get(CallbackInfoReturnable<Object> cir) {
        SettingWatcher.touch((Setting<?>) (Object) this);
    }

    @Inject(method = "reset", at = @At("TAIL"))
    private void catppuccin$reset(CallbackInfo ci) {
        if ((Object) this instanceof ColorSetting s)
            ColorLinkRegistry.unlink(s);
    }
}
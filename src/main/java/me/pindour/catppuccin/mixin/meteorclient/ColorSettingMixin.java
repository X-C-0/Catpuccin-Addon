package me.pindour.catppuccin.mixin.meteorclient;

import me.pindour.catppuccin.gui.themes.catppuccin.colors.ColorLink;
import me.pindour.catppuccin.gui.themes.catppuccin.colors.ColorLinkRegistry;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ColorSetting.class, remap = false)
public abstract class ColorSettingMixin {
    @Inject(method = "load*", at = @At("TAIL"))
    private void catppuccin$load(CompoundTag tag, CallbackInfoReturnable<SettingColor> cir) {
        ColorLinkRegistry.linkFromTag((ColorSetting) (Object) this, tag);
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void catppuccin$save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        ColorLink link = ColorLinkRegistry.getLink((ColorSetting) (Object) this);
        if (link != null) tag.put(ColorLinkRegistry.KEY_LINK, link.toTag());
    }
}

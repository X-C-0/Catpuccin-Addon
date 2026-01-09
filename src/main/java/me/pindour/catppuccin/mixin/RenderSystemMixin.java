package me.pindour.catppuccin.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.pindour.catppuccin.renderer.CatppuccinRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public abstract class RenderSystemMixin {
    @Inject(method = "flipFrame", at = @At("TAIL"))
    private static void catppuccin$flipFrame(CallbackInfo info) {
        CatppuccinRenderer.flipFrame();
    }
}
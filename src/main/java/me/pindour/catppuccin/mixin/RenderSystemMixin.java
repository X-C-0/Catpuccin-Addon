package me.pindour.catppuccin.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;

//? if <=26.1.2 {
/*import me.pindour.catppuccin.renderer.CatppuccinRenderer;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///? }

@Mixin(RenderSystem.class)
public abstract class RenderSystemMixin {
    //? if <=26.1.2 {
    /*@Inject(method = "flipFrame", at = @At("TAIL"))
    private static void catppuccin$flipFrame(CallbackInfo info) {
        CatppuccinRenderer.get().flipFrame();
    }
    *///? }
}
package me.pindour.catpuccin.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.pindour.catpuccin.gui.renderer.CatpuccinRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public abstract class RenderSystemMixin {
    @Inject(method = "flipFrame", at = @At("TAIL"))
    private static void catpuccin$flipFrame(CallbackInfo info) {
        CatpuccinRenderer.flipFrame();
    }
}

package me.pindour.catppuccin.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
//? if >=26.2 {
import me.pindour.catppuccin.renderer.CatppuccinRenderer;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? }

@Mixin(Minecraft.class)
public class MinecraftRenderFrameMixin {
    //? if >=26.2 {
    @Inject(method = "renderFrame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;endFrame()V", shift = At.Shift.AFTER))
    private void catppuccin$afterRenderFrame(boolean advanceGameTime, CallbackInfo ci) {
        CatppuccinRenderer.get().flipFrame();
    }
    //? }
}

package me.pindour.catppuccin.mixin.meteorclient;

import meteordevelopment.meteorclient.gui.renderer.operations.TextOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TextOperation.class, remap = false)
public interface TextOperationAccessor {
    @Accessor("text")
    String catppuccin$getText();
}
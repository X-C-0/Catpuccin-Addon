package me.pindour.catppuccin.mixin.meteorclient;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderOperation;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiRenderOperation.class, remap = false)
public interface GuiRenderOperationAccessor {
    @Accessor("x")
    double catppuccin$getX();

    @Accessor("y")
    double catppuccin$getY();

    @Accessor("color")
    Color catppuccin$getColor();
}
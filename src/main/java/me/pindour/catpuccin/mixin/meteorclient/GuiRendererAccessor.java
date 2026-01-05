package me.pindour.catpuccin.mixin.meteorclient;

import it.unimi.dsi.fastutil.Stack;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.Scissor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiRenderer.class, remap = false)
public interface GuiRendererAccessor {
    @Accessor("scissorStack")
    Stack<Scissor> getScissorStack();
}

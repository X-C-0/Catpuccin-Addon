package me.pindour.catpuccin.mixin.meteorclient;

#if MC_VER < MC_1_21_10
import java.util.Stack;
#else
import it.unimi.dsi.fastutil.Stack;
#endif
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.Scissor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiRenderer.class, remap = false)
public interface GuiRendererAccessor {
    @Accessor("scissorStack")
    Stack<Scissor> getScissorStack();
}

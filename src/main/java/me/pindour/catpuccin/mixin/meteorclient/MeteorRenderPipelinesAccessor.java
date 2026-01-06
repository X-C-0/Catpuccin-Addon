package me.pindour.catpuccin.mixin.meteorclient;

#if MC_VER > MC_1_21_4
import com.mojang.blaze3d.pipeline.RenderPipeline;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
#endif
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(
    #if MC_VER > MC_1_21_4
    value = MeteorRenderPipelines.class,
    #else
    targets = "meteordevelopment.meteorclient.renderer.MeteorRenderPipelines",
    #endif
    remap = false
)
public interface MeteorRenderPipelinesAccessor {
    @Invoker("add")
    #if MC_VER > MC_1_21_4
    static RenderPipeline invokeAdd(RenderPipeline pipeline) {
    #else
    static Object invokeAdd(Object pipeline) {
    #endif
        throw new AssertionError();
    }
}

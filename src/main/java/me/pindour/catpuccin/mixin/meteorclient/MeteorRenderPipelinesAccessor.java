package me.pindour.catpuccin.mixin.meteorclient;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = MeteorRenderPipelines.class, remap = false)
public interface MeteorRenderPipelinesAccessor {
    @Invoker("add")
    static RenderPipeline invokeAdd(RenderPipeline pipeline) {
        throw new AssertionError();
    }
}

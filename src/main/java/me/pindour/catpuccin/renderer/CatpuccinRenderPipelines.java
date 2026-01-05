package me.pindour.catpuccin.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import meteordevelopment.meteorclient.renderer.ExtendedRenderPipelineBuilder;
import meteordevelopment.meteorclient.renderer.MeteorVertexFormats;
import me.pindour.catpuccin.CatpuccinAddon;
import me.pindour.catpuccin.mixin.meteorclient.MeteorRenderPipelinesAccessor;
import net.minecraft.client.gl.UniformType;

public class CatpuccinRenderPipelines {
    private static final RenderPipeline.Snippet MESH_UNIFORMS = RenderPipeline.builder()
        .withUniform("MeshData", UniformType.UNIFORM_BUFFER)
        .buildSnippet();

    public static final RenderPipeline ROUNDED_UI = MeteorRenderPipelinesAccessor.invokeAdd(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(CatpuccinAddon.identifier("pipeline/rounded_ui"))
        .withVertexFormat(MeteorVertexFormats.POS2_TEXTURE_COLOR, VertexFormat.DrawMode.TRIANGLES)
        .withVertexShader(CatpuccinAddon.identifier("shaders/rounded_ui.vert"))
        .withFragmentShader(CatpuccinAddon.identifier("shaders/rounded_ui.frag"))
        .withUniform("RoundedRectData", UniformType.UNIFORM_BUFFER)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .withDepthWrite(false)
        .withBlend(BlendFunction.TRANSLUCENT)
        .withCull(false)
        .build()
    );
}

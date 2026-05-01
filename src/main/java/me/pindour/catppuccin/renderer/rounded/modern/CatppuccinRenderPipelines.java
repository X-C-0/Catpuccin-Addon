package me.pindour.catppuccin.renderer.rounded.modern;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
//? if >=1.21.5 {
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.pindour.catppuccin.CatppuccinAddon;
import meteordevelopment.meteorclient.renderer.ExtendedRenderPipelineBuilder;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.renderer.MeteorVertexFormats;

import java.lang.reflect.Method;
import java.util.Optional;

public class CatppuccinRenderPipelines {

    private static final RenderPipeline.Snippet MESH_UNIFORMS = RenderPipeline.builder()
        .withUniform("MeshData", UniformType.UNIFORM_BUFFER)
        .buildSnippet();

    public static final RenderPipeline ROUNDED_UI = register(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(CatppuccinAddon.identifier("pipeline/rounded_ui"))
        .withVertexFormat(MeteorVertexFormats.POS2_TEXTURE_COLOR, VertexFormat.Mode.TRIANGLES)
        .withVertexShader(CatppuccinAddon.identifier("shaders/rounded_ui.vert"))
        .withFragmentShader(CatppuccinAddon.identifier("shaders/rounded_ui.frag"))
        .withUniform("RoundedRectData", UniformType.UNIFORM_BUFFER)
        .withDepthStencilState(Optional.empty())
        .withColorTargetState(new ColorTargetState(new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA)))
        .withCull(false)
        .build()
    );

    private static RenderPipeline register(RenderPipeline pipeline) {
        try {
            Method method = MeteorRenderPipelines.class.getDeclaredMethod("add", RenderPipeline.class);
            method.setAccessible(true);
            return (RenderPipeline) method.invoke(null, pipeline);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to register pipeline", e);
        }
    }
}
//?}
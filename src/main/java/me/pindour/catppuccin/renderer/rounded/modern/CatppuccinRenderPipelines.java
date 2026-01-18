package me.pindour.catppuccin.renderer.rounded.modern;

//? if >=1.21.5 {
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.pindour.catppuccin.CatppuccinAddon;
import meteordevelopment.meteorclient.renderer.ExtendedRenderPipelineBuilder;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.renderer.MeteorVertexFormats;
import net.minecraft.client.gl.UniformType;

import java.lang.reflect.Method;

public class CatppuccinRenderPipelines {

    private static final RenderPipeline.Snippet MESH_UNIFORMS = RenderPipeline.builder()
        .withUniform("MeshData", UniformType.UNIFORM_BUFFER)
        .buildSnippet();

    public static final RenderPipeline ROUNDED_UI = register(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
        .withLocation(CatppuccinAddon.identifier("pipeline/rounded_ui"))
        .withVertexFormat(MeteorVertexFormats.POS2_TEXTURE_COLOR, VertexFormat.DrawMode.TRIANGLES)
        .withVertexShader(CatppuccinAddon.identifier("shaders/rounded_ui.vert"))
        .withFragmentShader(CatppuccinAddon.identifier("shaders/rounded_ui.frag"))
        .withUniform("RoundedRectData", UniformType.UNIFORM_BUFFER)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .withDepthWrite(false)
        .withBlend(BlendFunction.TRANSLUCENT)
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
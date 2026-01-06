package me.pindour.catpuccin.renderer;

#if MC_VER >= MC_1_21_5
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.pindour.catpuccin.CatpuccinAddon;
import meteordevelopment.meteorclient.renderer.ExtendedRenderPipelineBuilder;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.renderer.MeteorVertexFormats;
import net.minecraft.client.gl.UniformType;
#endif

public class CatpuccinRenderPipelines {
    private CatpuccinRenderPipelines() {
    }

    #if MC_VER >= MC_1_21_6
    private static final RenderPipeline.Snippet MESH_UNIFORMS = RenderPipeline.builder()
        .withUniform("MeshData", UniformType.UNIFORM_BUFFER)
        .buildSnippet();

    public static final RenderPipeline ROUNDED_UI = register(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
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

    #elif MC_VER == MC_1_21_5
    public static final RenderPipeline ROUNDED_UI_LEGACY = register(
        new ExtendedRenderPipelineBuilder()
        .withLocation(CatpuccinAddon.identifier("pipeline/rounded_ui_legacy"))
        .withVertexFormat(MeteorVertexFormats.POS2_TEXTURE_COLOR, VertexFormat.DrawMode.TRIANGLES)
        .withVertexShader(CatpuccinAddon.identifier("shaders/rounded_ui_legacy.vert"))
        .withFragmentShader(CatpuccinAddon.identifier("shaders/rounded_ui_legacy.frag"))
        .withUniform("u_Proj", UniformType.MATRIX4X4)
        .withUniform("u_ModelView", UniformType.MATRIX4X4)
        .withUniform("u_FillColor", UniformType.VEC4)
        .withUniform("u_BorderColor", UniformType.VEC4)
        .withUniform("u_Radii0", UniformType.VEC2)
        .withUniform("u_Radii1", UniformType.VEC2)
        .withUniform("u_BorderData", UniformType.VEC2)
        .withUniform("u_HalfSize", UniformType.VEC2)
        .withUniform("u_Padding", UniformType.VEC2)
        .withUniform("u_ClipMin", UniformType.VEC2)
        .withUniform("u_ClipMax", UniformType.VEC2)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .withDepthWrite(false)
        .withBlend(BlendFunction.TRANSLUCENT)
        .withCull(false)
        .build()
    );
    #endif

    #if MC_VER >= MC_1_21_5
    private static RenderPipeline register(RenderPipeline pipeline) {
        try {
            java.lang.reflect.Method method = MeteorRenderPipelines.class.getDeclaredMethod("add", RenderPipeline.class);
            method.setAccessible(true);
            return (RenderPipeline) method.invoke(null, pipeline);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to register pipeline", e);
        }
    }
    #endif
}

package me.pindour.catppuccin.renderer.rounded.modern;

//? if >=1.21.5 {
import me.pindour.catppuccin.renderer.rounded.RoundedRendererInternal;
import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class RoundedRendererModern implements RoundedRendererInternal {
    private final MeshBuilder roundedMesh;

    public RoundedRendererModern() {
        this.roundedMesh = new MeshBuilder(CatppuccinRenderPipelines.ROUNDED_UI);
    }

    @Override
    public void begin() {
        roundedMesh.begin();
    }

    @Override
    public void end() {
        if (roundedMesh.isBuilding()) roundedMesh.end();
    }

    @Override
    public void render(MatrixStack matrices) { }

    @Override
    public void render(double x, double y,
                       double width, double height,
                       float topLeft, float topRight,
                       float bottomLeft, float bottomRight,
                       Color fillColor, Color outlineColor, float outlineWidth) {

        double halfWidth = width * 0.5;
        double halfHeight = height * 0.5;

        roundedMesh.ensureQuadCapacity();
        roundedMesh.quad(
                roundedMesh.vec2(x, y).vec2(-halfWidth, -halfHeight).color(Color.WHITE).next(),
                roundedMesh.vec2(x, y + height).vec2(-halfWidth, halfHeight).color(Color.WHITE).next(),
                roundedMesh.vec2(x + width, y + height).vec2(halfWidth, halfHeight).color(Color.WHITE).next(),
                roundedMesh.vec2(x + width, y).vec2(halfWidth, -halfHeight).color(Color.WHITE).next()
        );

        if (roundedMesh.isBuilding()) roundedMesh.end();

        RoundedUniforms.update(width, height, topLeft, topRight, bottomLeft, bottomRight, fillColor, outlineColor, outlineWidth);
        MeshRenderer.begin()
                .attachments(MinecraftClient.getInstance().getFramebuffer())
                .pipeline(CatppuccinRenderPipelines.ROUNDED_UI)
                .mesh(roundedMesh)
                .uniform("RoundedRectData", RoundedUniforms.getUniformStorage())
                .end();

        roundedMesh.begin();
    }

    public void flipFrame() {
        RoundedUniforms.flipFrame();
    }
}
//?}
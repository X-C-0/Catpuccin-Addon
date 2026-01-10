package me.pindour.catppuccin.renderer.modern;

//? if >=1.21.5 {
import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;

public class RoundedRenderer {
    private final MeshBuilder roundedMesh;

    private double alpha = 1.0;

    public RoundedRenderer() {
        this.roundedMesh = new MeshBuilder(CatppuccinRenderPipelines.ROUNDED_UI);
    }

    public void begin() {
        roundedMesh.begin();
    }

    public void end() {
        if (roundedMesh.isBuilding()) roundedMesh.end();
    }

    public void setAlpha(double a) {
        this.alpha = a;
    }

    public void render(double x, double y, double width, double height, double radius, Color color,
                       boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
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

        RoundedUniforms.updateModern(width, height, radius, color, alpha, topLeft, topRight, bottomLeft, bottomRight);
        MeshRenderer.begin()
                .attachments(MinecraftClient.getInstance().getFramebuffer())
                .pipeline(CatppuccinRenderPipelines.ROUNDED_UI)
                .mesh(roundedMesh)
                .uniform("RoundedRectData", RoundedUniforms.getUniformStorage())
                .end();

        roundedMesh.begin();
    }
}
//?}
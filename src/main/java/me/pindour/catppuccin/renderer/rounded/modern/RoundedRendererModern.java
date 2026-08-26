package me.pindour.catppuccin.renderer.rounded.modern;

//? if >=1.21.5 {
import com.mojang.blaze3d.vertex.PoseStack;
import me.pindour.catppuccin.api.render.style.Outline;
import me.pindour.catppuccin.api.render.style.Shadow;
import me.pindour.catppuccin.renderer.rounded.RoundedRendererInternal;
import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.Minecraft;

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
    public void render(PoseStack stack) { }

    @Override
    public void render(double x, double y,
                       double width, double height,
                       float topLeft, float topRight,
                       float bottomLeft, float bottomRight,
                       Color fillColor,
                       Outline outline,
                       Shadow shadow) {

        double padX = shadow.padX();
        double padY = shadow.padY();

        double minX = x - padX;
        double minY = y - padY;
        double maxX = x + width + padX;
        double maxY = y + height + padY;

        roundedMesh.ensureQuadCapacity();
        roundedMesh.quad(
                roundedMesh.vec2(minX, minY).color(Color.WHITE).next(),
                roundedMesh.vec2(minX, maxY).color(Color.WHITE).next(),
                roundedMesh.vec2(maxX, maxY).color(Color.WHITE).next(),
                roundedMesh.vec2(maxX, minY).color(Color.WHITE).next()
        );

        if (roundedMesh.isBuilding()) roundedMesh.end();

        RoundedUniforms.update(
                x, y,
                width, height,
                topLeft, topRight,
                bottomLeft, bottomRight,
                fillColor,
                outline,
                shadow
        );

        MeshRenderer.begin()
                .attachments(Minecraft.getInstance().gameRenderer.mainRenderTarget())
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
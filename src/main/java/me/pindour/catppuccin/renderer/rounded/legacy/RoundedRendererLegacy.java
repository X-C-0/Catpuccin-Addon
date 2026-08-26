package me.pindour.catppuccin.renderer.rounded.legacy;

//? if <=1.21.4 {
/*import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.pindour.catppuccin.api.render.style.Outline;
import me.pindour.catppuccin.api.render.style.Shadow;
import me.pindour.catppuccin.renderer.CatppuccinRenderer;
import me.pindour.catppuccin.renderer.rounded.RoundedRendererInternal;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.joml.Matrix4fStack;

import java.util.ArrayList;
import java.util.List;

public class RoundedRendererLegacy implements RoundedRendererInternal {
    private static final CatppuccinShader ROUNDED_SHADER = new CatppuccinShader("rounded_ui.vert", "rounded_ui.frag");
    private final CatppuccinMesh roundedMesh = new CatppuccinMesh();
    private final Pool<RoundedUniformsLegacy.RoundedCall> roundedCallPool = new Pool<>(RoundedUniformsLegacy.RoundedCall::new);
    private final List<RoundedUniformsLegacy.RoundedCall> roundedCalls = new ArrayList<>();

    @Override
    public void begin() {
        roundedCalls.clear();
    }

    @Override
    public void end() {
    }

    @Override
    public void render(double x, double y,
                       double width, double height,
                       float topLeft, float topRight,
                       float bottomLeft, float bottomRight,
                       Color fillColor,
                       Outline outline,
                       Shadow shadow) {

        RoundedUniformsLegacy.RoundedCall call = roundedCallPool.get();
        CatppuccinRenderer renderer = CatppuccinRenderer.get();

        call.set(
                (float) x,
                (float) y,
                (float) width,
                (float) height,
                (float) shadow.padX(),
                (float) shadow.padY(),
                topLeft,
                topRight,
                bottomLeft,
                bottomRight,
                fillColor,
                outline,
                shadow,
                renderer.isClipEnabled(),
                renderer.getClipMinX(),
                renderer.getClipMinY(),
                renderer.getClipMaxX(),
                renderer.getClipMaxY()
        );

        roundedCalls.add(call);
    }

    @Override
    public void render(PoseStack matrices) {
        if (roundedCalls.isEmpty()) return;

        GL.saveState();
        GL.disableDepth();
        GL.enableBlend();
        GL.disableCull();

        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        if (matrices != null) {
            modelView.mul(matrices.last().pose());
        }

        ROUNDED_SHADER.bind();
        ROUNDED_SHADER.set("u_Proj", RenderSystem.getProjectionMatrix());
        ROUNDED_SHADER.set("u_ModelView", modelView);

        for (RoundedUniformsLegacy.RoundedCall call : roundedCalls) {
            RoundedUniformsLegacy.update(ROUNDED_SHADER, call);
            roundedMesh.render(
                    call.x - call.padX,
                    call.y - call.padY,
                    call.width + call.padX * 2,
                    call.height + call.padY * 2
            );
            roundedCallPool.free(call);
        }

        roundedCalls.clear();
        modelView.popMatrix();
        GL.restoreState();
    }

    @Override
    public void flipFrame() { }
}
*///?}
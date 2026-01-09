package me.pindour.catppuccin.renderer.legacy;

//? if <=1.21.4 {
import com.mojang.blaze3d.systems.RenderSystem;
import me.pindour.catppuccin.renderer.CatppuccinRenderer;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4fStack;

import java.util.ArrayList;
import java.util.List;

public class RoundedRendererLegacy {
    private static final CatppuccinShader ROUNDED_SHADER = new CatppuccinShader("rounded_ui_legacy.vert", "rounded_ui_legacy.frag");
    private final CatppuccinMesh roundedMesh = new CatppuccinMesh();
    private final Pool<RoundedUniformsLegacy.RoundedCall> roundedCallPool = new Pool<>(RoundedUniformsLegacy.RoundedCall::new);
    private final List<RoundedUniformsLegacy.RoundedCall> roundedCalls = new ArrayList<>();

    private double alpha = 1.0;

    public void begin() {
        roundedCalls.clear();
    }

    public void end() {
    }

    public void setAlpha(double a) {
        this.alpha = a;
    }

    public void render(double x, double y, double width, double height, double radius, Color color,
                       boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        RoundedUniformsLegacy.RoundedCall call = roundedCallPool.get();
        CatppuccinRenderer renderer = CatppuccinRenderer.get();
        call.set(
                (float) x,
                (float) y,
                (float) width,
                (float) height,
                (float) radius,
                color,
                topLeft,
                topRight,
                bottomLeft,
                bottomRight,
                renderer.isClipEnabled(),
                renderer.getClipMinX(),
                renderer.getClipMinY(),
                renderer.getClipMaxX(),
                renderer.getClipMaxY()
        );
        roundedCalls.add(call);
    }

    public void render(MatrixStack matrices) {
        if (roundedCalls.isEmpty()) return;

        GL.saveState();
        GL.disableDepth();
        GL.enableBlend();
        GL.disableCull();

        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        if (matrices != null) {
            modelView.mul(matrices.peek().getPositionMatrix());
        }

        ROUNDED_SHADER.bind();
        ROUNDED_SHADER.set("u_Proj", RenderSystem.getProjectionMatrix());
        ROUNDED_SHADER.set("u_ModelView", modelView);

        for (RoundedUniformsLegacy.RoundedCall call : roundedCalls) {
            RoundedUniformsLegacy.update(ROUNDED_SHADER, call, alpha);
            roundedMesh.render(call.x, call.y, call.width, call.height);
            roundedCallPool.free(call);
        }

        roundedCalls.clear();
        modelView.popMatrix();
        GL.restoreState();
    }
}
//?}
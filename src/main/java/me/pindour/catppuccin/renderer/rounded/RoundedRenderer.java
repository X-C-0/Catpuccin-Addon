package me.pindour.catppuccin.renderer.rounded;

import me.pindour.catppuccin.renderer.CatppuccinRenderPipelines;
import meteordevelopment.meteorclient.utils.render.color.Color;

//? if <=1.21.4 {
/*import me.pindour.catppuccin.renderer.CatpuccinLegacyQuadMesh;
import me.pindour.catppuccin.renderer.CatpuccinShader;
*///?}

//? if <=1.21.4 {
/*import meteordevelopment.meteorclient.renderer.GL;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4fStack;
*///?}

//? if >=1.21.5 {
import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import net.minecraft.client.MinecraftClient;
//?}

public class RoundedRenderer {
    //? if >=1.21.5 {
    private final MeshBuilder roundedMesh;
    //?} else {
    /*private static final CatpuccinShader ROUNDED_SHADER = new CatpuccinShader("rounded_ui_legacy.vert", "rounded_ui_legacy.frag");
    private final CatpuccinLegacyQuadMesh roundedMesh = new CatpuccinLegacyQuadMesh();
    private final Pool<RoundedUniforms.RoundedCall> roundedCallPool = new Pool<>(RoundedUniforms.RoundedCall::new);
    private final List<RoundedUniforms.RoundedCall> roundedCalls = new ArrayList<>();
    *///?}

    private double alpha = 1.0;

    public RoundedRenderer() {
        //? if >=1.21.6 {
        this.roundedMesh = new MeshBuilder(CatppuccinRenderPipelines.ROUNDED_UI);
    }

    public void begin() {
        //? if >=1.21.5 {
        roundedMesh.begin();
        //?} else {
        /*roundedCalls.clear();
         *///?}
    }

    public void end() {
        //? if >=1.21.5 {
        if (roundedMesh.isBuilding()) roundedMesh.end();
        //?}
    }

    public void setAlpha(double a) {
        this.alpha = a;
    }

    public void render(double x, double y, double width, double height, double radius, Color color,
                       boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        double halfWidth = width * 0.5;
        double halfHeight = height * 0.5;

        //? if >=1.21.5 {
        submitRoundedMesh(x, y, width, height, halfWidth, halfHeight, radius, color, topLeft, topRight, bottomLeft, bottomRight);
        //?} else {
        /*RoundedUniforms.RoundedCall call = roundedCallPool.get();
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
        *///?}
    }

    //? if >=1.21.5 {
    private void submitRoundedMesh(double x, double y, double width, double height, double halfWidth, double halfHeight,
                                   double radius, Color color,
                                   boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        roundedMesh.ensureQuadCapacity();
        roundedMesh.quad(
                roundedMesh.vec2(x, y).vec2(-halfWidth, -halfHeight).color(Color.WHITE).next(),
                roundedMesh.vec2(x, y + height).vec2(-halfWidth, halfHeight).color(Color.WHITE).next(),
                roundedMesh.vec2(x + width, y + height).vec2(halfWidth, halfHeight).color(Color.WHITE).next(),
                roundedMesh.vec2(x + width, y).vec2(halfWidth, -halfHeight).color(Color.WHITE).next()
        );

        if (roundedMesh.isBuilding()) roundedMesh.end();

        //? if >=1.21.6 {
        RoundedUniforms.updateModern(width, height, radius, color, alpha, topLeft, topRight, bottomLeft, bottomRight);
        MeshRenderer.begin()
                .attachments(MinecraftClient.getInstance().getFramebuffer())
                .pipeline(CatppuccinRenderPipelines.ROUNDED_UI)
                .mesh(roundedMesh)
                .uniform("RoundedRectData", RoundedUniforms.getUniformStorage())
                .end();
        //?} else {
        /*renderLegacyMesh(width, height, radius, color, topLeft, topRight, bottomLeft, bottomRight);
         *///?}

        roundedMesh.begin();
    }
    //?}

    //? if <=1.21.4 {
    /*public void render(MatrixStack matrices) {
        if (roundedCalls.isEmpty()) return;

        GL.saveState();
        GL.disableDepth();
        GL.enableBlend();
        GL.disableCull();

        Matrix4fStack modelView = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        if (matrices != null) {
            modelView.mul(matrices.peek().getPositionMatrix());
        }
        //? if <=1.21.1
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();

        ROUNDED_SHADER.bind();
        ROUNDED_SHADER.set("u_Proj", com.mojang.blaze3d.systems.RenderSystem.getProjectionMatrix());
        ROUNDED_SHADER.set("u_ModelView", modelView);

        for (RoundedUniforms.RoundedCall call : roundedCalls) {
            RoundedUniforms.updateLegacyOld(ROUNDED_SHADER, call, alpha);
            roundedMesh.render(call.x, call.y, call.width, call.height);
            roundedCallPool.free(call);
        }

        roundedCalls.clear();
        modelView.popMatrix();
        //? if <=1.21.1
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();
        GL.restoreState();
    }
    *///?}
}
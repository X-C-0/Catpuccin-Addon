package me.pindour.catpuccin.gui.renderer;

import me.pindour.catpuccin.gui.text.FontStyle;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.text.RichTextOperation;
import me.pindour.catpuccin.gui.text.RichTextSegment;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderOperation;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

#if MC_VER < MC_1_21_5
import me.pindour.catpuccin.renderer.CatpuccinLegacyQuadMesh;
import me.pindour.catpuccin.renderer.CatpuccinShader;
#endif

#if MC_VER <= MC_1_21_4
import meteordevelopment.meteorclient.renderer.GL;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4fStack;
#endif

#if MC_VER >= MC_1_21_5
import me.pindour.catpuccin.renderer.CatpuccinRenderPipelines;
import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.renderer.Texture;
import net.minecraft.client.MinecraftClient;
#endif

#if MC_VER >= MC_1_21_6
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.gl.DynamicUniformStorage;
import org.joml.Vector2f;
import org.joml.Vector4f;
import java.nio.ByteBuffer;
#endif

public class CatpuccinRenderer {
    private static final CatpuccinRenderer INSTANCE = new CatpuccinRenderer();
    private CatpuccinGuiTheme theme;

    #if MC_VER >= MC_1_21_5
    private static Texture TEXTURE;
    #endif

    private final Renderer2D r = new Renderer2D(false);
    private final Renderer2D rTex = new Renderer2D(true);
#if MC_VER == MC_1_21_5
    private void renderRoundedLegacyMesh(double width, double height, double radius, Color color,
                                         boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        float r = (float) radius;
        float tl = topLeft ? r : 0f;
        float tr = topRight ? r : 0f;
        float br = bottomRight ? r : 0f;
        float bl = bottomLeft ? r : 0f;

        float a = (float) (color.a / 255f * alpha);
        float fillR = color.r / 255f;
        float fillG = color.g / 255f;
        float fillB = color.b / 255f;

        float halfW = (float) (width * 0.5);
        float halfH = (float) (height * 0.5);

        float[] clip = clipRect();

        MeshRenderer.begin()
            .attachments(MinecraftClient.getInstance().getFramebuffer())
            .pipeline(CatpuccinRenderPipelines.ROUNDED_UI_LEGACY)
            .mesh(roundedMesh)
            .setupCallback(pass -> {
                pass.setUniform("u_FillColor", fillR, fillG, fillB, a);
                pass.setUniform("u_BorderColor", 0f, 0f, 0f, 0f);
                pass.setUniform("u_Radii0", tl, tr);
                pass.setUniform("u_Radii1", br, bl);
                pass.setUniform("u_BorderData", 0f, 1f);
                pass.setUniform("u_HalfSize", halfW, halfH);
                pass.setUniform("u_Padding", 0f, 0f);
                pass.setUniform("u_ClipMin", clip[0], clip[1]);
                pass.setUniform("u_ClipMax", clip[2], clip[3]);
            })
            .end();
    }
    #endif



    #if MC_VER >= MC_1_21_6
    private static final int ROUNDED_DATA_SIZE = new Std140SizeCalculator()
        .putVec4()
        .putVec4()
        .putVec4()
        .putVec4()
        .putVec2()
        .putVec2()
        .putVec4()
        .get();
    private static final RoundedRectData ROUNDED_DATA = new RoundedRectData();
    private static final DynamicUniformStorage<RoundedRectData> ROUNDED_STORAGE = new DynamicUniformStorage<>("Catpuccin Rounded UBO", ROUNDED_DATA_SIZE, 16);
    #endif

    #if MC_VER >= MC_1_21_6
    private final MeshBuilder roundedMesh = new MeshBuilder(CatpuccinRenderPipelines.ROUNDED_UI);
    #elif MC_VER == MC_1_21_5
    private final MeshBuilder roundedMesh = new MeshBuilder(CatpuccinRenderPipelines.ROUNDED_UI_LEGACY);
    private final Color tmpFillColor = new Color();
    private final Color tmpBorderColor = new Color();
    #else
    private static final CatpuccinShader ROUNDED_SHADER = new CatpuccinShader("rounded_ui_legacy.vert", "rounded_ui_legacy.frag");
    private final CatpuccinLegacyQuadMesh roundedMesh = new CatpuccinLegacyQuadMesh();
    private final Pool<RoundedCall> roundedCallPool = new Pool<>(RoundedCall::new);
    private final List<RoundedCall> roundedCalls = new ArrayList<>();
    private final Color tmpFillColor = new Color();
    private final Color tmpBorderColor = new Color();
    #endif

    private double alpha = 1.0;
    private boolean clipEnabled = false;
    private float clipMinX;
    private float clipMinY;
    private float clipMaxX;
    private float clipMaxY;

    private final Pool<RichTextOperation> textPool = new Pool<>(RichTextOperation::new);
    private final Map<StyleKey, List<RichTextOperation>> groupedOperations = new HashMap<>();
    #if MC_VER >= MC_1_21_5
    private final float[] clipRect = new float[4];
    #endif

    #if MC_VER >= MC_1_21_5
    public static void init(Texture texture) {
        TEXTURE = texture;
    }
    #endif

    public static CatpuccinRenderer get() {
        return INSTANCE;
    }

    public void setTheme(CatpuccinGuiTheme theme) {
        if (this.theme == null) this.theme = theme;
    }

    public void begin() {
        r.begin();
        rTex.begin();
        beginRoundedBatch();
    }

    public void end() {
        r.end();
        rTex.end();
        endRoundedBatch();
    }

    #if MC_VER <= MC_1_21_4
    public void render(MatrixStack matrices) {
        renderRounded(matrices);
        r.render(matrices);
    }

    public void renderTexture(MatrixStack matrices) {
        rTex.render(matrices);
    }

    #else
    public void render() {
        r.render();
        renderTextureInternal();
    }
    #endif

    public void renderText() {
        if (theme == null) return;

        // Render each style group in batches to minimize font/scale changes
        for (Map.Entry<StyleKey, List<RichTextOperation>> entry : groupedOperations.entrySet()) {
            List<RichTextOperation> textOps = entry.getValue();

            if (textOps.isEmpty()) continue;

            StyleKey key = entry.getKey();

            theme.richTextRenderer().setFontStyle(key.style());
            theme.richTextRenderer().begin(theme.scale(key.scale()));

            for (RichTextOperation text : textOps) {
                text.run(textPool);
            }

            theme.richTextRenderer().end();
            textOps.clear();
        }
    }

    public void setAlpha(double a) {
        alpha = a;
        r.setAlpha(a);
        rTex.setAlpha(a);
    }

    public void setClipRect(double minX, double minY, double maxX, double maxY) {
        clipEnabled = true;
        clipMinX = (float) minX;
        clipMinY = (float) minY;
        clipMaxX = (float) maxX;
        clipMaxY = (float) maxY;
    }

    public void clearClipRect() {
        clipEnabled = false;
        clipMinX = 0f;
        clipMinY = 0f;
        clipMaxX = 0f;
        clipMaxY = 0f;
    }

    public void text(RichText text, double x, double y, Color color) {
        double segmentX = x;

        for (RichTextSegment segment : text.getSegments()) {
            if (segment.getText() == null || segment.getText().isEmpty()) continue;

            RichTextOperation operation = getOperation(textPool, segmentX, y, color)
                    .set(segment, theme.richTextRenderer());

            // Group operations by style and scale to batch render them later
            StyleKey key = new StyleKey(operation.getStyle(), operation.getScale());

            groupedOperations
                    .computeIfAbsent(key, k -> new ArrayList<>())
                    .add(operation);

            segmentX += theme.textWidth(segment);
        }
    }

    public void roundedRect(WWidget widget, double radius, Color color, CornerStyle style) {
        roundedRect(widget.x, widget.y, widget.width, widget.height, radius, color, style);
    }

    public void roundedRect(double x, double y, double width, double height, double radius, Color color, CornerStyle style) {
        roundedRect(x, y, width, height, radius, color, style.topLeft, style.topRight, style.bottomLeft, style.bottomRight);
    }

    /**
     * Renders a rounded rectangle using a shader SDF with selective corner rounding.
     *
     * @param x           Starting X coordinate
     * @param y           Starting Y coordinate
     * @param width       Rectangle width
     * @param height      Rectangle height
     * @param radius      Corner radius
     * @param color       Fill color
     * @param topLeft     Round top left corner
     * @param topRight    Round top right corner
     * @param bottomLeft  Round bottom left corner
     * @param bottomRight Round bottom right corner
     */
    public void roundedRect(double x, double y, double width, double height, double radius, Color color, boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        if (width <= 0 || height <= 0) return;
        if (radius <= 0 || theme == null || !theme.roundedCorners.get()) {
            r.quad(x, y, width, height, color);
            return;
        }

        double halfWidth = width * 0.5;
        double halfHeight = height * 0.5;

        #if MC_VER >= MC_1_21_5
        submitRoundedMesh(x, y, width, height, halfWidth, halfHeight, radius, color, topLeft, topRight, bottomLeft, bottomRight);
        #else
        RoundedCall call = roundedCallPool.get();
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
            clipEnabled,
            clipMinX,
            clipMinY,
            clipMaxX,
            clipMaxY
        );
        roundedCalls.add(call);
        #endif
    }

    #if MC_VER >= MC_1_21_5
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

        #if MC_VER >= MC_1_21_6
        updateRoundedUniforms(width, height, radius, color, topLeft, topRight, bottomLeft, bottomRight);
        MeshRenderer.begin()
            .attachments(MinecraftClient.getInstance().getFramebuffer())
            .pipeline(CatpuccinRenderPipelines.ROUNDED_UI)
            .mesh(roundedMesh)
            .uniform("RoundedRectData", ROUNDED_STORAGE.write(ROUNDED_DATA))
            .end();
        #else
        renderRoundedLegacyMesh(width, height, radius, color, topLeft, topRight, bottomLeft, bottomRight);
        #endif

        roundedMesh.begin();
    }
    #endif

    private void beginRoundedBatch() {
        #if MC_VER >= MC_1_21_5
        roundedMesh.begin();
        #else
        roundedCalls.clear();
        #endif
    }

    private void endRoundedBatch() {
        #if MC_VER >= MC_1_21_5
        if (roundedMesh.isBuilding()) roundedMesh.end();
        #endif
    }

    #if MC_VER >= MC_1_21_5
    private void renderTextureInternal() {
        #if MC_VER >= MC_1_21_11
        rTex.render(TEXTURE.getGlTextureView(), TEXTURE.getSampler());
        #elif MC_VER >= MC_1_21_6
        rTex.render(TEXTURE.getGlTextureView());
        #else
        rTex.render(TEXTURE.getGlTexture());
        #endif
    }
    #endif

    private <T extends GuiRenderOperation<T>> T getOperation(Pool<T> pool, double x, double y, Color color) {
        T op = pool.get();
        op.set(x, y, color);
        return op;
    }

    #if MC_VER >= MC_1_21_6
    public static void flipFrame() {
        ROUNDED_STORAGE.clear();
    }
    #else
    public static void flipFrame() {
    }
    #endif

    #if MC_VER <= MC_1_21_4
    private void renderRounded(MatrixStack matrices) {
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
        #if MC_VER <= MC_1_21_1
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();
        #endif

        ROUNDED_SHADER.bind();
        ROUNDED_SHADER.set("u_Proj", com.mojang.blaze3d.systems.RenderSystem.getProjectionMatrix());
        ROUNDED_SHADER.set("u_ModelView", modelView);

        for (RoundedCall call : roundedCalls) {
            updateRoundedUniformsLegacy(call);
            roundedMesh.render(call.x, call.y, call.width, call.height);
            roundedCallPool.free(call);
        }

        roundedCalls.clear();
        modelView.popMatrix();
        #if MC_VER <= MC_1_21_1
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();
        #endif
        GL.restoreState();
    }
    #endif


    #if MC_VER >= MC_1_21_6
    private void updateRoundedUniforms(double width, double height, double radius, Color color,
                                       boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        float r = (float) radius;
        float tl = topLeft ? r : 0f;
        float tr = topRight ? r : 0f;
        float br = bottomRight ? r : 0f;
        float bl = bottomLeft ? r : 0f;

        float a = (float) (color.a / 255f * alpha);
        ROUNDED_DATA.fillColor.set(color.r / 255f, color.g / 255f, color.b / 255f, a);
        ROUNDED_DATA.borderColor.set(0f, 0f, 0f, 0f);
        ROUNDED_DATA.radii.set(tl, tr, br, bl);
        ROUNDED_DATA.borderData.set(0f, 1f, 0f, 0f);
        ROUNDED_DATA.halfSize.set((float) (width * 0.5), (float) (height * 0.5));
        ROUNDED_DATA.padding.set(0f, 0f);
        applyClipRect(ROUNDED_DATA.clipRect);
    }

    private static final class RoundedRectData implements DynamicUniformStorage.Uploadable {
        private final Vector4f fillColor = new Vector4f();
        private final Vector4f borderColor = new Vector4f();
        private final Vector4f radii = new Vector4f();
        private final Vector4f borderData = new Vector4f();
        private final Vector2f halfSize = new Vector2f();
        private final Vector2f padding = new Vector2f();
        private final Vector4f clipRect = new Vector4f();

        @Override
        public void write(ByteBuffer buffer) {
            Std140Builder.intoBuffer(buffer)
                .putVec4(fillColor)
                .putVec4(borderColor)
                .putVec4(radii)
                .putVec4(borderData)
                .putVec2(halfSize)
                .putVec2(padding)
                .putVec4(clipRect);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }
    #elif MC_VER < MC_1_21_5
    private void updateRoundedUniformsLegacy(RoundedCall call) {
        float r = call.radius;
        float tl = call.topLeft ? r : 0f;
        float tr = call.topRight ? r : 0f;
        float br = call.bottomRight ? r : 0f;
        float bl = call.bottomLeft ? r : 0f;

        int alphaValue = Math.round(call.color.a * (float) alpha);
        tmpFillColor.set(call.color).a(alphaValue);
        tmpBorderColor.set(0, 0, 0, 0);

        ROUNDED_SHADER.set("u_FillColor", tmpFillColor);
        ROUNDED_SHADER.set("u_BorderColor", tmpBorderColor);
        ROUNDED_SHADER.set("u_Radii0", tl, tr);
        ROUNDED_SHADER.set("u_Radii1", br, bl);
        ROUNDED_SHADER.set("u_BorderData", 0.0, 1.0);
        ROUNDED_SHADER.set("u_HalfSize", call.width * 0.5f, call.height * 0.5f);
        ROUNDED_SHADER.set("u_Padding", 0.0, 0.0);
        if (call.clipEnabled) {
            ROUNDED_SHADER.set("u_ClipMin", call.clipMinX, call.clipMinY);
            ROUNDED_SHADER.set("u_ClipMax", call.clipMaxX, call.clipMaxY);
        } else {
            ROUNDED_SHADER.set("u_ClipMin", 0.0, 0.0);
            ROUNDED_SHADER.set("u_ClipMax", -1.0, -1.0);
        }
    }

    private static final class RoundedCall {
        private float x;
        private float y;
        private float width;
        private float height;
        private float radius;
        private Color color;
        private boolean topLeft;
        private boolean topRight;
        private boolean bottomLeft;
        private boolean bottomRight;
        private boolean clipEnabled;
        private float clipMinX;
        private float clipMinY;
        private float clipMaxX;
        private float clipMaxY;

        private void set(float x, float y, float width, float height, float radius, Color color,
                         boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight,
                         boolean clipEnabled, float clipMinX, float clipMinY, float clipMaxX, float clipMaxY) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.radius = radius;
            this.color = color;
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomLeft = bottomLeft;
            this.bottomRight = bottomRight;
            this.clipEnabled = clipEnabled;
            this.clipMinX = clipMinX;
            this.clipMinY = clipMinY;
            this.clipMaxX = clipMaxX;
            this.clipMaxY = clipMaxY;
        }
    }

#endif

    #if MC_VER >= MC_1_21_5
    private float[] clipRect() {
        if (clipEnabled) {
            clipRect[0] = clipMinX;
            clipRect[1] = clipMinY;
            clipRect[2] = clipMaxX;
            clipRect[3] = clipMaxY;
        } else {
            clipRect[0] = 0f;
            clipRect[1] = 0f;
            clipRect[2] = -1f;
            clipRect[3] = -1f;
        }
        return clipRect;
    }
    #endif

    #if MC_VER >= MC_1_21_6
    private void applyClipRect(Vector4f target) {
        if (clipEnabled) {
            target.set(clipMinX, clipMinY, clipMaxX, clipMaxY);
        } else {
            target.set(0f, 0f, -1f, -1f);
        }
    }
    #endif

    public record StyleKey(FontStyle style, double scale) { }
}

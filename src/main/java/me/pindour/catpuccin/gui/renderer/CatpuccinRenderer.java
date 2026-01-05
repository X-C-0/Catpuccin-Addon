package me.pindour.catpuccin.gui.renderer;

import me.pindour.catpuccin.gui.text.FontStyle;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.text.RichTextOperation;
import me.pindour.catpuccin.gui.text.RichTextSegment;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderOperation;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;
import me.pindour.catpuccin.renderer.CatpuccinRenderPipelines;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.client.MinecraftClient;
import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatpuccinRenderer {
    private static final CatpuccinRenderer INSTANCE = new CatpuccinRenderer();
    private CatpuccinGuiTheme theme;

    private static Texture TEXTURE;

    private final Renderer2D r = new Renderer2D(false);
    private final Renderer2D rTex = new Renderer2D(true);
    private final MeshBuilder roundedMesh = new MeshBuilder(CatpuccinRenderPipelines.ROUNDED_UI);

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

    private double alpha = 1.0;
    private boolean clipEnabled = false;
    private float clipMinX;
    private float clipMinY;
    private float clipMaxX;
    private float clipMaxY;

    private final Pool<RichTextOperation> textPool = new Pool<>(RichTextOperation::new);
    private final Map<StyleKey, List<RichTextOperation>> groupedOperations = new HashMap<>();

    public static void init(Texture texture) {
        TEXTURE = texture;
    }

    public static CatpuccinRenderer get() {
        return INSTANCE;
    }

    public void setTheme(CatpuccinGuiTheme theme) {
        if (this.theme == null) this.theme = theme;
    }

    public void begin() {
        r.begin();
        rTex.begin();
        roundedMesh.begin();
    }

    public void end() {
        r.end();
        rTex.end();
        if (roundedMesh.isBuilding()) roundedMesh.end();
    }

    public void render() {
        r.render();
        rTex.render("u_Texture", TEXTURE.getGlTextureView(), TEXTURE.getSampler());
    }

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

        roundedMesh.ensureQuadCapacity();
        roundedMesh.quad(
            roundedMesh.vec2(x, y).vec2(-halfWidth, -halfHeight).color(Color.WHITE).next(),
            roundedMesh.vec2(x, y + height).vec2(-halfWidth, halfHeight).color(Color.WHITE).next(),
            roundedMesh.vec2(x + width, y + height).vec2(halfWidth, halfHeight).color(Color.WHITE).next(),
            roundedMesh.vec2(x + width, y).vec2(halfWidth, -halfHeight).color(Color.WHITE).next()
        );

        updateRoundedUniforms(width, height, radius, color, topLeft, topRight, bottomLeft, bottomRight);

        if (roundedMesh.isBuilding()) roundedMesh.end();

        MeshRenderer.begin()
            .attachments(MinecraftClient.getInstance().getFramebuffer())
            .pipeline(CatpuccinRenderPipelines.ROUNDED_UI)
            .mesh(roundedMesh)
            .uniform("RoundedRectData", ROUNDED_STORAGE.write(ROUNDED_DATA))
            .end();

        roundedMesh.begin();
    }

    private <T extends GuiRenderOperation<T>> T getOperation(Pool<T> pool, double x, double y, Color color) {
        T op = pool.get();
        op.set(x, y, color);
        return op;
    }

    public record StyleKey(FontStyle style, double scale) { }

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
        if (clipEnabled) {
            ROUNDED_DATA.clipRect.set(clipMinX, clipMinY, clipMaxX, clipMaxY);
        } else {
            ROUNDED_DATA.clipRect.set(0f, 0f, -1f, -1f);
        }
    }

    private static final class RoundedRectData implements DynamicUniformStorage.Uploadable {
        private final org.joml.Vector4f fillColor = new org.joml.Vector4f();
        private final org.joml.Vector4f borderColor = new org.joml.Vector4f();
        private final org.joml.Vector4f radii = new org.joml.Vector4f();
        private final org.joml.Vector4f borderData = new org.joml.Vector4f();
        private final org.joml.Vector2f halfSize = new org.joml.Vector2f();
        private final org.joml.Vector2f padding = new org.joml.Vector2f();
        private final org.joml.Vector4f clipRect = new org.joml.Vector4f();

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
}

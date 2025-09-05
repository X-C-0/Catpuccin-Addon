package me.pindour.catpuccin.gui.renderer;

import me.pindour.catpuccin.gui.text.FontStyle;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.text.RichTextOperation;
import me.pindour.catpuccin.gui.text.RichTextSegment;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderOperation;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.TextureRegion;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.ByteTexture;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatpuccinRenderer {
    private static final CatpuccinRenderer INSTANCE = new CatpuccinRenderer();
    private CatpuccinGuiTheme theme;

    private static ByteTexture TEXTURE;
    private static TextureRegion CIRCLE_TEXTURE;

    private final Renderer2D r = new Renderer2D(false);
    private final Renderer2D rTex = new Renderer2D(true);

    private final Pool<RichTextOperation> textPool = new Pool<>(RichTextOperation::new);
    private final Map<StyleKey, List<RichTextOperation>> groupedOperations = new HashMap<>();

    public static void init(ByteTexture texture) {
        TEXTURE = texture;
    }

    public static CatpuccinRenderer get() {
        return (INSTANCE != null ? INSTANCE : new CatpuccinRenderer());
    }

    public void setTheme(CatpuccinGuiTheme theme) {
        if (this.theme == null) this.theme = theme;
    }

    public void begin() {
        r.begin();
        rTex.begin();
    }

    public void end() {
        r.end();
        rTex.end();
    }

    public void render(MatrixStack matrices) {
        r.render(matrices);
        // Bind the texture before rendering - ByteTexture extends AbstractTexture which has bind()
        GL.bindTexture(TEXTURE.getGlId());
        rTex.render(matrices);
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
        r.setAlpha(a);
        rTex.setAlpha(a);
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
     * Renders a rounded rectangle using quads and circles with selective corner rounding.
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
        if (radius <= 0 || !theme.roundedCorners.get()) {
            r.quad(x, y, width, height, color);
            return;
        }

        if (CIRCLE_TEXTURE == null) CIRCLE_TEXTURE = GuiRenderer.CIRCLE.get(64, 64);

        double maxRadius = Math.min(width, height) / 2;
        radius = Math.min(radius, maxRadius);

        double topQuadX = x + radius;
        double topQuadWidth = width - 2 * radius;
        double bottomQuadX = x + radius;
        double bottomQuadWidth = width - 2 * radius;

        if (topLeft) texPartialCircle(x, y, radius, radius, 0.0, 0.0, 0.5, 0.5, color);
        else {
            topQuadX -= radius;
            topQuadWidth += radius;
        }

        if (topRight) texPartialCircle(x + width - radius, y, radius, radius, 0.5, 0.0, 1.0, 0.5, color);
        else topQuadWidth += radius;

        if (bottomLeft) texPartialCircle(x, y + height - radius, radius, radius, 0.0, 0.5, 0.5, 1.0, color);
        else {
            bottomQuadX -= radius;
            bottomQuadWidth += radius;
        }

        if (bottomRight) texPartialCircle(x + width - radius, y + height - radius, radius, radius, 0.5, 0.5, 1.0, 1.0, color);
        else bottomQuadWidth += radius;

        // Top quad
        r.quad(topQuadX, y, topQuadWidth, radius, color);

        // Center quad
        r.quad(x, y + radius, width, height - 2 * radius, color);

        // Bottom quad
        r.quad(bottomQuadX, y + height - radius, bottomQuadWidth, radius, color);
    }

    public void texPartialCircle(double x, double y, double width, double height,
                                 double u1, double v1, double u2, double v2,
                                 Color color) {

        double texU1 = MathHelper.lerp(u1, CIRCLE_TEXTURE.x1, CIRCLE_TEXTURE.x2);
        double texV1 = MathHelper.lerp(v1, CIRCLE_TEXTURE.y1, CIRCLE_TEXTURE.y2);
        double texU2 = MathHelper.lerp(u2, CIRCLE_TEXTURE.x1, CIRCLE_TEXTURE.x2);
        double texV2 = MathHelper.lerp(v2, CIRCLE_TEXTURE.y1, CIRCLE_TEXTURE.y2);

        rTex.texQuad(x, y, width, height, 0, texU1, texV1, texU2, texV2, color);
    }

    private <T extends GuiRenderOperation<T>> T getOperation(Pool<T> pool, double x, double y, Color color) {
        T op = pool.get();
        op.set(x, y, color);
        return op;
    }

    public record StyleKey(FontStyle style, double scale) { }
}
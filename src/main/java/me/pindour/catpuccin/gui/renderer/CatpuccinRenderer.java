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
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.ByteTexture; // CHANGED IMPORT
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatpuccinRenderer {
    private static CatpuccinRenderer INSTANCE;
    private static CatpuccinGuiTheme theme;

    private static ByteTexture TEXTURE; // CHANGED TYPE
    private static TextureRegion CIRCLE_TEXTURE;

    private final Renderer2D r = new Renderer2D(false);
    private final Renderer2D rTex = new Renderer2D(true);

    private final Pool<RichTextOperation> textPool = new Pool<>(RichTextOperation::new);
    private final List<RichTextOperation> texts = new ArrayList<>();

    private final Map<Pair<FontStyle, Double>, List<RichTextOperation>> textsByStyleAndScale = new HashMap<>();

    // Store MatrixStack for rendering
    private MatrixStack matrices;

    public CatpuccinRenderer() {
        INSTANCE = this;
    }

    public static void init(ByteTexture texture) {
        TEXTURE = texture;
    }

    public static CatpuccinRenderer get() {
        return (INSTANCE != null ? INSTANCE : new CatpuccinRenderer());
    }

    public void setTheme(CatpuccinGuiTheme theme) {
        if (CatpuccinRenderer.theme != theme) CatpuccinRenderer.theme = theme;
    }

    public void begin() {
        r.begin();
        rTex.begin();
    }

    public void begin(MatrixStack matrices) {
        this.matrices = matrices;
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
        TEXTURE.bindTexture();
        rTex.render(matrices);
    }

    public void render() {
        if (matrices != null) {
            render(matrices);
        } else {
            // Fallback: create a new MatrixStack if none was provided
            render(new MatrixStack());
        }
    }

    public void renderText() {
        if (theme == null) return;

        textsByStyleAndScale.clear();

        for (RichTextOperation text : texts) {
            FontStyle style = text.getStyle();
            double scale = text.getScale();
            Pair<FontStyle, Double> key = new Pair<>(style, scale);
            textsByStyleAndScale.computeIfAbsent(key, k -> new ArrayList<>()).add(text);
        }

        for (Map.Entry<Pair<FontStyle, Double>, List<RichTextOperation>> entry : textsByStyleAndScale.entrySet()) {
            FontStyle style = entry.getKey().getLeft();
            double scale = entry.getKey().getRight();

            theme.richTextRenderer().setFontStyle(style);
            theme.richTextRenderer().begin(theme.scale(scale));

            for (RichTextOperation text : entry.getValue()) {
                text.run(textPool);
            }

            theme.richTextRenderer().end();

            entry.getValue().clear();
        }

        texts.clear();
    }

    public void setAlpha(double a) {
        r.setAlpha(a);
        rTex.setAlpha(a);
    }

    public void text(RichText text, double x, double y, Color color) {
        double segmentX = x;

        for (RichTextSegment segment : text.getSegments()) {
            if (segment.getText() == null || segment.getText().isEmpty()) continue;

            double segmentWidth = theme.textWidth(segment);

            texts.add(getOperation(textPool, segmentX, y, color).set(segment, theme.richTextRenderer()));

            segmentX += segmentWidth;
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
        if (radius <= 0) {
            r.quad(x, y, width, height, color);
            return;
        }

        if (CIRCLE_TEXTURE == null) CIRCLE_TEXTURE = GuiRenderer.CIRCLE.get(64, 64);

        double maxRadius = Math.min(width, height) / 2;
        radius = Math.min(radius, maxRadius);

        if (topLeft) texPartialCircle(x, y, radius, radius, 0.0, 0.0, 0.5, 0.5, color);
        else r.quad(x, y, radius, radius, color);

        if (topRight) texPartialCircle(x + width - radius, y, radius, radius, 0.5, 0.0, 1.0, 0.5, color);
        else r.quad(x + width - radius, y, radius, radius, color);

        if (bottomRight) texPartialCircle(x + width - radius, y + height - radius, radius, radius, 0.5, 0.5, 1.0, 1.0, color);
        else r.quad(x + width - radius, y + height - radius, radius, radius, color);

        if (bottomLeft) texPartialCircle(x, y + height - radius, radius, radius, 0.0, 0.5, 0.5, 1.0, color);
        else r.quad(x, y + height - radius, radius, radius, color);

        r.quad(x + radius, y + radius, width - 2 * radius, height - 2 * radius, color);
        r.quad(x + radius, y, width - 2 * radius, radius, color);
        r.quad(x + radius, y + height - radius, width - 2 * radius, radius, color);
        r.quad(x, y + radius, radius, height - 2 * radius, color);
        r.quad(x + width - radius, y + radius, radius, height - 2 * radius, color);
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
}
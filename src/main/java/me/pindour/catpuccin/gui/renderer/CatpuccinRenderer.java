package me.pindour.catpuccin.gui.renderer;

import me.pindour.catpuccin.gui.text.FontStyle;
import me.pindour.catpuccin.gui.text.RichText;
import me.pindour.catpuccin.gui.text.RichTextOperation;
import me.pindour.catpuccin.gui.text.RichTextSegment;
import me.pindour.catpuccin.gui.themes.catpuccin.CatpuccinGuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderOperation;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.renderer.packer.TextureRegion;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatpuccinRenderer {
    private static CatpuccinRenderer INSTANCE;
    private static CatpuccinGuiTheme theme;
    private static Texture TEXTURE;

    private final Renderer2D r = new Renderer2D(false);
    private final Renderer2D rTex = new Renderer2D(true);

    private final Pool<RichTextOperation> textPool = new Pool<>(RichTextOperation::new);
    private final List<RichTextOperation> texts = new ArrayList<>();

    private final Map<Pair<FontStyle, Double>, List<RichTextOperation>> textsByStyleAndScale = new HashMap<>();

    public CatpuccinRenderer() {
        INSTANCE = this;
    }

    public static void init(Texture texture) {
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

    public void end() {
        r.end();
        rTex.end();
    }

    public void render() {
        r.render();
        rTex.render(pass -> pass.bindSampler("u_Texture", TEXTURE.getGlTexture()));
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

        double maxRadius = Math.min(width, height) / 2;
        radius = Math.min(radius, maxRadius);

        GuiTexture circleTexture = Textures.CIRCLE.texture();

        if (topLeft) texPartialQuad(x, y, radius, radius, 0.0, 0.0, 0.5, 0.5, circleTexture, color);
        else r.quad(x, y, radius, radius, color);

        if (topRight) texPartialQuad(x + width - radius, y, radius, radius, 0.5, 0.0, 1.0, 0.5, circleTexture, color);
        else r.quad(x + width - radius, y, radius, radius, color);

        if (bottomRight) texPartialQuad(x + width - radius, y + height - radius, radius, radius, 0.5, 0.5, 1.0, 1.0, circleTexture, color);
        else r.quad(x + width - radius, y + height - radius, radius, radius, color);

        if (bottomLeft) texPartialQuad(x, y + height - radius, radius, radius, 0.0, 0.5, 0.5, 1.0, circleTexture, color);
        else r.quad(x, y + height - radius, radius, radius, color);

        r.quad(x + radius, y + radius, width - 2 * radius, height - 2 * radius, color);
        r.quad(x + radius, y, width - 2 * radius, radius, color);
        r.quad(x + radius, y + height - radius, width - 2 * radius, radius, color);
        r.quad(x, y + radius, radius, height - 2 * radius, color);
        r.quad(x + width - radius, y + radius, radius, height - 2 * radius, color);
    }

    private void texPartialQuad(double x, double y, double width, double height, double u1, double v1, double u2, double v2, GuiTexture texture, Color color) {
        int CIRCLE_RESOLUTION = 256;

        TextureRegion base = texture.get(CIRCLE_RESOLUTION, CIRCLE_RESOLUTION);
        TextureRegion partialRegion = new TextureRegion(CIRCLE_RESOLUTION, CIRCLE_RESOLUTION);

        partialRegion.x1 = MathHelper.lerp(u1, base.x1, base.x2);
        partialRegion.y1 = MathHelper.lerp(v1, base.y1, base.y2);
        partialRegion.x2 = MathHelper.lerp(u2, base.x1, base.x2);
        partialRegion.y2 = MathHelper.lerp(v2, base.y1, base.y2);

        rTex.texQuad(x, y, width, height, partialRegion, color);
    }

    private <T extends GuiRenderOperation<T>> T getOperation(Pool<T> pool, double x, double y, Color color) {
        T op = pool.get();
        op.set(x, y, color);
        return op;
    }
}

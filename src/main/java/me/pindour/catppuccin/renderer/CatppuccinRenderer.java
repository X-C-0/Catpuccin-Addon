package me.pindour.catppuccin.renderer;

import me.pindour.catppuccin.api.render.RoundedRect;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.renderer.rounded.RoundedRenderer;
import me.pindour.catppuccin.renderer.text.CatppuccinTextRenderer;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;

//? if >=1.21.5 {
import me.pindour.catppuccin.renderer.rounded.modern.RoundedRendererModern;
//?} else {
/*import me.pindour.catppuccin.renderer.rounded.legacy.RoundedRendererLegacy;
import net.minecraft.client.util.math.MatrixStack;
*///?}

public class CatppuccinRenderer {
    private static final CatppuccinRenderer INSTANCE = new CatppuccinRenderer();

    private CatppuccinGuiTheme theme;

    //? if >=1.21.5
    private final RoundedRenderer roundedRenderer = new RoundedRendererModern();
    //? if <=1.21.4
    //private final RoundedRenderer roundedRenderer = new RoundedRendererLegacy();

    private final CatppuccinTextRenderer textRenderer = new CatppuccinTextRenderer();
    private final Renderer2D r = new Renderer2D(false);

    private boolean clipEnabled = false;
    private float clipMinX;
    private float clipMinY;
    private float clipMaxX;
    private float clipMaxY;

    public static CatppuccinRenderer get() {
        return INSTANCE;
    }

    public void setTheme(CatppuccinGuiTheme theme) {
        if (this.theme == null) this.theme = theme;
    }

    public void begin() {
        roundedRenderer.begin();
        r.begin();
    }

    public void end() {
        roundedRenderer.end();
        r.end();
    }

    //? if <=1.21.4 {
    /*public void render(MatrixStack matrices) {
        roundedRenderer.render(matrices);
        r.render(matrices);
    }
    *///?} else {
    public void render() {
        r.render();
    }
    //?}

    public void renderText() {
        if (theme == null) return;
        textRenderer.render(theme);
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

    public boolean isClipEnabled() {
        return clipEnabled;
    }

    public float getClipMinX() { return clipMinX; }
    public float getClipMinY() { return clipMinY; }
    public float getClipMaxX() { return clipMaxX; }
    public float getClipMaxY() { return clipMaxY; }

    public void text(RichText text, double x, double y, Color color) {
        textRenderer.text(text, x, y, color, theme);
    }

    /**
     * Low-level render method that renders a rounded rectangle using a shader SDF with selective corner rounding.
     * This method should not be called directly; use {@link RoundedRect} instead.
     *
     * @param x            The X coordinate of the rectangle.
     * @param y            The Y coordinate of the rectangle.
     * @param width        The width of the rectangle.
     * @param height       The height of the rectangle.
     * @param rTopLeft     Top-left corner radius in pixels.
     * @param rTopRight    Top-right corner radius in pixels.
     * @param rBottomLeft  Bottom-left corner radius in pixels.
     * @param rBottomRight Bottom-right corner radius in pixels.
     * @param fillColor    The inner color of the rectangle.
     * @param outlineColor The color of the border outline.
     * @param outlineWidth The width of the border in pixels.
     */
    public void renderRoundedRect(double x, double y,
                                  double width, double height,
                                  float rTopLeft, float rTopRight,
                                  float rBottomLeft, float rBottomRight,
                                  Color fillColor, Color outlineColor, float outlineWidth) {

        if (width <= 0 || height <= 0) return;
        if (fillColor.a == 0 && (outlineColor.a == 0 || outlineWidth <= 0)) return;

        roundedRenderer.render(
                x, y,
                width, height,
                rTopLeft, rTopRight,
                rBottomLeft, rBottomRight,
                fillColor, outlineColor, outlineWidth
        );
    }

    public void flipFrame() {
        roundedRenderer.flipFrame();
    }
}
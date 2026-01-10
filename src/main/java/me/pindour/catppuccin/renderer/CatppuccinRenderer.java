package me.pindour.catppuccin.renderer;

import me.pindour.catppuccin.gui.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.renderer.text.CatppuccinTextRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;

//? if >=1.21.5 {
import me.pindour.catppuccin.renderer.modern.RoundedRenderer;
import me.pindour.catppuccin.renderer.modern.RoundedUniforms;
//?} else {
/*import me.pindour.catppuccin.renderer.legacy.RoundedRendererLegacy;
import net.minecraft.client.util.math.MatrixStack;
*///?}

public class CatppuccinRenderer {
    private static final CatppuccinRenderer INSTANCE = new CatppuccinRenderer();

    private CatppuccinGuiTheme theme;

    //? if >=1.21.5
    private final RoundedRenderer roundedRenderer = new RoundedRenderer();
    //? if <=1.21.4
    //private final RoundedRendererLegacy roundedRenderer = new RoundedRendererLegacy();

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

    public void setAlpha(double a) {
        roundedRenderer.setAlpha(a);
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
     * Renders a rounded rectangle using a widget's dimensions.
     */
    public void roundedRect(WWidget widget, double radius, Color color, CornerStyle style) {
        roundedRect(widget.x, widget.y, widget.width, widget.height, radius, color, style);
    }

    /**
     * Renders a rounded rectangle with selective corner rounding.
     */
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
    public void roundedRect(double x, double y, double width, double height, double radius, Color color,
                            boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
        if (width <= 0 || height <= 0) return;

        if (radius <= 0 || theme == null || !theme.roundedCorners.get()) {
            r.quad(x, y, width, height, color);
            return;
        }

        roundedRenderer.render(x, y, width, height, radius, color, topLeft, topRight, bottomLeft, bottomRight);
    }

    public static void flipFrame() {
        //? if >=1.21.5
        RoundedUniforms.flipFrame();
    }
}
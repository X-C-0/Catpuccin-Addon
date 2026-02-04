package me.pindour.catpuccin.gui.text;

import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.renderer.text.*;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RichTextRenderer implements TextRenderer {
    public static final Color SHADOW_COLOR = new Color(60, 60, 60, 180);
    private final MeshBuilder mesh = new MeshBuilder(MeteorRenderPipelines.UI_TEXT);

    private final Font[] regularFonts;
    private final Font[] boldFonts;
    private final Font[] italicFonts;

    private Font currentFont;
    private FontStyle currentStyle = FontStyle.REGULAR;
    private int currentFontIndex = 0;

    private boolean building;
    private boolean scaleOnly;
    private double fontScale = 1;
    private double scale = 1;

    public RichTextRenderer(FontFace fontFace) throws IOException {
        this.regularFonts = loadFonts(fontFace);
        this.boldFonts = resolveVariant(fontFace, FontInfo.Type.Bold);
        this.italicFonts = resolveVariant(fontFace, FontInfo.Type.Italic);
    }

    private Font[] loadFonts(FontFace fontFace) throws IOException {
        ByteBuffer buffer = fontFace.readToDirectByteBuffer();

        Font[] fonts = new Font[5];

        for (int i = 0; i < fonts.length; i++)
            fonts[i] = new Font(buffer, (int) Math.round(27 * ((i * 0.5) + 1)));

        return fonts;
    }

    private Font[] resolveVariant(FontFace regularFace, FontInfo.Type type) throws IOException {
        FontFamily family = Fonts.getFamily(regularFace.info.family());
        FontFace fontVariant = family.get(type);

        if (fontVariant != null && fontVariant != regularFace)
            return loadFonts(fontVariant);

        return regularFonts; // Fallback
    }

    @Override
    public void setAlpha(double a) {
        mesh.alpha = a;
    }

    @Override
    public void begin(double scale, boolean scaleOnly, boolean big) {
        if (building) throw new RuntimeException("begin() called twice");

        if (!scaleOnly) mesh.begin();

        Font[] fonts = getFonts(currentStyle);
        int scaleIndex = calculateScaleIndex(scale);

        if (scaleIndex >= fonts.length) scaleIndex = fonts.length - 1;

        currentFontIndex = scaleIndex;
        currentFont = fonts[currentFontIndex];

        building = true;

        fontScale = currentFont.getHeight() / 27.0;
        this.scaleOnly = scaleOnly;
        this.scale = 1 + (scale - fontScale) / fontScale;
    }

    private int calculateScaleIndex(double scale) {
        double scaleTruncated = Math.floor(scale * 10) / 10;

        if (scaleTruncated >= 3) return 4;
        if (scaleTruncated >= 2.5) return 3;
        if (scaleTruncated >= 2) return 2;
        if (scaleTruncated >= 1.5) return 1;
        return 0;
    }

    // Width Calculations

    public double getWidth(RichTextSegment segment, int length) {
        Font[] fonts = getFonts(segment.getStyle());
        Font font = building ? getCurrentFont(fonts) : fonts[0];

        return (font.getWidth(segment.getText(), length) + (segment.hasShadow() ? 1 : 0)) * segment.getScale() / 1.5;
    }

    public double getWidth(RichText text, int length) {
        if (length <= 0) return 0;

        double totalWidth = 0;
        int remainingLength = length;

        for (RichTextSegment segment : text.getSegments()) {
            if (remainingLength == 0) break;

            String segText = segment.getText();
            if (segText.isEmpty()) continue;

            int segLength = Math.min(segText.length(), remainingLength);

            Font[] fonts = getFonts(segment.getStyle());
            Font font = building ? getCurrentFont(fonts) : fonts[0];

            double segmentWidth = font.getWidth(segText, segLength);
            if (segment.hasShadow()) segmentWidth += 1;

            totalWidth += segmentWidth * (segment.getScale() / 1.5);
            remainingLength -= segLength;
        }

        return totalWidth;
    }

    public double getWidth(RichText text) {
        return getWidth(text, text.length());
    }

    @Override
    public double getWidth(String text, int length, boolean shadow) {
        if (text.isEmpty()) return 0;
        return getWidth(RichText.of(text).shadowIf(shadow), length);
    }

    // Height Calculations

    public double getHeight(RichTextSegment segment) {
        Font[] fonts = getFonts(segment.getStyle());
        Font font = building ? getCurrentFont(fonts) : fonts[0];

        return (font.getHeight() + 1 + (segment.hasShadow() ? 1 : 0)) * segment.getScale() / 1.5;
    }

    public double getHeight(RichText text) {
        if (text.getSegments().isEmpty()) return 0;

        double largestSegment = 0;
        for (RichTextSegment segment : text.getSegments())
            largestSegment = Math.max(largestSegment, getHeight(segment));

        return largestSegment;
    }

    @Override
    public double getHeight(boolean shadow) {
        Font font = building ? currentFont : getFonts(FontStyle.REGULAR)[0];
        return (font.getHeight() + 1 + (shadow ? 1 : 0)) * scale / 1.5;
    }

    // Rendering

    public void renderTextWithStyle(String text, double x, double y, Color color, boolean shadow, FontStyle fontStyle) {
        if (currentStyle != fontStyle) setFontStyle(fontStyle);
        render(text, x, y, color, shadow);
    }

    @Override
    public double render(String text, double x, double y, Color color, boolean shadow) {
        boolean wasBuilding = building;
        if (!wasBuilding) begin();

        double width = renderText(text, x, y, color, shadow);

        if (!wasBuilding) end();
        return width;
    }

    private double renderText(String text, double x, double y, Color color, boolean shadow) {
        double renderScale = scale / 1.5;

        if (shadow) {
            int originalShadowAlpha = SHADOW_COLOR.a;
            SHADOW_COLOR.a = (int) (color.a / 255.0 * originalShadowAlpha);

            double shadowOffset = fontScale * renderScale;
            double width = currentFont.render(mesh, text, x + shadowOffset, y + shadowOffset, SHADOW_COLOR, renderScale);
            currentFont.render(mesh, text, x, y, color, renderScale);

            SHADOW_COLOR.a = originalShadowAlpha;
            return width;
        } else {
            return currentFont.render(mesh, text, x, y, color, renderScale);
        }
    }

    @Override
    public boolean isBuilding() {
        return building;
    }

    @Override
    public void end() {
        if (!building) throw new RuntimeException("end() called without calling begin()");

        if (!scaleOnly) {
            mesh.end();

            MeshRenderer.begin()
                    .attachments(MinecraftClient.getInstance().getFramebuffer())
                    .pipeline(MeteorRenderPipelines.UI_TEXT)
                    .mesh(mesh)
                    .sampler("u_Texture", currentFont.texture.getGlTextureView(), currentFont.texture.getSampler())
                    .end();
        }

        building = false;
        scale = 1;
    }

    public void setFontStyle(FontStyle style) {
        if (this.currentStyle == style) return;
        this.currentStyle = style;

        Font[] fonts = getFonts(style);
        if (building) currentFont = getCurrentFont(fonts);
    }

    // Helpers

    private Font[] getFonts(FontStyle style) {
        return switch (style) {
            case BOLD -> boldFonts;
            case ITALIC -> italicFonts;
            default -> regularFonts;
        };
    }

    private Font getCurrentFont(Font[] fonts) {
        if (currentFontIndex >= 0 && currentFontIndex < fonts.length)
            return fonts[currentFontIndex];

        return fonts[0];
    }
}
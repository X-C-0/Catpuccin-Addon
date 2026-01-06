package me.pindour.catpuccin.gui.text;

import meteordevelopment.meteorclient.renderer.*;
import meteordevelopment.meteorclient.renderer.text.*;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

#if MC_VER <= MC_1_21_4
import net.minecraft.client.util.math.MatrixStack;
#else
import net.minecraft.client.MinecraftClient;
#endif

public class RichTextRenderer implements TextRenderer {
    public static final Color SHADOW_COLOR = new Color(60, 60, 60, 180);

    #if MC_VER <= MC_1_21_4
    private final Mesh mesh = new ShaderMesh(Shaders.TEXT, DrawMode.Triangles, Mesh.Attrib.Vec2, Mesh.Attrib.Vec2, Mesh.Attrib.Color);
    #else
    private final MeshBuilder mesh = new MeshBuilder(MeteorRenderPipelines.UI_TEXT);
    #endif

    public final FontFace fontFace;

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

    public void destroy() {
        #if MC_VER <= MC_1_21_4
        mesh.destroy();
        #else
        if (mesh.isBuilding()) mesh.end();
        #endif
    }

    public RichTextRenderer(FontFace regularFace, FontFace boldFace, FontFace italicFace) {
        if (boldFace == null || italicFace == null) {
            FontFamily fontFamily = findFontFamily(regularFace);

            regularFace = fontFamily.get(FontInfo.Type.Regular);
            boldFace = fontFamily.get(FontInfo.Type.Bold);
            italicFace = fontFamily.get(FontInfo.Type.Italic);
        }

        this.fontFace = regularFace;
        this.regularFonts = loadFonts(regularFace);
        this.boldFonts = boldFace != null ? loadFonts(boldFace) : regularFonts;
        this.italicFonts = italicFace != null ? loadFonts(italicFace) : regularFonts;
    }

    public RichTextRenderer(FontFace fontFace) {
        this(fontFace, null, null);
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
    #if MC_VER <= MC_1_21_4
    public void end(MatrixStack matrices) {
    #else
    public void end() {
    #endif
        if (!building) throw new RuntimeException("end() called without calling begin()");

        if (!scaleOnly) {
            mesh.end();

            renderBuiltMesh(
                #if MC_VER <= MC_1_21_4
                matrices
                #endif
            );
        }

        building = false;
        scale = 1;
    }

    private void renderBuiltMesh(
        #if MC_VER <= MC_1_21_4
        MatrixStack matrices
        #endif
    ) {
        #if MC_VER <= MC_1_21_4
        GL.bindTexture(currentFont.texture.getGlId());
        mesh.render(matrices);
        #else
        renderMesh();
        #endif
    }

    #if MC_VER >= MC_1_21_5
    private void renderMesh() {
        MeshRenderer.begin()
            .attachments(MinecraftClient.getInstance().getFramebuffer())
            .pipeline(MeteorRenderPipelines.UI_TEXT)
            .mesh(mesh)
            #if MC_VER >= MC_1_21_11
            .sampler("u_Texture", currentFont.texture.getGlTextureView(), currentFont.texture.getSampler())
            #elif MC_VER >= MC_1_21_6
            .sampler("u_Texture", currentFont.texture.getGlTextureView())
            #else
            .setupCallback(pass -> pass.bindSampler("u_Texture", currentFont.texture.getGlTexture()))
            #endif
            .end();
    }
    #endif

    public void setFontStyle(FontStyle style) {
        if (this.currentStyle == style) return;
        this.currentStyle = style;

        Font[] fonts = getFonts(style);
        if (building) currentFont = getCurrentFont(fonts);
    }

    // Helpers

    private Font[] loadFonts(FontFace fontFace) {
        byte[] bytes = Utils.readBytes(fontFace.toStream());
        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length).put(bytes).flip();

        Font[] fonts = new Font[5];

        for (int i = 0; i < fonts.length; i++)
            fonts[i] = new Font(buffer, (int) Math.round(27 * ((i * 0.5) + 1)));

        return fonts;
    }

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

    private FontFamily findFontFamily(FontFace fontFace) {
        for (FontFamily fontFamily : Fonts.FONT_FAMILIES)
            if (fontFamily.getName().equalsIgnoreCase(fontFace.info.family()))
                return fontFamily;

        return Fonts.FONT_FAMILIES.getFirst();
    }
}

package me.pindour.catppuccin.renderer.rounded.modern;

//? if >=1.21.5 {
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import me.pindour.catppuccin.api.render.style.Outline;
import me.pindour.catppuccin.api.render.style.Shadow;
import me.pindour.catppuccin.renderer.CatppuccinRenderer;
import net.minecraft.client.renderer.DynamicUniformStorage;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.joml.Vector2f;
import org.joml.Vector4f;
//? if >=26.2
import org.jspecify.annotations.NonNull;

import java.nio.ByteBuffer;

public class RoundedUniforms {
    private static final int ROUNDED_DATA_SIZE = new Std140SizeCalculator()
            .putVec4()
            .putVec4()
            .putVec2()
            .putVec4()
            .putVec2()
            .putVec4()
            .putVec4()
            .putVec2()
            .putVec2()
            .putInt()
            .putVec2()
            .get();

    private static final RoundedRectData ROUNDED_DATA = new RoundedRectData();
    private static final DynamicUniformStorage<RoundedRectData> ROUNDED_STORAGE = new DynamicUniformStorage<>("Catppuccin - Rounded UBO", ROUNDED_DATA_SIZE, 16);

    public static GpuBufferSlice getUniformStorage() {
        return ROUNDED_STORAGE.writeUniform(ROUNDED_DATA);
    }

    public static void flipFrame() {
        ROUNDED_STORAGE.endFrame();
    }

    public static void update(double x, double y,
                              double width, double height,
                              float topLeft, float topRight,
                              float bottomLeft, float bottomRight,
                              Color fillColor,
                              Outline outline,
                              Shadow shadow) {

        ROUNDED_DATA.radii.set(topLeft, topRight, bottomRight, bottomLeft);
        ROUNDED_DATA.halfSize.set((float) (width * 0.5), (float) (height * 0.5));
        ROUNDED_DATA.center.set((float) (x + width * 0.5), (float) (y + height * 0.5));
        applyClipRect(ROUNDED_DATA.clipRect);

        setColor(ROUNDED_DATA.fillColor, fillColor);
        setColor(ROUNDED_DATA.borderColor, outline.color);
        ROUNDED_DATA.borderData.set(outline.width, 1f);

        setColor(ROUNDED_DATA.shadowColor, shadow.color);
        ROUNDED_DATA.shadowOffset.set((float) shadow.offsetX, (float) shadow.offsetY);
        ROUNDED_DATA.shadowBlurSpread.set((float) shadow.blur, (float) shadow.spread);
        ROUNDED_DATA.shadowEnabled = shadow.isVisible() ? 1 : 0;
    }

    private static void applyClipRect(Vector4f target) {
        CatppuccinRenderer renderer = CatppuccinRenderer.get();
        if (renderer.isClipEnabled()) {
            target.set(renderer.getClipMinX(), renderer.getClipMinY(), renderer.getClipMaxX(), renderer.getClipMaxY());
        } else {
            target.set(0f, 0f, -1f, -1f);
        }
    }

    private static void setColor(Vector4f target, Color color) {
        target.set(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);
    }

    private static final class RoundedRectData implements DynamicUniformStorage.DynamicUniform {
        private final Vector4f fillColor = new Vector4f();
        private final Vector4f borderColor = new Vector4f();
        private final Vector2f borderData = new Vector2f();
        private final Vector4f radii = new Vector4f();
        private final Vector2f halfSize = new Vector2f();
        private final Vector4f clipRect = new Vector4f();
        private final Vector4f shadowColor = new Vector4f();
        private final Vector2f shadowOffset = new Vector2f();
        private final Vector2f shadowBlurSpread = new Vector2f();
        private int shadowEnabled;
        private final Vector2f center = new Vector2f();

        @Override
        public void write(
                //? >=26.2
                @NonNull
                ByteBuffer buffer
        ) {
            Std140Builder.intoBuffer(buffer)
                    .putVec4(fillColor)
                    .putVec4(borderColor)
                    .putVec2(borderData)
                    .putVec4(radii)
                    .putVec2(halfSize)
                    .putVec4(clipRect)
                    .putVec4(shadowColor)
                    .putVec2(shadowOffset)
                    .putVec2(shadowBlurSpread)
                    .putInt(shadowEnabled)
                    .putVec2(center);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }
}
//?}
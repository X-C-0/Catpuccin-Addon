package me.pindour.catppuccin.renderer.rounded.modern;

//? if >=1.21.5 {
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import me.pindour.catppuccin.renderer.CatppuccinRenderer;
import net.minecraft.client.gl.DynamicUniformStorage;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

public class RoundedUniforms {
    private static final int ROUNDED_DATA_SIZE = new Std140SizeCalculator()
            .putVec4()
            .putVec4()
            .putVec2()
            .putVec4()
            .putVec2()
            .putVec4()
            .get();

    private static final RoundedRectData ROUNDED_DATA = new RoundedRectData();
    private static final DynamicUniformStorage<RoundedRectData> ROUNDED_STORAGE = new DynamicUniformStorage<>("Catppuccin - Rounded UBO", ROUNDED_DATA_SIZE, 16);

    public static GpuBufferSlice getUniformStorage() {
        return ROUNDED_STORAGE.write(ROUNDED_DATA);
    }

    public static void flipFrame() {
        ROUNDED_STORAGE.clear();
    }

    public static void update(double width, double height,
                              float topLeft, float topRight,
                              float bottomLeft, float bottomRight,
                              Color fillColor, Color borderColor, float borderWidth) {

        ROUNDED_DATA.fillColor.set(fillColor.r / 255f, fillColor.g / 255f, fillColor.b / 255f, fillColor.a / 255f);
        ROUNDED_DATA.borderColor.set(borderColor.r / 255f, borderColor.g / 255f, borderColor.b / 255f, borderColor.a / 255f);
        ROUNDED_DATA.borderData.set(borderWidth, 1f);
        ROUNDED_DATA.radii.set(topLeft, topRight, bottomRight, bottomLeft);
        ROUNDED_DATA.halfSize.set((float) (width * 0.5), (float) (height * 0.5));
        applyClipRect(ROUNDED_DATA.clipRect);
    }

    private static void applyClipRect(Vector4f target) {
        CatppuccinRenderer renderer = CatppuccinRenderer.get();
        if (renderer.isClipEnabled()) {
            target.set(renderer.getClipMinX(), renderer.getClipMinY(), renderer.getClipMaxX(), renderer.getClipMaxY());
        } else {
            target.set(0f, 0f, -1f, -1f);
        }
    }

    private static final class RoundedRectData implements DynamicUniformStorage.Uploadable {
        private final Vector4f fillColor = new Vector4f();
        private final Vector4f borderColor = new Vector4f();
        private final Vector2f borderData = new Vector2f();
        private final Vector4f radii = new Vector4f();
        private final Vector2f halfSize = new Vector2f();
        private final Vector4f clipRect = new Vector4f();

        @Override
        public void write(ByteBuffer buffer) {
            Std140Builder.intoBuffer(buffer)
                    .putVec4(fillColor)
                    .putVec4(borderColor)
                    .putVec2(borderData)
                    .putVec4(radii)
                    .putVec2(halfSize)
                    .putVec4(clipRect);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }
}
//?}
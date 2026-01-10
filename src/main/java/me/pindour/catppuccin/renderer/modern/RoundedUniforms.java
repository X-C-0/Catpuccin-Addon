package me.pindour.catppuccin.renderer.modern;

//? if >=1.21.5 {
/*import com.mojang.blaze3d.buffers.GpuBufferSlice;
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
            .putVec4()
            .putVec4()
            .putVec2()
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

    public static void updateModern(double width, double height, double radius, Color color, double alpha,
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
        private final Vector4f radii = new Vector4f();
        private final Vector4f borderData = new Vector4f();
        private final Vector2f halfSize = new Vector2f();
        private final Vector2f padding = new Vector2f();
        private final Vector4f clipRect = new Vector4f();

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
*///?}
package me.pindour.catppuccin.renderer.rounded;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import me.pindour.catppuccin.renderer.CatppuccinRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

//? if >=1.21.6 {
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.gl.DynamicUniformStorage;
import org.joml.Vector2f;
import org.joml.Vector4f;
import java.nio.ByteBuffer;
//?}

//? if <=1.21.4 {
/*import me.pindour.catppuccin.renderer.CatpuccinShader;
 *///?}

public class RoundedUniforms {
    //? if >=1.21.6 {
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
    //?}

    //? if <=1.21.4 {
    /*private static final Color tmpFillColor = new Color();
    private static final Color tmpBorderColor = new Color();

    public static void updateLegacy(CatpuccinShader shader, RoundedCall call, double alpha) {
        float r = call.radius;
        float tl = call.topLeft ? r : 0f;
        float tr = call.topRight ? r : 0f;
        float br = call.bottomRight ? r : 0f;
        float bl = call.bottomLeft ? r : 0f;

        int alphaValue = Math.round(call.color.a * (float) alpha);
        tmpFillColor.set(call.color).a(alphaValue);
        tmpBorderColor.set(0, 0, 0, 0);

        shader.set("u_FillColor", tmpFillColor);
        shader.set("u_BorderColor", tmpBorderColor);
        shader.set("u_Radii0", tl, tr);
        shader.set("u_Radii1", br, bl);
        shader.set("u_BorderData", 0.0, 1.0);
        shader.set("u_HalfSize", call.width * 0.5f, call.height * 0.5f);
        shader.set("u_Padding", 0.0, 0.0);
        if (call.clipEnabled) {
            shader.set("u_ClipMin", call.clipMinX, call.clipMinY);
            shader.set("u_ClipMax", call.clipMaxX, call.clipMaxY);
        } else {
            shader.set("u_ClipMin", 0.0, 0.0);
            shader.set("u_ClipMax", -1.0, -1.0);
        }
    }

    public static final class RoundedCall {
        public float x;
        public float y;
        public float width;
        public float height;
        public float radius;
        public Color color;
        public boolean topLeft;
        public boolean topRight;
        public boolean bottomLeft;
        public boolean bottomRight;
        public boolean clipEnabled;
        public float clipMinX;
        public float clipMinY;
        public float clipMaxX;
        public float clipMaxY;

        public void set(float x, float y, float width, float height, float radius, Color color,
                       boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight,
                       boolean clipEnabled, float clipMinX, float clipMinY, float clipMaxX, float clipMaxY) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.radius = radius;
            this.color = color;
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomLeft = bottomLeft;
            this.bottomRight = bottomRight;
            this.clipEnabled = clipEnabled;
            this.clipMinX = clipMinX;
            this.clipMinY = clipMinY;
            this.clipMaxX = clipMaxX;
            this.clipMaxY = clipMaxY;
        }
    }
    *///?}
}
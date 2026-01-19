package me.pindour.catppuccin.renderer.rounded.legacy;

//? if <=1.21.4 {
/*import meteordevelopment.meteorclient.utils.render.color.Color;

public class RoundedUniformsLegacy {

    public static void update(CatppuccinShader shader, RoundedCall call) {
        shader.set("u_FillColor", call.fillColor);
        shader.set("u_BorderColor", call.borderColor);
        shader.set("u_Radii0", call.topLeft, call.topRight);
        shader.set("u_Radii1", call.bottomRight, call.bottomLeft);
        shader.set("u_BorderData", call.borderWidth, 1.0);
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
        public Color fillColor;
        public Color borderColor;
        public float borderWidth;
        public float topLeft;
        public float topRight;
        public float bottomLeft;
        public float bottomRight;
        public boolean clipEnabled;
        public float clipMinX;
        public float clipMinY;
        public float clipMaxX;
        public float clipMaxY;

        public void set(float x, float y,
                        float width, float height,
                        float topLeft, float topRight,
                        float bottomLeft, float bottomRight,
                        Color fillColor, Color borderColor, float borderWidth,
                        boolean clipEnabled,
                        float clipMinX, float clipMinY,
                        float clipMaxX, float clipMaxY) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.fillColor = fillColor;
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
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
}
*///?}
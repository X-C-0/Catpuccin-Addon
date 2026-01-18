package me.pindour.catppuccin.renderer.rounded.legacy;

//? if <=1.21.4 {
/*import meteordevelopment.meteorclient.utils.render.color.Color;

public class RoundedUniformsLegacy {
    private static final Color tmpFillColor = new Color();
    private static final Color tmpBorderColor = new Color();

    public static void update(CatppuccinShader shader, RoundedCall call, double alpha) {
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
}
*///?}
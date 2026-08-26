package me.pindour.catppuccin.renderer.rounded.legacy;

//? if <=1.21.4 {
/*import me.pindour.catppuccin.api.render.style.Outline;
import me.pindour.catppuccin.api.render.style.Shadow;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class RoundedUniformsLegacy {

    public static void update(CatppuccinShader shader, RoundedCall call) {
        shader.set("u_FillColor", call.fillColor);
        shader.set("u_BorderColor", call.outline.color);
        shader.set("u_Radii", call.topLeft, call.topRight, call.bottomRight, call.bottomLeft);
        shader.set("u_BorderData", call.outline.width, 1.0);
        shader.set("u_HalfSize", call.width * 0.5f, call.height * 0.5f);
        shader.set("u_Center", call.x + call.width * 0.5f, call.y + call.height * 0.5f);
        if (call.clipEnabled) {
            shader.set("u_ClipRect", call.clipMinX, call.clipMinY, call.clipMaxX, call.clipMaxY);
        } else {
            shader.set("u_ClipRect", 0.0, 0.0, -1.0, -1.0);
        }
        shader.set("u_ShadowColor", call.shadow.color);
        shader.set("u_ShadowOffset", call.shadow.offsetX, call.shadow.offsetY);
        shader.set("u_ShadowBlurSpread", call.shadow.blur, call.shadow.spread);
        shader.set("u_ShadowEnabled", call.shadow.isVisible() ? 1 : 0);
    }

    public static final class RoundedCall {
        public float x;
        public float y;
        public float width;
        public float height;
        public float padX;
        public float padY;
        public Color fillColor;
        public float topLeft;
        public float topRight;
        public float bottomLeft;
        public float bottomRight;
        public Outline outline;
        public Shadow shadow;
        public boolean clipEnabled;
        public float clipMinX;
        public float clipMinY;
        public float clipMaxX;
        public float clipMaxY;

        public void set(float x, float y,
                        float width, float height,
                        float padX, float padY,
                        float topLeft, float topRight,
                        float bottomLeft, float bottomRight,
                        Color fillColor,
                        Outline outline,
                        Shadow shadow,
                        boolean clipEnabled,
                        float clipMinX, float clipMinY,
                        float clipMaxX, float clipMaxY) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.padX = padX;
            this.padY = padY;
            this.fillColor = fillColor;
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomLeft = bottomLeft;
            this.bottomRight = bottomRight;
            this.outline = outline;
            this.shadow = shadow;
            this.clipEnabled = clipEnabled;
            this.clipMinX = clipMinX;
            this.clipMinY = clipMinY;
            this.clipMaxX = clipMaxX;
            this.clipMaxY = clipMaxY;
        }
    }
}
*///?}
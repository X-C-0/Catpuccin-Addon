package me.pindour.catppuccin.renderer.rounded;

import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.util.math.MatrixStack;

public interface RoundedRendererInternal {
    void begin();

    void end();

    void render(MatrixStack matrices);

    void render(double x, double y,
                double width, double height,
                float topLeft, float topRight,
                float bottomLeft, float bottomRight,
                Color fillColor, Color outlineColor, float outlineWidth);

    void flipFrame();
}

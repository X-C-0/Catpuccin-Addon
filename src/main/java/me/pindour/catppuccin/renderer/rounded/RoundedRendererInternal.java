package me.pindour.catppuccin.renderer.rounded;

import com.mojang.blaze3d.vertex.PoseStack;

import meteordevelopment.meteorclient.utils.render.color.Color;

public interface RoundedRendererInternal {
    void begin();

    void end();

    void render(PoseStack matrices);

    void render(double x, double y,
                double width, double height,
                float topLeft, float topRight,
                float bottomLeft, float bottomRight,
                Color fillColor, Color outlineColor, float outlineWidth);

    void flipFrame();
}

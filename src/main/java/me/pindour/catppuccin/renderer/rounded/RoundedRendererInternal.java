package me.pindour.catppuccin.renderer.rounded;

import com.mojang.blaze3d.vertex.PoseStack;
import me.pindour.catppuccin.api.render.style.Outline;
import me.pindour.catppuccin.api.render.style.Shadow;
import meteordevelopment.meteorclient.utils.render.color.Color;

public interface RoundedRendererInternal {
    void begin();

    void end();

    void render(PoseStack stack);

    void render(double x, double y,
                double width, double height,
                float topLeft, float topRight,
                float bottomLeft, float bottomRight,
                Color fillColor, Outline outline, Shadow shadow);

    void flipFrame();
}

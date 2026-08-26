package me.pindour.catppuccin.api.render;

import me.pindour.catppuccin.api.render.style.Outline;
import me.pindour.catppuccin.api.render.style.Shadow;
import meteordevelopment.meteorclient.utils.render.color.Color;

public interface RoundedRectRenderer {
    void renderRoundedRect(double x, double y,
                           double width, double height,
                           float rTopLeft, float rTopRight,
                           float rBottomLeft, float rBottomRight,
                           Color fillColor,
                           Outline outline,
                           Shadow shadow);
}

package me.pindour.catppuccin.api.render;

import meteordevelopment.meteorclient.utils.render.color.Color;

public interface RoundedRectRenderer {
    void renderRoundedRect(double x, double y,
                           double width, double height,
                           float rTopLeft, float rTopRight,
                           float rBottomLeft, float rBottomRight,
                           Color fillColor, Color outlineColor, float outlineWidth);
}

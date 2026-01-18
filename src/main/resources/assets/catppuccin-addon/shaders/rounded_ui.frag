#version 330 core

out vec4 fragColor;

in vec2 v_LocalPos;
in vec2 v_ScreenPos;

layout(std140) uniform RoundedRectData {
    vec4 u_FillColor;
    vec4 u_BorderColor;
    vec2 u_BorderData;  // x = border width, y = edge softness multiplier
    vec4 u_Radii;       // x = top-left, y = top-right, z = bottom-right, w = bottom-left
    vec2 u_HalfSize;
    vec4 u_ClipRect;    // x = minX, y = minY, z = maxX, w = maxY
};

float roundedRectSDF(vec2 p, vec2 halfSize, vec4 radii) {
    float r = radii.x;
    if (p.x > 0.0 && p.y < 0.0) r = radii.y;
    else if (p.x > 0.0 && p.y > 0.0) r = radii.z;
    else if (p.x < 0.0 && p.y > 0.0) r = radii.w;

    float maxRadius = min(halfSize.x, halfSize.y);
    r = clamp(r, 0.0, maxRadius);

    vec2 q = abs(p) - (halfSize - vec2(r));
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
    if (u_ClipRect.z > u_ClipRect.x) {
        if (v_ScreenPos.x < u_ClipRect.x || v_ScreenPos.y < u_ClipRect.y ||
        v_ScreenPos.x > u_ClipRect.z || v_ScreenPos.y > u_ClipRect.w) {
            discard;
        }
    }

    float dist = roundedRectSDF(v_LocalPos, u_HalfSize, u_Radii);

    float softness = max(u_BorderData.y, 1.0);
    float aa = fwidth(dist) * 0.5 * softness;

    float shapeAlpha = smoothstep(aa, -aa, dist);
    if (shapeAlpha <= 0.0) discard;

    vec4 color = u_FillColor;
    float borderWidth = u_BorderData.x;

    if (borderWidth > 0.0) {
        float innerDist = dist + borderWidth;
        float innerAlpha = smoothstep(aa, -aa, innerDist);

        color = mix(u_BorderColor, u_FillColor, innerAlpha);
    }

    color.a *= shapeAlpha;
    if (color.a <= 0.0) discard;

    fragColor = color;
}
#version 330 core

out vec4 fragColor;

in vec2 v_LocalPos;
in vec2 v_ScreenPos;

uniform vec4 u_FillColor;
uniform vec4 u_BorderColor;
uniform vec2 u_Radii0;
uniform vec2 u_Radii1;
uniform vec2 u_BorderData;
uniform vec2 u_HalfSize;
uniform vec2 u_ClipMin;
uniform vec2 u_ClipMax;

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
    if (u_ClipMax.x > u_ClipMin.x) {
        if (v_ScreenPos.x < u_ClipMin.x || v_ScreenPos.y < u_ClipMin.y ||
            v_ScreenPos.x > u_ClipMax.x || v_ScreenPos.y > u_ClipMax.y) {
            discard;
        }
    }

    vec4 radii = vec4(u_Radii0, u_Radii1);
    float dist = roundedRectSDF(v_LocalPos, u_HalfSize, radii);

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

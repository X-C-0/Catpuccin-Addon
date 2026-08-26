#version 330 core

out vec4 fragColor;

in vec2 v_LocalPos;
in vec2 v_ScreenPos;

//? if <=1.21.4 {
/*uniform vec4 u_FillColor;
uniform vec4 u_BorderColor;
uniform vec4 u_Radii;
uniform vec2 u_BorderData;
uniform vec2 u_HalfSize;
uniform vec4 u_ClipRect;
uniform vec4 u_ShadowColor;
uniform vec2 u_ShadowOffset;
uniform vec2 u_ShadowBlurSpread;
uniform int u_ShadowEnabled;
*///? } else {
layout(std140) uniform RoundedRectData {
    vec4 u_FillColor;
    vec4 u_BorderColor;
    vec2 u_BorderData;       // x = border width, y = edge softness multiplier
    vec4 u_Radii;            // x = top-left, y = top-right, z = bottom-right, w = bottom-left
    vec2 u_HalfSize;
    vec4 u_ClipRect;         // x = minX, y = minY, z = maxX, w = maxY
    vec4 u_ShadowColor;      // rgba
    vec2 u_ShadowOffset;     // px
    vec2 u_ShadowBlurSpread; // x = blur, y = spread
    int u_ShadowEnabled;     // 0 / 1 - bool
    vec2 u_Center;           // rect center in screen space
};
//? }

float roundedRectSDF(vec2 p, vec2 halfSize, vec4 radii) {
    vec2 s = step(vec2(0.0), p);
    float r = mix(mix(radii.x, radii.w, s.y), mix(radii.y, radii.z, s.y), s.x);

    float maxRadius = min(halfSize.x, halfSize.y);
    r = clamp(r, 0.0, maxRadius);

    vec2 q = abs(p) - (halfSize - vec2(r));
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

// Antialiased coverage (0..1) for a signed distance, given a half-width AA band.
float coverage(float dist, float aa) {
    return smoothstep(aa, -aa, dist);
}

// Standard premultiplied-alpha "over" compositing.
// src.rgb is a STRAIGHT (non-premultiplied) color; src.a is its opacity.
// dst is assumed to already be premultiplied (as produced by this same function).
vec4 compositeOver(vec4 src, vec4 dst) {
    vec3 srcPremultiplied = src.rgb * src.a;
    float outAlpha = src.a + dst.a * (1.0 - src.a);
    vec3 outRGB = srcPremultiplied + dst.rgb * (1.0 - src.a);
    return vec4(outRGB, outAlpha);
}

void main() {

    // Clip Rect

    if (u_ClipRect.z > u_ClipRect.x) {
        if (v_ScreenPos.x < u_ClipRect.x || v_ScreenPos.y < u_ClipRect.y ||
            v_ScreenPos.x > u_ClipRect.z || v_ScreenPos.y > u_ClipRect.w) {
            discard;
        }
    }

    vec4 result = vec4(0.0); // premultiplied accumulator

    // Drop shadow

    if (u_ShadowEnabled == 1) {
        float blur = max(u_ShadowBlurSpread.x, 0.0);
        float spread = u_ShadowBlurSpread.y;

        vec2 shadowLocalPos = v_LocalPos - u_ShadowOffset;
        vec2 shadowHalfSize = u_HalfSize + spread;
        vec4 shadowRadii = max(vec4(0.0), u_Radii + spread);

        float shadowDist = roundedRectSDF(shadowLocalPos, shadowHalfSize, shadowRadii);
        float shadowAA = (blur == 0.0) ? fwidth(shadowDist) * 0.5 : 0.0;
        float shadowAlpha = coverage(shadowDist, blur + shadowAA);

        vec4 shadow = vec4(u_ShadowColor.rgb, shadowAlpha * u_ShadowColor.a);
        result = compositeOver(shadow, result);
    }

    // Fill + border

    bool hasFill = u_FillColor.a > 0.001;
    bool hasBorder = u_BorderData.x > 0.0 && u_BorderColor.a > 0.001;

    if (hasFill || hasBorder) {
        float fillDist = roundedRectSDF(v_LocalPos, u_HalfSize, u_Radii);
        float softness = max(u_BorderData.y, 1.0);
        float fillAA = fwidth(fillDist) * 0.5 * softness;
        float fillAlpha = coverage(fillDist, fillAA);

        if (fillAlpha > 0.0) {
            vec4 fill = u_FillColor;

            float borderWidth = u_BorderData.x;
            if (borderWidth > 0.0) {
                float innerDist = fillDist + borderWidth;
                float innerAlpha = coverage(innerDist, fillAA);
                fill = mix(u_BorderColor, u_FillColor, innerAlpha);
            }

            fill.a *= fillAlpha;
            result = compositeOver(fill, result);
        }
    }

    if (result.a <= 0.001) discard;

    // Output is expected as straight alpha
    fragColor = vec4(result.rgb / max(result.a, 1e-4), result.a);
}
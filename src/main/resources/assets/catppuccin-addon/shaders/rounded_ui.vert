#version 330 core

layout(location = 0) in vec2 Position;
layout(location = 1) in vec4 Color;

//? if <=1.21.4 {
/*uniform mat4 u_Proj;
uniform mat4 u_ModelView;
uniform vec2 u_Center;

*///? } else {
layout(std140) uniform MeshData {
    mat4 u_Proj;
    mat4 u_ModelView;
};

layout(std140) uniform RoundedRectData {
    vec4 u_FillColor;
    vec4 u_BorderColor;
    vec2 u_BorderData;
    vec4 u_Radii;
    vec2 u_HalfSize;
    vec4 u_ClipRect;
    vec4 u_ShadowColor;
    vec2 u_ShadowOffset;
    vec2 u_ShadowBlurSpread;
    int u_ShadowEnabled;
    vec2 u_Center;
};
//? }

out vec2 v_LocalPos;
out vec2 v_ScreenPos;

void main() {
    vec4 worldPos = u_ModelView * vec4(Position, 0.0, 1.0);
    gl_Position = u_Proj * worldPos;

    // Local position relative to the rect center, independent of quad padding.
    v_LocalPos = Position - u_Center;
    v_ScreenPos = Position;
}

#version 330 core

layout(location = 0) in vec2 Position;
layout(location = 1) in vec2 Texture;
layout(location = 2) in vec4 Color;

layout(std140) uniform MeshData {
    mat4 u_Proj;
    mat4 u_ModelView;
};

out vec2 v_LocalPos;
out vec2 v_ScreenPos;

void main() {
    vec4 worldPos = u_ModelView * vec4(Position, 0.0, 1.0);
    gl_Position = u_Proj * worldPos;

    v_LocalPos = Texture;
    v_ScreenPos = Position;
}

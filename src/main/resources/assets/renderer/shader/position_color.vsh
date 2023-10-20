#version 330

layout (location = 0) in vec3 i_pos;
layout (location = 1) in vec4 i_color;

uniform mat4 u_projection;

out vec4 vertexColor;

void main() {
    gl_Position = u_projection * vec4(i_pos, 1.0f);
    vertexColor = i_color;
}
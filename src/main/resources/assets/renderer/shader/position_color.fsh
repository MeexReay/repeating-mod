#version 330

in vec4 vertexColor;

out vec4 fragmentColor;

void main() {
    if (vertexColor.a == 0.0f) {
        discard;
    }
    fragmentColor = vertexColor;
}
#version 330

// Fullscreen triangle generated from gl_VertexID; the pipeline uses an empty
// vertex format and draws three vertices, so there is no vertex buffer.

out vec2 texCoord0;

void main() {
    vec2 uv = vec2((gl_VertexID << 1) & 2, gl_VertexID & 2);

    gl_Position = vec4(uv * 2.0 - 1.0, 0.0, 1.0);
    texCoord0 = uv;
}

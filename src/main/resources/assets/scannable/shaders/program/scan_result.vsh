#version 130

attribute vec4 Position;

uniform mat4 ProjMat;

out vec4 color;
out vec2 texCoord;

void main() {
    gl_Position = ProjMat * Position;
    color = gl_Color;
    texCoord = gl_MultiTexCoord0.st;
}

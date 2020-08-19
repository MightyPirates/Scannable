#version 130

attribute vec4 Position;

uniform mat4 projMat;
uniform mat4 viewMat;

out vec4 color;
out vec2 texCoord;

void main() {
    gl_Position = projMat * viewMat * Position;
    color = gl_Color;
    texCoord = gl_MultiTexCoord0.st;
}

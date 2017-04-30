#version 120

varying out vec3 ray;
varying out vec2 texCoord;

void main() {
  ray = gl_Normal.xyz;
  texCoord = gl_MultiTexCoord0.st;
  gl_Position = ftransform();
}
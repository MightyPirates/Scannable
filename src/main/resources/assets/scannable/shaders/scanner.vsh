#version 130

out vec3 ray;
out vec2 texCoord;

void main() {
  ray = gl_Normal.xyz;
  texCoord = gl_MultiTexCoord0.st;
  gl_Position = ftransform();
}
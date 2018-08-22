#version 130

uniform sampler2D depthTex;

in vec2 texCoord;

void main()
{
    gl_FragColor =  texture2D(depthTex, texCoord);
}

#version 130

uniform vec2 OutSize;
uniform float Time;

in vec4 color;
in vec2 texCoord;

float scanlines() {
    return sqrt(sin(gl_FragCoord.y+Time*10)*0.5+0.5);
}

void main() {
    float timeScale = (sin(Time * 2.5) + 1.0) * 0.5;
    timeScale = timeScale*0.25 + 0.75;

    vec2 edgeDist = abs(texCoord.xy-0.5)*2.0;
    edgeDist = edgeDist*0.25+0.75;
    float edgeMul = pow(max(edgeDist.x, edgeDist.y), 8.0)*0.5+0.5;

    vec4 c = color*scanlines();
    c.a *= timeScale;
    c.a *= edgeMul;
    gl_FragColor = c;
}

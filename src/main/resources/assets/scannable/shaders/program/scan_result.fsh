#version 120

uniform float time;

varying vec4 color;
varying vec2 texCoord;

float scanlines() {
    return sqrt(sin(gl_FragCoord.y+time*10)*0.5+0.5);
}

void main() {
    float timeScale = (sin(time * 2.5) + 1.0) * 0.5;
    timeScale = timeScale*0.15 + 0.85;

    vec2 edgeDist = abs(texCoord.xy-0.5)*2.0;
    edgeDist = edgeDist*0.25+0.75;
    float edgeMul = pow(max(edgeDist.x, edgeDist.y), 8.0)*0.4+0.6;

    vec4 c = color*scanlines();
    c *= timeScale;
    c *= edgeMul;
    gl_FragColor = c;
}

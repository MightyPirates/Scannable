#version 330

// vec4 for alignment.
layout(std140) uniform ScanResult {
    vec4 scanTime; // x: seconds since these results were collected.
};

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

float scanlines(float time) {
    return sqrt(sin(gl_FragCoord.y+time*10)*0.5+0.5);
}

void main() {
    float time = scanTime.x;

    float timeScale = (sin(time * 2.5) + 1.0) * 0.5;
    timeScale = timeScale*0.15 + 0.85;

    vec2 edgeDist = abs(texCoord0.xy-0.5)*2.0;
    edgeDist = edgeDist*0.25+0.75;
    float edgeMul = pow(max(edgeDist.x, edgeDist.y), 8.0)*0.8+0.2;

    vec4 c = vertexColor*scanlines(time);
    c *= timeScale;
    c *= edgeMul;
    fragColor = c;
}

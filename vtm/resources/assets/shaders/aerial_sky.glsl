#ifdef GLES
precision highp float;
#endif

attribute vec4 a_pos;
uniform mat4 u_mvp;

varying float height;

void main(void) {
    gl_Position = u_mvp * a_pos;
    height = a_pos.z;
}

$$

#ifdef GLES
precision highp float;
#endif

varying float height;

uniform vec4 u_skyColor;
uniform vec4 u_horizonColor;

float offset = 100.0;

void main() {
    float visibility;
    float h = height - offset;
    if (h > 0)
    visibility = clamp(h / 2000.0, 0.0, 1.0);
    else
    visibility = clamp(-h / (2 * offset), 0.0, 1.0);
    gl_FragColor = mix(u_horizonColor, u_skyColor, visibility);
}

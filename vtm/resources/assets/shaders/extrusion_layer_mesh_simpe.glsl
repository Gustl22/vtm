#ifdef GLES
precision highp float;
#endif
uniform mat4 u_mvp;
uniform mat4 u_mv;
uniform vec4 u_color;
uniform float u_alpha;
uniform vec3 u_light;
attribute vec4 a_pos;
attribute vec2 a_normal;
varying vec4 color;

// Fog
const float density = 0.007;
const float gradient = 1.5;

void main() {
    // change height by u_alpha
    vec4 pos = a_pos;
    gl_Position = u_mvp * pos;

    // Fog
    float distance = -(u_mv * pos).z;// Direct distance from camera
    float visibility = clamp(exp(-pow((distance * densitiy), gradient)), 0.0, 1.0);
    visibility = clamp((distance / 4000.0), 0.0, 1.0);

    // extreme fake-ssao by height
    color = u_color.rgba;
    color = mix(vec4(1.0, 0.0, 0.0, 1.0), color, visibility);

    if (distance < 0){
        color = vec4(0.0, 0.0, 1.0, 1.0);
    }
}

$$

varying vec4 color;

void main() {
    gl_FragColor = color;
}

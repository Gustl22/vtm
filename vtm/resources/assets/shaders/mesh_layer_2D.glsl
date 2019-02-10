#ifdef GLES
precision highp float;
#endif
uniform mat4 u_mvp;
uniform float u_height;
attribute float a_pos_z;
attribute vec2 a_pos;

void main() {
    gl_Position = u_mvp * vec4(a_pos, u_height + a_pos_z, 1.0);
}

$$

#ifdef GLES
precision highp float;
#endif
uniform vec4 u_color;

void main() {
    gl_FragColor = u_color;
}

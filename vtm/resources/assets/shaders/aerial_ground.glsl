#ifdef GLES
precision highp float;
#endif

attribute vec4 a_pos;
uniform mat4 u_mvp;

//varying float dist;

void main(void) {
    gl_Position = u_mvp * a_pos;
    //    vec4 mtmp = gl_Position;
    //    dist = (mtmp.xyz / mtmp.w).z * 0.5 + 0.5 - u_fogShift;
}

$$

#ifdef GLES
precision highp float;
#endif

//varying float dist;

#ifdef FOG
uniform float u_fogDensitiy;
uniform float u_fogGradient;
uniform vec4 u_fogColor;
uniform float u_fogShift;
#endif

void main() {

    float dist = clamp((gl_FragCoord.z) - u_fogShift, 0.0, 1.0);
    //    float dist = 1.0;
    float visibility = clamp(exp(-pow((dist * u_fogDensitiy), u_fogGradient)), 0.0, 1.0);
    gl_FragColor = mix(u_fogColor, gl_FragColor, visibility);
    //    gl_FragColor = vec4(gl_FragCoord.z / gl_FragCoord.w) - 2.0;
}

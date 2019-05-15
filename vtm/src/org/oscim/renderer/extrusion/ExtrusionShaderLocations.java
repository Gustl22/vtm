package org.oscim.renderer.extrusion;

import org.oscim.renderer.GLShader;
import org.oscim.renderer.ShaderLocations;

public class ExtrusionShaderLocations extends ShaderLocations {
    /**
     * The normal of vertex's face as attribute.
     */
    int aNormal;

    /**
     * The alpha value (e.g. for fading animation) as uniform.
     */
    int uAlpha;

    /**
     * The extrusion color(s) as uniform.
     */
    int uColor;

    /**
     * The lights position vector as uniform.
     */
    int uLight;

    /**
     * The shader render mode as uniform.
     * <p>
     * Extrusion shader:
     * -1: translucent (depth buffer only)
     * 0: draw roof
     * 1: draw side one
     * 2: draw side two
     * 3: draw outline
     */
    int uMode;

    /**
     * The height limit of extrusions as uniform.
     */
    int uZLimit;

    public ExtrusionShaderLocations() {
        super();
    }

    @Override
    public void init(GLShader shader) {
        uColor = shader.getUniform("u_color");
        uAlpha = shader.getUniform("u_alpha");
        uMode = shader.getUniform("u_mode");
        uZLimit = shader.getUniform("u_zlimit");
        aNormal = shader.getAttrib("a_normal");
        uLight = shader.getUniform("u_light");
    }
}

package org.oscim.renderer.light;

import org.oscim.renderer.GLShader;
import org.oscim.renderer.ShaderLocations;

public class FogShaderLocations extends ShaderLocations {
    /**
     * The sFog color as uniform.
     */
    int uColor;

    /**
     * The sFog density as uniform.
     */
    int uDensity;

    /**
     * The sFog gradient as uniform.
     */
    int uGradient;

    /**
     * The sFog shift as uniform.
     */
    int uShift;

    public FogShaderLocations() {
        super("#define FOG 1\n");
    }

    @Override
    public void init(GLShader shader) {
        uColor = shader.getUniform("u_fogColor");
        uDensity = shader.getUniform("u_fogDensitiy");
        uGradient = shader.getUniform("u_fogGradient");
        uShift = shader.getUniform("u_fogShift");
    }
}

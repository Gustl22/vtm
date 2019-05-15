package org.oscim.renderer.light;

import org.oscim.renderer.GLShader;
import org.oscim.renderer.ShaderLocations;

public class ShadowShaderLocations extends ShaderLocations {

    /**
     * The light color and shadow transparency as uniform.
     */
    int uLightColor;

    /**
     * The light mvp for shadow as uniform.
     */
    int uLightMvp;

    /**
     * The shadow map texture as uniform.
     */
    int uShadowMap;

    /**
     * The shadow map resolution as uniform.
     */
    int uShadowRes;

    public ShadowShaderLocations() {
        super("#define SHADOW 1\n");
    }

    @Override
    public void init(GLShader shader) {
        uLightColor = shader.getUniform("u_lightColor");
        uLightMvp = shader.getUniform("u_light_mvp");
        uShadowMap = shader.getUniform("u_shadowMap");
        uShadowRes = shader.getUniform("u_shadowRes");
    }
}

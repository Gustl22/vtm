package org.oscim.renderer.extrusion;

import org.oscim.renderer.BasicShader;
import org.oscim.renderer.light.FogShaderLocations;
import org.oscim.renderer.light.ShadowRenderer;

public class ExtrusionShader extends BasicShader {
    private String shader;
    private ExtrusionShaderLocations extrusionShaderLocations;
    private ShadowRenderer.ExtrusionShadowShaderLocations shadowShaderLocations;
    private FogShaderLocations fogShaderLocations;

    public ExtrusionShader(String shader) {
        this.shader = shader;
        extrusionShaderLocations = new ExtrusionShaderLocations();
    }

    public boolean isShadow() {
        return shadowShaderLocations != null;
    }

    public ExtrusionShader setShadow(boolean shadow) {
        if (shadow)
            shadowShaderLocations = new ShadowRenderer.ExtrusionShadowShaderLocations();
        else
            shadowShaderLocations = null;
        return this;
    }

    public boolean isFog() {
        return fogShaderLocations != null;
    }

    public ExtrusionShader setFog(boolean fog) {
        if (fog)
            fogShaderLocations = new FogShaderLocations();
        else
            fogShaderLocations = null;
        return this;
    }

    public ExtrusionShaderLocations getExtrusionShaderLocations() {
        return extrusionShaderLocations;
    }

    public ShadowRenderer.ExtrusionShadowShaderLocations getShadowShaderLocations() {
        return shadowShaderLocations;
    }

    public FogShaderLocations getFogShaderLocations() {
        return fogShaderLocations;
    }

    /**
     * Build the extrusion shader and keep reference to it.
     *
     * @return this as reference.
     */
    public ExtrusionShader build() {
        String directives = (isShadow() ? shadowShaderLocations.getDirective() : "")
                + (isFog() ? fogShaderLocations.getDirective() : "");
        createDirective(shader, directives);
        super.init();
        extrusionShaderLocations.init(this);
        if (isShadow()) shadowShaderLocations.init(this);
        if (isFog()) fogShaderLocations.init(this);
        return this;
    }

}

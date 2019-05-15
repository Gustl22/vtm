package org.oscim.renderer.extrusion;

import org.oscim.renderer.LayerRenderer;
import org.oscim.renderer.ShaderLocations;
import org.oscim.renderer.light.Sun;

public abstract class ExtrusionLayerRenderer<T extends ShaderLocations> extends LayerRenderer {

    /**
     * Hold the superior renderer.
     */
    protected ExtrusionLayerRenderer mRenderer;

    /**
     * Hold the shader locations
     */
    protected T mShaderLocations;

    public ExtrusionLayerRenderer(ExtrusionLayerRenderer mRenderer) {
        this.mRenderer = mRenderer;
    }

    @Override
    public boolean setup() {
        return mRenderer.setup() && super.setup();
    }

    /**
     * @return true if it's a mesh, false if it's an extruded polygon.
     */
    public Sun getSun() {
        return mRenderer.getSun();
    }

    /**
     * @return true if it's a mesh, false if it's an extruded polygon.
     */
    public boolean isMesh() {
        return mRenderer.isMesh();
    }

    /**
     * See {@link org.oscim.layers.tile.buildings.BuildingLayer#TRANSLUCENT}.
     *
     * @return true if it's translucent, elde false.
     */
    public boolean isTranslucent() {
        return mRenderer.isTranslucent();
    }

    public void setRenderer(ExtrusionLayerRenderer renderer) {
        mRenderer = (ExtrusionLayerRenderer) renderer;
    }

    public void setShaderLocations(T shaderLocations) {
        mShaderLocations = shaderLocations;
    }

    public T getShaderLocations() {
        return mShaderLocations;
    }

    public ExtrusionLayerRenderer getRenderer() {
        return mRenderer;
    }

    public ExtrusionRenderer getExtrusionRenderer() {
        return mRenderer.getExtrusionRenderer();
    }

    public boolean isLight() {
        return mRenderer.isLight();
    }

    public void setLight(boolean useLight) {
        mRenderer.setLight(useLight);
    }

    /**
     * @return the main extrusion shader.
     */
    public ExtrusionShader useMainShader() {
        return mRenderer.useMainShader();
    }

    /**
     * @return the main extrusion shader.
     */
    public ExtrusionShader getMainShader() {
        return mRenderer.getMainShader();
    }

    /**
     * @param shader the current shader to be used.
     */
    public void setShader(ExtrusionShader shader) {
        mRenderer.setShader(shader);
    }
}

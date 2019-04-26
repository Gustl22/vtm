package org.oscim.renderer;

import org.oscim.renderer.light.Sun;

public abstract class ExtrusionLayerRenderer extends LayerRenderer {

    /**
     * Hold the superior renderer.
     */
    protected ExtrusionLayerRenderer mRenderer;

    public ExtrusionLayerRenderer(ExtrusionLayerRenderer mRenderer) {
        this.mRenderer = mRenderer;
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

    public ExtrusionLayerRenderer getRenderer() {
        return mRenderer;
    }

    public void useLight(boolean useLight) {
        mRenderer.useLight(useLight);
    }

    public void setShader(ExtrusionRenderer.Shader shader) {
        mRenderer.setShader(shader);
    }
}

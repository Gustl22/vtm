package org.oscim.renderer;

public abstract class BasicShader extends GLShader {
    /**
     * The vertex position as attribute.
     */
    public int aPos;

    /**
     * The model-view-projection matrix as uniform.
     */
    public int uMVP;

    @Override
    public void init() {
        uMVP = getUniform("u_mvp");
        aPos = getAttrib("a_pos");
    }
}

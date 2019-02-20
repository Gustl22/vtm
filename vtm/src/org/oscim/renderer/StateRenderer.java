package org.oscim.renderer;

public abstract class StateRenderer {

    public final GLState mGLState;

    public StateRenderer(GLState glState) {
        mGLState = glState;
    }
}

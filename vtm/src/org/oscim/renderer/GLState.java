/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
 * Copyright 2018-2019 Gustl22
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.renderer;

import org.oscim.backend.GL;
import org.oscim.backend.GLAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.backend.GLAdapter.gl;

public class GLState {
    static final Logger log = LoggerFactory.getLogger(GLState.class);

    public static final int DISABLED = -1;
    public static final int UNBIND = 0;

    private final boolean[] vertexArray = {false, false};
    private boolean blend = false;
    private boolean depth = false;
    private boolean stencil = false;
    private int shader;
    private float[] clearColor;
    private int glVertexBuffer;
    private int glIndexBuffer;

    private int currentFramebufferId;
    private int currentTexId;

    private int viewportWidth;
    private int viewportHeight;

    // Quad state
    public int mQuadIndicesID;
    public int mQuadVerticesID;

    void init() {
        vertexArray[0] = false;
        vertexArray[1] = false;
        blend = false;
        depth = false;
        stencil = false;
        shader = DISABLED;
        currentTexId = DISABLED;
        glVertexBuffer = DISABLED;
        glIndexBuffer = DISABLED;
        clearColor = null;

        gl.disable(GL.STENCIL_TEST);
        gl.disable(GL.DEPTH_TEST);
        gl.disable(GL.BLEND);
    }

    public boolean useProgram(int shaderProgram) {
        if (shaderProgram < 0) {
            shader = DISABLED;
        } else if (shaderProgram != shader) {
            gl.useProgram(shaderProgram);
            shader = shaderProgram;
            return true;
        }
        return false;
    }

    public void blend(boolean enable) {
        if (blend == enable)
            return;

        if (enable)
            gl.enable(GL.BLEND);
        else
            gl.disable(GL.BLEND);
        blend = enable;
    }

    public void testDepth(boolean enable) {
        if (depth != enable) {

            if (enable)
                gl.enable(GL.DEPTH_TEST);
            else
                gl.disable(GL.DEPTH_TEST);

            depth = enable;
        }
    }

    public void test(boolean depthTest, boolean stencilTest) {
        if (depth != depthTest) {

            if (depthTest)
                gl.enable(GL.DEPTH_TEST);
            else
                gl.disable(GL.DEPTH_TEST);

            depth = depthTest;
        }

        if (stencil != stencilTest) {

            if (stencilTest)
                gl.enable(GL.STENCIL_TEST);
            else
                gl.disable(GL.STENCIL_TEST);

            stencil = stencilTest;
        }
    }

    /**
     * Enable or disable vertex arrays.
     * Valid values are
     * -1: {@link #DISABLED},
     * 0: enable first,
     * 1: enable second.
     */
    public void enableVertexArrays(int va1, int va2) {
        if (va1 > 1 || va2 > 1)
            log.debug("FIXME: enableVertexArrays...");

        if ((va1 == 0 || va2 == 0)) {
            if (!vertexArray[0]) {
                gl.enableVertexAttribArray(0);
                vertexArray[0] = true;
            }
        } else {
            if (vertexArray[0]) {
                gl.disableVertexAttribArray(0);
                vertexArray[0] = false;
            }
        }

        if ((va1 == 1 || va2 == 1)) {
            if (!vertexArray[1]) {
                gl.enableVertexAttribArray(1);
                vertexArray[1] = true;
            }
        } else {
            if (vertexArray[1]) {
                gl.disableVertexAttribArray(1);
                vertexArray[1] = false;
            }
        }
    }

    public void bindFramebuffer(int id) {
        gl.bindFramebuffer(GL.FRAMEBUFFER, id);
        currentFramebufferId = id;
    }

    public int getFramebuffer() {
        return currentFramebufferId;
    }

    public void bindTex2D(int id) {
        if (id < 0) {
            gl.bindTexture(GL.TEXTURE_2D, 0);
            currentTexId = 0;
        } else if (currentTexId != id) {
            gl.bindTexture(GL.TEXTURE_2D, id);
            currentTexId = id;
        }
    }

    public int getTexture() {
        return currentTexId;
    }

    public void setClearColor(float[] color) {
        // Workaround for artifacts at canvas resize on desktop
        if (!GLAdapter.GDX_DESKTOP_QUIRKS) {
            if (clearColor != null &&
                    color[0] == clearColor[0] &&
                    color[1] == clearColor[1] &&
                    color[2] == clearColor[2] &&
                    color[3] == clearColor[3])
                return;
        }

        clearColor = color;
        gl.clearColor(color[0], color[1], color[2], color[3]);
    }

    public void bindBuffer(int target, int id) {
        //log.debug(">> buffer {} {}", target == GL20.ARRAY_BUFFER, id);

        if (target == GL.ARRAY_BUFFER) {
            if (glVertexBuffer == id)
                return;
            glVertexBuffer = id;
        } else if (target == GL.ELEMENT_ARRAY_BUFFER) {
            if (glIndexBuffer == id)
                return;
            glIndexBuffer = id;
        } else {
            log.debug("invalid target {}", target);
            return;
        }
        //log.debug("bind buffer {} {}", target == GL20.ARRAY_BUFFER, id);

        if (id >= 0)
            gl.bindBuffer(target, id);
    }

    public void bindElementBuffer(int id) {

        if (glIndexBuffer == id)
            return;
        glIndexBuffer = id;

        if (id >= 0)
            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, id);

    }

    public void bindVertexBuffer(int id) {

        if (glVertexBuffer == id)
            return;
        glVertexBuffer = id;

        if (id >= 0)
            gl.bindBuffer(GL.ARRAY_BUFFER, id);

    }

    /**
     * Bind VBO for a simple quad. Handy for simple custom RenderLayers
     * Vertices: float[]{ -1, -1, -1, 1, 1, -1, 1, 1 }
     * <p/>
     * GL.drawArrays(GL20.TRIANGLE_STRIP, 0, 4);
     */
    public void bindQuadVertexVBO(int location) {

        if (location >= 0) {
            bindVertexBuffer(mQuadVerticesID);
            enableVertexArrays(location, GLState.DISABLED);
            gl.vertexAttribPointer(location, 2, GL.FLOAT, false, 0, 0);
        }
    }

    /**
     * Bind indices for rendering up to MAX_QUADS (512),
     * ie. MAX_INDICES (512*6) in one draw call.
     * Vertex order is 0-1-2 2-1-3
     */
    public void bindQuadIndicesVBO() {
        bindElementBuffer(mQuadIndicesID);
    }

    public void viewport(int width, int height) {
        gl.viewport(0, 0, width, height);
        viewportWidth = width;
        viewportHeight = height;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }
}

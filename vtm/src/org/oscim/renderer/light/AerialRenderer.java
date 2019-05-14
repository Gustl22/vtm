/*
 * Copyright 2019 Gustl22
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
package org.oscim.renderer.light;

import org.oscim.backend.GL;
import org.oscim.renderer.BasicShader;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.LayerRenderer;
import org.oscim.renderer.MapRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;

import static org.oscim.backend.GLAdapter.gl;

public class AerialRenderer extends LayerRenderer {
    private static final Logger log = LoggerFactory.getLogger(AerialRenderer.class);

    public static boolean DEBUG = false;

    Fog mFog;
    protected int mGroundQuad;

    /**
     * Shader to draw the ground.
     */
    private GroundShader mGroundShader;

    public static class GroundShader extends BasicShader {
        Fog.ShaderLocations sFog = new Fog.ShaderLocations();

        public GroundShader(String shader) {
            createDirective(shader, "#define FOG 1\n");
        }

        @Override
        public void init() {
            super.init();
            sFog.initLocations(this);
        }
    }

    public AerialRenderer(Fog fog) {
        mFog = fog;
    }

    /**
     * Bind a plane to easily draw all over the ground.
     */
    private static int bindPlane(float width, float height) {
        int vertexBuffer;
        int[] vboIds = GLUtils.glGenBuffers(1);
        FloatBuffer floatBuffer = MapRenderer.getFloatBuffer(8);
        // indices:  0 1 2 - 2 1 3
        float[] quad = new float[]{
                -width, height,
                width, height,
                -width, -height,
                width, -height
        };
        floatBuffer.put(quad);
        floatBuffer.flip();
        vertexBuffer = vboIds[0];
        GLState.bindVertexBuffer(vertexBuffer);
        gl.bufferData(GL.ARRAY_BUFFER,
                quad.length * 4, floatBuffer,
                GL.STATIC_DRAW);
        GLState.bindVertexBuffer(GLState.UNBIND);
        return vertexBuffer;
    }

    @Override
    public boolean setup() {
        // Ground plane for sFog

        mGroundQuad = bindPlane(Short.MAX_VALUE, Short.MAX_VALUE);

        // Shader
        mGroundShader = new GroundShader("aerial_ground");

        //mRenderer.setup(); // No need to setup, as shaders are taken from here

        return super.setup();
    }

    @Override
    public void update(GLViewport viewport) {
        setReady(true);
    }

    @Override
    public void render(GLViewport viewport) {

        // DRAW SCENE
        GLState.test(false, false);
//        gl.depthFunc(GL.ALWAYS);
        gl.clear(GL.DEPTH_BUFFER_BIT);


        // Draw GROUND
        {
            mGroundShader.useProgram();
            viewport.mvp.copy(viewport.viewproj);
            viewport.mvp.setAsUniform(mGroundShader.uMVP);

            GLUtils.setColor(mGroundShader.sFog.uColor, mFog.getColor());
            gl.uniform1f(mGroundShader.sFog.uDensity, mFog.getDensity());
            gl.uniform1f(mGroundShader.sFog.uGradient, mFog.getGradient());
            gl.uniform1f(mGroundShader.sFog.uShift, mFog.getShift());

            // Bind VBO
            GLState.bindVertexBuffer(mGroundQuad);
            GLState.enableVertexArrays(mGroundShader.aPos, GLState.DISABLED);
            gl.vertexAttribPointer(mGroundShader.aPos, 2, GL.FLOAT, false, 0, 0);
            MapRenderer.bindQuadIndicesVBO();
            GLState.blend(true);  // allow transparency
//            gl.blendFunc(GL.ZERO, GL.SRC_COLOR); // multiply frame colors
            gl.drawElements(GL.TRIANGLES, 6, GL.UNSIGNED_SHORT, 0);
            GLState.blend(false);
//            gl.blendFunc(GL.ONE, GL.ONE_MINUS_SRC_ALPHA); // Reset to default func
        }
    }
}

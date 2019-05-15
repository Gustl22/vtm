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
import org.oscim.backend.canvas.Color;
import org.oscim.renderer.BasicShader;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.LayerRenderer;
import org.oscim.renderer.MapRenderer;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.utils.math.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;

import static org.oscim.backend.GLAdapter.gl;

public class AerialRenderer extends LayerRenderer {
    private static final Logger log = LoggerFactory.getLogger(AerialRenderer.class);

    public static boolean DEBUG = false;
    public static final int CIRCLE_COUNT = 32;
    public static final int HORIZON_COLOR = Color.get(211, 230, 255);

    Fog mFog;
    protected int mGroundQuad;
    protected int mSkyCylinder;
    int texW = -1;

    /**
     * Shader to draw the ground.
     */
    private GroundShader mGroundShader;
    private SkyShader mSkyShader;

    public static class GroundShader extends BasicShader {
        FogShaderLocations sFog = new FogShaderLocations();

        public GroundShader(String shader) {
            createDirective(shader, sFog.getDirective());
        }

        @Override
        public void init() {
            super.init();
            sFog.init(this);
        }
    }

    public static class SkyShader extends BasicShader {
        /**
         * The sky color as uniform.
         */
        int uSkyColor;

        /**
         * The horizon color as uniform.
         */
        int uHorizonColor;

        public SkyShader(String shader) {
            create(shader);
        }

        @Override
        public void init() {
            super.init();
            uSkyColor = getUniform("u_skyColor");
            uHorizonColor = getUniform("u_horizonColor");
        }
    }


    public AerialRenderer(Fog fog) {
        mFog = fog;
    }

    /**
     * Bind a plane to easily draw all over the ground.
     */
    private static int bindPlane(float x, float y) {
        int vertexBuffer;
        int[] vboIds = GLUtils.glGenBuffers(1);
        // indices:  0 1 2 - 2 1 3
        float[] quad = new float[]{
                -x, y, 0,
                x, y, 0,
                -x, -y, 0,
                x, -y, 0};

        FloatBuffer floatBuffer = MapRenderer.getFloatBuffer(quad.length);
        floatBuffer.put(quad);
        floatBuffer.flip();
        vertexBuffer = vboIds[0];
        GLState.bindVertexBuffer(vertexBuffer);
        gl.bufferData(GL.ARRAY_BUFFER,
                quad.length * RenderBuckets.FLOAT_BYTES, floatBuffer,
                GL.STATIC_DRAW);
        GLState.bindVertexBuffer(GLState.UNBIND);
        return vertexBuffer;
    }

    private static int bindCylinder(float radius) {
        int vertexBuffer;
        int[] vboIds = GLUtils.glGenBuffers(1);
        // indices:  0 1 2 - 2 1 3
        float[] cylinder;
        // 2 VERTICES per TRIANGLE_STRIP + 2 VERTICES at end * 3 coords
        cylinder = new float[(CIRCLE_COUNT * 2 + 2) * 3];

        int count = 0;

        for (int i = 0; i < CIRCLE_COUNT + 1; i++) {
            final double angle = ((double) i / CIRCLE_COUNT) * MathUtils.PI2;
            float x = (float) Math.cos(angle) * radius;
            float y = (float) Math.sin(angle) * radius;

            cylinder[count++] = x;
            cylinder[count++] = y;
            cylinder[count++] = radius;

            cylinder[count++] = x;
            cylinder[count++] = y;
            cylinder[count++] = 0; // TODO may move to another renderer and can make it -radius
        }

        FloatBuffer floatBuffer = MapRenderer.getFloatBuffer(cylinder.length);
        floatBuffer.put(cylinder);
        floatBuffer.flip();
        vertexBuffer = vboIds[0];
        GLState.bindVertexBuffer(vertexBuffer);
        gl.bufferData(GL.ARRAY_BUFFER,
                cylinder.length * RenderBuckets.FLOAT_BYTES, floatBuffer,
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
        mSkyShader = new SkyShader("aerial_sky");

        //mRenderer.setup(); // No need to setup, as shaders are taken from here

        return super.setup();
    }

    @Override
    public void update(GLViewport viewport) {
        setReady(true);
        if (!viewport.changed())
            return;

        setupFBO(viewport);
    }

    private void setupFBO(GLViewport viewport) {
        if (texW != viewport.getWidth()) {
            // Apothem calculated from the far plane radius and count:
            // https://en.wikipedia.org/wiki/Regular_polygon#Circumradius
            texW = (int) viewport.getWidth();
            mSkyCylinder = bindCylinder(texW * 9.5f / 2); // Some weird clip value
        }
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
            gl.vertexAttribPointer(mGroundShader.aPos, 3, GL.FLOAT, false, 0, 0);
            MapRenderer.bindQuadIndicesVBO();
            GLState.blend(true);  // allow transparency
//            gl.blendFunc(GL.ZERO, GL.SRC_COLOR); // multiply frame colors
            gl.drawElements(GL.TRIANGLES, 6, GL.UNSIGNED_SHORT, 0);
            GLState.blend(false);
//            gl.blendFunc(GL.ONE, GL.ONE_MINUS_SRC_ALPHA); // Reset to default func
        }

        // Draw Sky
        {
            mSkyShader.useProgram();
            viewport.mvp.copy(viewport.viewproj);
            viewport.mvp.setAsUniform(mSkyShader.uMVP);

            GLUtils.setColor(mSkyShader.uSkyColor, mFog.getColor());
            GLUtils.setColor(mSkyShader.uHorizonColor, HORIZON_COLOR);

            // Bind VBO
            GLState.bindVertexBuffer(mSkyCylinder);
            GLState.enableVertexArrays(mSkyShader.aPos, GLState.DISABLED);
            gl.vertexAttribPointer(mSkyShader.aPos, 3, GL.FLOAT, false, 0, 0);
            //MapRenderer.bindQuadIndicesVBO();
            GLState.blend(true);  // allow transparency
//            gl.blendFunc(GL.ZERO, GL.SRC_COLOR); // multiply frame colors
            gl.drawArrays(GL.TRIANGLE_STRIP, 0, CIRCLE_COUNT * 2 + 2);
//            gl.drawElements(GL.TRIANGLE_STRIP, 12, GL.UNSIGNED_SHORT, 0);
            GLState.blend(false);
//            gl.blendFunc(GL.ONE, GL.ONE_MINUS_SRC_ALPHA); // Reset to default func
        }
    }
}

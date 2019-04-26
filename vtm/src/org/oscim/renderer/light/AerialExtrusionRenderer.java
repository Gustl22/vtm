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
import org.oscim.renderer.ExtrusionLayerRenderer;
import org.oscim.renderer.ExtrusionRenderer;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.backend.GLAdapter.gl;

public class AerialExtrusionRenderer extends ExtrusionLayerRenderer {
    private static final Logger log = LoggerFactory.getLogger(AerialExtrusionRenderer.class);

    public static boolean DEBUG = false;

    /**
     * Shader to draw the extrusions.
     */
    private Shader mExtrusionShader;

    private Fog mFog;

    public static class Shader extends ExtrusionRenderer.Shader {
        Fog.Shader fog = new Fog.Shader();

        public Shader(String shader) {
            super(shader, "#define FOG 1\n");
            fog.uColor = getUniform("u_fogColor");
            fog.uDensity = getUniform("u_fogDensitiy");
            fog.uGradient = getUniform("u_fogGradient");
            fog.uShift = getUniform("u_fogShift");
        }
    }

    public AerialExtrusionRenderer(ExtrusionLayerRenderer renderer, Fog fog) {
        super(renderer);
        mFog = fog;
    }

    @Override
    public boolean setup() {
        if (isMesh())
            mExtrusionShader = new Shader("extrusion_layer_mesh");
        else
            mExtrusionShader = new Shader("extrusion_layer_ext");

        //mRenderer.setup(); // No need to setup, as shaders are taken from here

        return super.setup();
    }

    @Override
    public void update(GLViewport viewport) {
        mRenderer.update(viewport);
        setReady(mRenderer.isReady());
    }

    @Override
    public void render(GLViewport viewport) {

        // DRAW SCENE
        GLState.test(false, false);
//        gl.depthFunc(GL.ALWAYS);
        gl.clear(GL.DEPTH_BUFFER_BIT);

        // Draw EXTRUSIONS (in ExtrusionRenderer)
        {
            mExtrusionShader.useProgram();

            GLUtils.setColor(mExtrusionShader.fog.uColor, mFog.getColor());
            gl.uniform1f(mExtrusionShader.fog.uDensity, mFog.getDensity());
            gl.uniform1f(mExtrusionShader.fog.uGradient, mFog.getGradient());
            gl.uniform1f(mExtrusionShader.fog.uShift, mFog.getShift());

            mRenderer.setShader(mExtrusionShader);
            mRenderer.useLight(true);
            mRenderer.render(viewport);
            //mRenderer.setShader(tmpShader);
        }
    }
}

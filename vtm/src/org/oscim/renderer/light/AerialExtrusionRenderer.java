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
import org.oscim.renderer.extrusion.ExtrusionLayerRenderer;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.extrusion.ExtrusionShader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.oscim.backend.GLAdapter.gl;

public class AerialExtrusionRenderer extends ExtrusionLayerRenderer<FogShaderLocations> {
    private static final Logger log = LoggerFactory.getLogger(AerialExtrusionRenderer.class);

    public static boolean DEBUG = false;

    private Fog mFog;

    public AerialExtrusionRenderer(ExtrusionLayerRenderer renderer, Fog fog) {
        super(renderer);
        mFog = fog;
        ExtrusionShader extrusionShader = getMainShader().setFog(true);
        setShaderLocations(extrusionShader.getFogShaderLocations());
    }

    @Override
    public void update(GLViewport viewport) {
        mRenderer.update(viewport);
        setReady(mRenderer.isReady());
    }

    @Override
    public void render(GLViewport viewport) {

        if (isLight()) {
            // DRAW SCENE
            GLState.test(false, false);
//        gl.depthFunc(GL.ALWAYS);
            gl.clear(GL.DEPTH_BUFFER_BIT);

            // Draw EXTRUSIONS (in ExtrusionRenderer)
            {
                useMainShader();

                GLUtils.setColor(getShaderLocations().uColor, mFog.getColor());
                gl.uniform1f(getShaderLocations().uDensity, mFog.getDensity());
                gl.uniform1f(getShaderLocations().uGradient, mFog.getGradient());
                gl.uniform1f(getShaderLocations().uShift, mFog.getShift());

                mRenderer.render(viewport);
                //mRenderer.setShader(tmpShader);
            }
        } else
            mRenderer.render(viewport);
    }
}

/*
 * Copyright 2012 Hannes Janetzek
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
package org.oscim.layers;

import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.map.Map;
import org.oscim.renderer.extrusion.ExtrusionLayerRenderer;
import org.oscim.renderer.extrusion.ExtrusionRenderer;
import org.oscim.renderer.light.AerialExtrusionRenderer;
import org.oscim.renderer.light.AerialRenderer;
import org.oscim.renderer.light.Fog;

public class AerialLayer extends Layer {

    public AerialLayer(Map map) {
        this(map, null);
    }

    /**
     * @param map           the map.
     * @param buildingLayer the extrusion layer to apply fog.
     */
    public AerialLayer(Map map, BuildingLayer buildingLayer) {
        this(map, buildingLayer, new Fog());
    }

    public AerialLayer(Map map, BuildingLayer buildingLayer, Fog fog) {
        super(map);
        if (buildingLayer != null) {
            // Include aerial renderer to BuildingLayer, too
            ExtrusionLayerRenderer prev = null;
            ExtrusionLayerRenderer renderer = buildingLayer.getRenderer();
            while (!(renderer instanceof ExtrusionRenderer)) {
                prev = renderer;
                renderer = renderer.getRenderer();
            }
            AerialExtrusionRenderer aerialExtrusionRenderer = new AerialExtrusionRenderer(renderer, fog);
            if (prev == null)
                buildingLayer.setRenderer(aerialExtrusionRenderer);
            else
                prev.setRenderer(aerialExtrusionRenderer);
        }
        mRenderer = new AerialRenderer(fog);
    }
}

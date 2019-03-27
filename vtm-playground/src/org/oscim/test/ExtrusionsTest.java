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
package org.oscim.test;

import org.oscim.core.MapElement;
import org.oscim.core.Tag;
import org.oscim.core.Tile;
import org.oscim.gdx.GdxMapApp;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.test.tiling.source.TestTileSource;
import org.oscim.theme.VtmThemes;

public class ExtrusionsTest extends GdxMapApp {

    enum GroundShape {
        HEXAGON, RECTANGLE, SHAPE_L, SHAPE_M, SHAPE_O, SHAPE_T, SHAPE_U, SHAPE_V, SHAPE_X, SHAPE_Z, TEST
    }

    /**
     * Iterate through ground or roof shapes.
     * 0: default ground and roof
     * 1: default ground, all roofs
     * 2: default roof, all grounds
     * 3: iterate all
     */
    private static final int MODE = 4;

    // Default ground shape
    public GroundShape mGroundShape = GroundShape.SHAPE_L;

    // Default roof shape
    public Tag mRoofShape = new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_SALTBOX);

    private Tag[] mRoofShapes = {
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_FLAT),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_SKILLION),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_PYRAMIDAL),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_HIPPED),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_GABLED),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_GAMBREL),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_MANSARD),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_SALTBOX),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_ROUND),
    };

    private Tag[] mRoundRoofShapes = {
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_PYRAMIDAL),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_ONION),
            new Tag(Tag.KEY_ROOF_SHAPE, Tag.VALUE_DOME)
    };

    private Tag[] mTags = {
            new Tag(Tag.KEY_BUILDING, Tag.VALUE_YES),
            new Tag(Tag.KEY_BUILDING_LEVELS, "1"),
            new Tag(Tag.KEY_ROOF_LEVELS, "1"),
            new Tag(Tag.KEY_ROOF_COLOR, "red"),
            new Tag(Tag.KEY_BUILDING_COLOR, "white")
    };

    private Tag[] mTagsRound = {
            new Tag(Tag.KEY_BUILDING, Tag.VALUE_YES),
            new Tag(Tag.KEY_BUILDING_LEVELS, "1"),
            new Tag(Tag.KEY_ROOF_COLOR, "red"),
            new Tag(Tag.KEY_BUILDING_COLOR, "white")
    };

    private void addExtrusions(TestTileSource tileSource) {
        Tag[] roofShapes;
        GroundShape[] groundShapes;
        if (MODE == 0) {
            roofShapes = new Tag[]{mRoofShape};
            groundShapes = new GroundShape[]{mGroundShape};
        } else if (MODE == 1 || MODE == 4) {
            roofShapes = mRoofShapes;
            groundShapes = new GroundShape[]{mGroundShape};
        } else if (MODE == 2) {
            roofShapes = new Tag[]{mRoofShape};
            groundShapes = GroundShape.values();
        } else {
            roofShapes = mRoofShapes;
            groundShapes = GroundShape.values();
        }
        int x = 0, y = 0;
        for (GroundShape ground : groundShapes) {
            MapElement e = new MapElement();
            e.startPolygon();
            applyGroundShape(ground, e);
            e.tags.set(mTags);
            e.tags.add(new Tag(Tag.KEY_ROOF_LEVELS, "1"));

            MapElement building;
            for (Tag roofShape : roofShapes) {
                building = new MapElement(e);
                building = building.translate(x, y);
                building.tags.add(roofShape);
                tileSource.addMapElement(building);

                x += 64;
                if (x >= Tile.SIZE / 3) {
                    y += 32;
                    x = 0;
                    if (y >= Tile.SIZE) {
                        x = 32;
                        y = 32;
                    }
                }
            }
        }

        if (MODE == 4) {
            MapElement e = new MapElement();
            e.startPolygon();
            applyGroundShape(GroundShape.HEXAGON, e);
            e.tags.set(mTagsRound);

            for (Tag roofShape : mRoundRoofShapes) {
                MapElement building = new MapElement(e);
                building = building.translate(x, y);
                building.tags.add(roofShape);
                if (roofShape.value.equals(Tag.VALUE_ONION))
                    building.tags.add(new Tag(Tag.KEY_ROOF_LEVELS, "2"));
                else
                    building.tags.add(new Tag(Tag.KEY_ROOF_LEVELS, "1"));


                tileSource.addMapElement(building);

                x += 64;
                if (x >= Tile.SIZE / 3) {
                    y += 32;
                    x = 0;
                    if (y >= Tile.SIZE) {
                        x = 32;
                        y = 32;
                    }
                }
            }
        }
    }

    private void applyGroundShape(GroundShape shape, MapElement e) {
        switch (shape) {
            case HEXAGON:
                hexagonGround(e);
                break;
            case RECTANGLE:
                rectangleGround(e);
                break;
            case SHAPE_L:
                shapeLGround(e);
                break;
            case SHAPE_M:
                shapeMGround(e);
                break;
            case SHAPE_O:
                shapeOGround(e);
                break;
            case SHAPE_T:
                shapeTGround(e);
                break;
            case SHAPE_U:
                shapeUGround(e);
                break;
            case SHAPE_V:
                shapeVGround(e);
                break;
            case SHAPE_X:
                shapeXGround(e);
                break;
            case SHAPE_Z:
                shapeZGround(e);
                break;
            case TEST:
                testGround(e);
                break;
        }
    }

    private void hexagonGround(MapElement e) {
        hexagonGround(e, 4, 0, 0);
    }

    private void hexagonGround(MapElement e, float unit, float shiftX, float shiftY) {
        float sqrt2 = unit * (float) Math.sqrt(2);

        e.addPoint(shiftX + unit, shiftY + 0);
        e.addPoint(shiftX + unit + sqrt2, shiftY + 0);
        e.addPoint(shiftX + 2 * unit + sqrt2, shiftY + unit);
        e.addPoint(shiftX + 2 * unit + sqrt2, shiftY + unit + sqrt2);
        e.addPoint(shiftX + unit + sqrt2, shiftY + 2 * unit + sqrt2);
        e.addPoint(shiftX + unit, shiftY + 2 * unit + sqrt2);
        e.addPoint(shiftX + 0, shiftY + unit + sqrt2);
        e.addPoint(shiftX + 0, shiftY + unit);
    }

    private void rectangleGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(25, 0);
        e.addPoint(25, 20);
        e.addPoint(0, 20);
    }

    private void shapeLGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(12, 0);
        e.addPoint(20, 0);
        e.addPoint(20, 15);
        e.addPoint(12, 15);
        e.addPoint(12, 10);
        e.addPoint(0, 10);
    }

    private void shapeL2Ground(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(12, 0);
        e.addPoint(40, 0);
        e.addPoint(40, 10);
        e.addPoint(30, 10);
        e.addPoint(30, 20);
        e.addPoint(0, 20);
    }

    private void shapeMGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(10, 0);
        e.addPoint(10, 5);
        e.addPoint(20, 5);
        e.addPoint(20, 20);
        e.addPoint(37, 20);
        e.addPoint(37, 25);
        e.addPoint(12, 25);
        e.addPoint(12, 15);
        e.addPoint(0, 15);
    }

    private void shapeOGround(MapElement e) {
        hexagonGround(e);
        e.reverse();
        e.startHole();
        hexagonGround(e, 5, 5, 5);
        e.reverse();
    }

    private void shapeTGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(10, 0);
        e.addPoint(20, 0);
        e.addPoint(30, 0);
        e.addPoint(30, 15);
        e.addPoint(20, 15);
        e.addPoint(20, 30);
        e.addPoint(10, 30);
        e.addPoint(10, 10);
        e.addPoint(0, 10);
    }

    private void shapeUGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(5, 0);
        e.addPoint(5, 10);
        e.addPoint(20, 10);
        e.addPoint(20, 0);
        e.addPoint(30, 0);
        e.addPoint(30, 20);
        e.addPoint(0, 20);
    }

    private void shapeVGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(5, 0);
        e.addPoint(15, 15);
        e.addPoint(20, 0);
        e.addPoint(30, 0);
        e.addPoint(20, 25);
        e.addPoint(15, 25);
    }

    private void shapeXGround(MapElement e) {
        e.addPoint(0, 10);
        e.addPoint(10, 10);
        e.addPoint(10, 0);
        e.addPoint(20, 0);
        e.addPoint(20, 15);
        e.addPoint(30, 15);
        e.addPoint(30, 25);
        e.addPoint(20, 25);
        e.addPoint(20, 30);
        e.addPoint(10, 30);
        e.addPoint(10, 20);
        e.addPoint(0, 20);
    }

    private void shapeZGround(MapElement e) {
        e.addPoint(0, 0);
        e.addPoint(10, 0);
        e.addPoint(10, 5);
        e.addPoint(20, 5);
        e.addPoint(20, 20);
        e.addPoint(12, 20);
        e.addPoint(12, 15);
        e.addPoint(0, 15);
    }

    private void testGround(MapElement e) {
        float x = 100;
        e.addPoint(208.56958f, -35.20929f + x);
        e.addPoint(224.00461f, -34.291678f + x);
        e.addPoint(223.84058f, -22.546248f + x);
        e.addPoint(259.84075f, -20.068697f + x);
        e.addPoint(266.2832f, -12.131365f + x);
        e.addPoint(265.2542f, -1.9688354f + x);

        e.addPoint(257.90205f, 2.7109718f + x);
        e.addPoint(239.30544f, 1.7933628f + x);
        e.addPoint(238.21677f, 14.295781f + x);
        e.addPoint(211.1197f, 13.14877f + x);
        e.addPoint(211.20918f, 11.2676735f + x);

        e.addPoint(165.70938f, 8.170745f + x);
        e.addPoint(168.27443f, -22.087442f + x);
        e.addPoint(207.63005f, -19.242848f + x);
    }

    @Override
    public void createLayers() {
        TestTileSource tts = new TestTileSource();

        addExtrusions(tts);

        VectorTileLayer vtl = mMap.setBaseMap(tts);
        BuildingLayer buildingLayer = new S3DBLayer(mMap, vtl, true);
        buildingLayer.getExtrusionRenderer().getSun().setProgress(0.1f);
        buildingLayer.getExtrusionRenderer().getSun().updatePosition();

        mMap.layers().add(buildingLayer);
//        mMap.layers().add(new TileGridLayer(mMap));

        mMap.setTheme(VtmThemes.DEFAULT);

        mMap.setMapPosition(0, 0, 1 << 17);
    }

    public static void main(String[] args) {
        GdxMapApp.init();
        GdxMapApp.run(new ExtrusionsTest());
    }
}

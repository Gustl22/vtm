/*
 * Copyright 2014 Hannes Janetzek
 * Copyright 2016-2018 devemux86
 * Copyright 2017 Longri
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
package org.oscim.android.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.oscim.android.filepicker.FilePicker;
import org.oscim.android.filepicker.FilterByFileExtension;
import org.oscim.android.filepicker.ValidMapFile;
import org.oscim.android.filepicker.ValidRenderTheme;
import org.oscim.core.MapElement;
import org.oscim.core.Tag;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.tile.MapTile;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.bucket.RenderBuckets;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.ImperialUnitAdapter;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.scalebar.MetricUnitAdapter;
import org.oscim.theme.ExternalRenderTheme;
import org.oscim.theme.ThemeUtils;
import org.oscim.theme.VtmThemes;
import org.oscim.theme.styles.AreaStyle;
import org.oscim.theme.styles.RenderStyle;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MapInfo;

public class MapsforgeMapActivity extends MapActivity {

    static final int SELECT_MAP_FILE = 0;
    static final int SELECT_THEME_FILE = SELECT_MAP_FILE + 1;

    private static final Tag ISSEA_TAG = new Tag("natural", "issea");
    private static final Tag NOSEA_TAG = new Tag("natural", "nosea");
    private static final Tag SEA_TAG = new Tag("natural", "sea");

    private TileGridLayer mGridLayer;
    private LabelLayer mLabelLayer;
    private DefaultMapScaleBar mMapScaleBar;
    private Menu mMenu;
    private boolean mS3db;
    private S3DBLayer mS3DBLayer = null;
    private VectorTileLayer mTileLayer;
    MapFileTileSource mTileSource;

    public MapsforgeMapActivity() {
        this(false);
    }

    public MapsforgeMapActivity(boolean s3db) {
        super();
        mS3db = s3db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivityForResult(new Intent(this, MapFilePicker.class),
                SELECT_MAP_FILE);
    }

    @Override
    protected void onDestroy() {
        if (mMapScaleBar != null)
            mMapScaleBar.destroy();

        super.onDestroy();
    }

    public static class MapFilePicker extends FilePicker {
        public MapFilePicker() {
            setFileDisplayFilter(new FilterByFileExtension(".map"));
            setFileSelectFilter(new ValidMapFile());
        }
    }

    public static class ThemeFilePicker extends FilePicker {
        public ThemeFilePicker() {
            setFileDisplayFilter(new FilterByFileExtension(".xml"));
            setFileSelectFilter(new ValidRenderTheme());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.theme_menu, menu);
        mMenu = menu;
        mMenu.findItem(R.id.labellayer).setVisible(true);
        mMenu.findItem(R.id.labellayer).setChecked(true);

        if (mS3db) {
            MenuItem renderColors = mMenu.findItem(R.id.rendercolors);
            renderColors.setVisible(true);
            renderColors.setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.theme_default:
                mMap.setTheme(VtmThemes.DEFAULT);
                item.setChecked(true);
                return true;

            case R.id.theme_osmarender:
                mMap.setTheme(VtmThemes.OSMARENDER);
                item.setChecked(true);
                return true;

            case R.id.theme_osmagray:
                mMap.setTheme(VtmThemes.OSMAGRAY);
                item.setChecked(true);
                return true;

            case R.id.theme_tubes:
                mMap.setTheme(VtmThemes.TRONRENDER);
                item.setChecked(true);
                return true;

            case R.id.theme_newtron:
                mMap.setTheme(VtmThemes.NEWTRON);
                item.setChecked(true);
                return true;

            case R.id.theme_external:
                startActivityForResult(new Intent(this, ThemeFilePicker.class),
                        SELECT_THEME_FILE);
                return true;

            case R.id.gridlayer:
                if (item.isChecked()) {
                    item.setChecked(false);
                    mMap.layers().remove(mGridLayer);
                } else {
                    item.setChecked(true);
                    if (mGridLayer == null)
                        mGridLayer = new TileGridLayer(mMap, getResources().getDisplayMetrics().density);

                    mMap.layers().add(mGridLayer);
                }
                mMap.updateMap(true);
                return true;
            case R.id.labellayer:
                if (item.isChecked()) {
                    item.setChecked(false);
                    mMap.layers().remove(mLabelLayer);
                } else {
                    item.setChecked(true);
                    if (mLabelLayer == null)
                        mLabelLayer = new LabelLayer(mMap, mTileLayer);

                    if (!mMap.layers().contains(mLabelLayer)) {
                        mMap.layers().add(mLabelLayer);
                    }
                }
                mMap.updateMap(true);
                return true;
            case R.id.rendercolors:
                MenuItem renderColors = mMenu.findItem(R.id.rendercolors);
                boolean checked = renderColors.isChecked();
                mS3DBLayer.setColored(!checked);
                renderColors.setChecked(!checked);
                mMap.clearMap();
                mMap.updateMap(true);
                return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == SELECT_MAP_FILE) {
            if (resultCode != RESULT_OK || intent == null || intent.getStringExtra(FilePicker.SELECTED_FILE) == null) {
                finish();
                return;
            }

            mTileSource = new MapFileTileSource();
            //mTileSource.setPreferredLanguage("en");
            String file = intent.getStringExtra(FilePicker.SELECTED_FILE);
            if (mTileSource.setMapFile(file)) {

                mTileLayer = mMap.setBaseMap(mTileSource);
                loadTheme(null);

                if (mS3db) {
                    BuildingLayer.POST_AA = true;
                    mS3DBLayer = new S3DBLayer(mMap, mTileLayer);
                    mMap.layers().add(mS3DBLayer);
                } else
                    mMap.layers().add(new BuildingLayer(mMap, mTileLayer));
                mLabelLayer = new LabelLayer(mMap, mTileLayer);
                mMap.layers().add(mLabelLayer);

                mMapScaleBar = new DefaultMapScaleBar(mMap);
                mMapScaleBar.setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.BOTH);
                mMapScaleBar.setDistanceUnitAdapter(MetricUnitAdapter.INSTANCE);
                mMapScaleBar.setSecondaryDistanceUnitAdapter(ImperialUnitAdapter.INSTANCE);
                mMapScaleBar.setScaleBarPosition(MapScaleBar.ScaleBarPosition.BOTTOM_LEFT);

                MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mMap, mMapScaleBar);
                BitmapRenderer renderer = mapScaleBarLayer.getRenderer();
                renderer.setPosition(GLViewport.Position.BOTTOM_LEFT);
                renderer.setOffset(5 * getResources().getDisplayMetrics().density, 0);
                mMap.layers().add(mapScaleBarLayer);

                MapInfo info = mTileSource.getMapInfo();
//                MapPosition pos = new MapPosition();
////                pos.setByBoundingBox(info.boundingBox, Tile.SIZE * 4, Tile.SIZE * 4);
//                pos.setZoomLevel(18);
////                pos.setPosition(48.13876, 11.57908); // Munich
//                pos.setPosition(48.48905, 11.13986); // Home
//                mMap.setMapPosition(pos);

//                mPrefs.clear();
            }
        } else if (requestCode == SELECT_THEME_FILE) {
            if (resultCode != RESULT_OK || intent == null || intent.getStringExtra(FilePicker.SELECTED_FILE) == null) {
                return;
            }

            String file = intent.getStringExtra(FilePicker.SELECTED_FILE);
            ExternalRenderTheme externalRenderTheme = new ExternalRenderTheme(file);

            // Use tessellation with sea and land for Mapsforge themes
            if (ThemeUtils.isMapsforgeTheme(externalRenderTheme)) {
                mTileLayer.addHook(new VectorTileLayer.TileLoaderThemeHook() {
                    @Override
                    public boolean process(MapTile tile, RenderBuckets buckets, MapElement element, RenderStyle style, int level) {
                        if (element.tags.contains(ISSEA_TAG) || element.tags.contains(SEA_TAG) || element.tags.contains(NOSEA_TAG)) {
                            if (style instanceof AreaStyle)
                                ((AreaStyle) style).mesh = true;
                        }
                        return false;
                    }

                    @Override
                    public void complete(MapTile tile, boolean success) {
                    }
                });
            }

            mMap.setTheme(externalRenderTheme);
            mMenu.findItem(R.id.theme_external).setChecked(true);
        }
    }

    protected void loadTheme(final String styleId) {
        mMap.setTheme(VtmThemes.DEFAULT);
    }
}

/**
 * @author DRTN
 * Team Website with download:
 * https://misterseph.github.io/DuckRelatedFractalProject/
 *
 * This Class contains either modifications or is entirely new in Assessment 3
 *
 * If you are in any doubt a complete changelog can be found here:
 * https://github.com/NotKieran/DRTN-Fractal/compare/Fractal_Initial...development
 *
 * And a more concise report can be found in our Change3 document.
 **/

package io.github.teamfractal.util;

import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import io.github.teamfractal.entity.LandPlot;

import java.util.Random;

public class PlotManager {
    public int x;
    public int y;
    private LandPlot[][] plots;
	private TiledMapTileSets tiles;
	private TiledMapTileLayer mapLayer;
	private TiledMapTileLayer playerOverlay;
	private TiledMapTileLayer roboticonOverlay;
	private TiledMapTile cityTile;
	private TiledMapTile waterTile;
	private TiledMapTile forestTile;
	private TiledMapTile hillTile1;
	private TiledMapTile hillTile2;
	private TiledMapTile hillTile3;
	private TiledMapTile hillTile4;

	public PlotManager() {

	}

	/**
	 * Set up the plot manager.
	 * @param tiles    Tiles.
	 * @param layers   Layers.
	 */
	public void setup(TiledMapTileSets tiles, MapLayers layers) {
		this.tiles = tiles;
		this.mapLayer = (TiledMapTileLayer)layers.get("MapData");
		this.playerOverlay = (TiledMapTileLayer)layers.get("PlayerOverlay");
		this.roboticonOverlay = (TiledMapTileLayer)layers.get("RoboticonOverlay");

		this.cityTile = tiles.getTile(60);
		this.waterTile = tiles.getTile(9);
		this.forestTile = tiles.getTile(61);
		this.hillTile1 = tiles.getTile(4);
		this.hillTile2 = tiles.getTile(5);
		this.hillTile3 = tiles.getTile(6);
		this.hillTile4 = tiles.getTile(7);

        this.x = mapLayer.getWidth();

        this.y = mapLayer.getHeight();

        this.plots = new LandPlot[x][y];

    }

	/**
	 * Get {@link LandPlot} at specific position.
	 * @param x   The x index.
	 * @param y   The y index.
	 * @return    The corresponding {@link LandPlot} object.
	 */
	public LandPlot getPlot(int x, int y) {
        if (x < 0 || x >= this.x || y < 0 || y >= this.y)
            return null;

		// Lazy load
		LandPlot p = this.plots[x][y];
		if (p == null) {
			p = createLandPlot(x, y);
		}

		return p;
	}

    /**
     * Function to return a random number in range
     *
     * @return random int value between min and max
     */
    private int randomResourceVal() {
        Random rand = new Random();
        int max = 6;
        int min = 0;
        return rand.nextInt(max) + min;

    }

    /**
     * Creates a landplot from a tile tile on the tiled map
	 * @param x - x coordinate on tiled map
	 * @param y - y coordinate on tiled map
	 * @return new Landplot with statistics determined by tile on tiled map
	 */
	private LandPlot createLandPlot(int x, int y) {
		int ore, energy, food;
		TiledMapTile tile = mapLayer.getCell(x, y).getTile();

        if (tile == cityTile) {
            ore = 1 + this.randomResourceVal();
            energy = 2 + this.randomResourceVal();
            food = 3 + this.randomResourceVal();
        } else if (tile == forestTile) {
            ore = 2 + this.randomResourceVal();
            energy = 3 + this.randomResourceVal();
            food = 1 + this.randomResourceVal();
        } else if (tile == waterTile) {
            ore = 3 + this.randomResourceVal();
            energy = 1 + this.randomResourceVal();
            food = 2 + this.randomResourceVal();
        } else if (tile == hillTile1 || tile == hillTile2 || tile == hillTile3 || tile == hillTile4) {
            ore = 3 + this.randomResourceVal();
            energy = 2 + this.randomResourceVal();
            food = 1 + this.randomResourceVal();
        } else {
            ore = 2 + this.randomResourceVal();
            energy = 2 + this.randomResourceVal();
            food = 2 + this.randomResourceVal();
        }


		LandPlot p = new LandPlot(ore, energy, food);
		p.setupTile(this, x, y);
		this.plots[x][y] = p;
		return p;
	}

	public TiledMapTileLayer getMapLayer() {
		return mapLayer;
	}

	public TiledMapTileLayer getPlayerOverlay() {
		return playerOverlay;
	}

	public TiledMapTileLayer getRoboticonOverlay() {
		return roboticonOverlay;
	}

	public LandPlot[][] getLandPlots(){
		return this.plots;
	}

}
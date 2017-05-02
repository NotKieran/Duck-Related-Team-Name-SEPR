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

package io.github.teamfractal.entity;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.exception.InvalidResourceTypeException;
import io.github.teamfractal.exception.NotCommonResourceException;
import io.github.teamfractal.util.PlotManager;

public class LandPlot {
    private final int IndexOre = 0;
    private final int IndexEnergy = 1;
    private final int IndexFood = 2;
    int x, y;
    /**
     * Saved modifiers for LandPlot.
     * [ Ore, Energy, Food ]
     */
    float[] productionModifiers = {0, 0, 0};
    private TiledMapTileLayer.Cell mapTile;
	private TiledMapTileLayer.Cell playerTile;
	private TiledMapTileLayer.Cell roboticonTile;
	private Player owner;
    /**
     * The base production amounts.
     * [ Ore, Energy, Food ]
     */
    private int[] productionAmounts;
    private boolean owned;
    private Roboticon installedRoboticon;
    private boolean hasRoboticon;

    /**
     * Initialise LandPlot with specific base amount of resources.
     *
     * @param ore    Amount of ore
     * @param energy Amount of energy
     * @param food   Amount of food
     */
    public LandPlot(int ore, int energy, int food) {
        this.productionAmounts = new int[]{ore, energy, food};
        this.owned = false;
    }

    //</editor-fold>

	//<editor-fold desc="Class getters">
	public TiledMapTileLayer.Cell getMapTile() {
		return mapTile;
	}

	public TiledMapTileLayer.Cell getPlayerTile() {
		return playerTile;
	}

	public TiledMapTileLayer.Cell getRoboticonTile() {
		return roboticonTile;
	}

	public Player getOwner() {
		return owner;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	/**
	 * Sets the owner of the land plot to the specified player
	 * @param player The player to be set as owner
	 * @return Returns true if the land plot didn't already have an owner, false if it did
	 */
	public boolean setOwner(Player player) {
		if (hasOwner()) {
			return false;
		}

		owner = player;
		player.addLandPlot(this);
		return true;
	}
	/**
	 * Returns the state of the land plots ownership
	 * @return True if owned, false otherwise
	 */
	public boolean hasOwner() {
		return getOwner() != null;
	}
	/**
	 * Removes the owner of the tile
	 */
	public void removeOwner() {
		if (!hasOwner())
			return ;

		owner.removeLandPlot(this);
	}
	/**
	 * Retrieves the overlays for the specific tile
	 * @param plotManager The plotmanager storing the images of the current mao
	 * @param x The x coordinate of the tile
	 * @param y The y coordinate if the tile
	 */
	public void setupTile (PlotManager plotManager, int x, int y) {
		this.x = x;
		this.y = y;
		this.mapTile = plotManager.getMapLayer().getCell(x, y);
		this.playerTile = plotManager.getPlayerOverlay().getCell(x, y);
		this.roboticonTile = plotManager.getRoboticonOverlay().getCell(x, y);
	}

	/**
	 * Get the type index from the {@link ResourceType}
	 * @param resource   The {@link ResourceType}
	 * @return           The index.
	 * @throws InvalidResourceTypeException Exception is thrown if the resource index is invalid.
	 */
	private int resourceTypeToIndex(ResourceType resource) {
		switch (resource) {
			case ORE:    return IndexOre;
			case FOOD:   return IndexFood;
			case ENERGY: return IndexEnergy;
		}

		throw new NotCommonResourceException(resource);
	}

	/**
	 * Install a roboticon to this LandPlot.
	 *
	 * @param roboticon    The roboticon to be installed.
	 */
	public synchronized boolean installRoboticon(Roboticon roboticon) {
		// Check if supplied roboticon is already installed.
		if (roboticon.isInstalled()) {
			return false;
		}

		if (roboticon.getCustomisation() != ResourceType.Unknown){
			int index = resourceTypeToIndex(roboticon.getCustomisation());
			if (roboticon.setInstalledLandplot(this)) {
				productionModifiers[index] += 1;
				this.installedRoboticon = roboticon;
				return true;
			}
		}
		else{
			if (roboticon.setInstalledLandplot(this)) {
				this.installedRoboticon = roboticon;
				return true;
			}
		}

		return false;
	}

	/**
	 * Calculate the amount of resources to be produced.
	 *
	 * @return The amount of resources to be produced in an 2D array.
	 */
    public int[] produceResources() {
        int[] produced = new int[3];
        for (int i = 0; i < 2; i++) {
            produced[i] = (int) ((float) productionAmounts[i] * productionModifiers[i]);
        }
		return produced;
	}

	/**
	 * Calculate the amount of resources to be produced for specific resource.
	 * @param resource  The resource type to be calculated.
	 * @return          Calculated amount of resource to be generated.
	 */
	public int produceResource(ResourceType resource) {
		if (this.hasRoboticon){
			int resIndex = resourceTypeToIndex(resource);
			return (int) ((float) productionAmounts[resIndex] * productionModifiers[resIndex]);
		}
		else return 0;
		
	}
	/**
	 * Gets the index of the specific resource
	 * @param resource The resource selected
	 * @return The index of the resource
	 */
	public int getResource(ResourceType resource) {
		int resIndex = resourceTypeToIndex(resource);
		return productionAmounts[resIndex];
	}
	/**
	 * Checks if the tile contains a roboticon
	 * @return True if the tile contains a roboticon, false otherwise
	 */
	public boolean hasRoboticon(){
		return this.hasRoboticon;
	}
	/**
	 * Setter for hasRoboticon
	 * @param roboticonInstalled The boolean that hasRoboticon is to be changed to
	 */
	public void setHasRoboticon(boolean roboticonInstalled){
		this.hasRoboticon = roboticonInstalled;
	}


}
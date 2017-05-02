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

import io.github.teamfractal.entity.enums.ResourceType;

public class Roboticon {
	private int ID;
	private ResourceType customisation;
	private LandPlot installedLandPlot;
	
	Roboticon(int ID) {
		this.ID = ID;
		customisation = ResourceType.Unknown;
	}
	/**
	 * Getter for the roboticon ID
	 * @return The roboticon ID
	 */
	public int getID () {
		return this.ID;
	}
	/**
	 * Getter for the customisation
	 * @return The customisation of the roboticon
	 */
	public ResourceType getCustomisation() {
		return this.customisation;
	}
	/**
	 * Sets the customisation of the roboticon to the specific type
	 * @param type The type of customisation
	 */
	void setCustomisation(ResourceType type) {
		this.customisation = type;
	}
	/**
	 * Getter for installedLandPlot
	 * @return The state of installedLandplot, true if installed and false if otherwise
	 */
	public synchronized boolean isInstalled() {
		return installedLandPlot != null;
	}
	/**
	 *
	 * @param landplot which roboticon is installed to
	 * @return true if roboticon is installed, false if not
	 */
	public synchronized boolean setInstalledLandplot(LandPlot landplot) {
		if (!isInstalled()) {
			installedLandPlot = landplot;
			return true;
		}

		System.out.println("Already installed to LandPlot! Cancel.");
		return false;
	}
}

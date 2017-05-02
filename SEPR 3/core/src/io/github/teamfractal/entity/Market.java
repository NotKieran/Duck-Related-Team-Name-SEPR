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
import io.github.teamfractal.exception.InvalidResourceTypeException;
import io.github.teamfractal.exception.NotCommonResourceException;

import java.util.Random;

public class Market {
	//<editor-fold desc="Resource getters and setters">
	private int food;
	private int energy;
	private int ore;
	private int roboticon;
	/**
	 * Initialise the market
	 */
	public Market() {
		setFood(16);
		setEnergy(16);
		setOre(0);
		setRoboticon(12);
	}

	/**
	 * Get the amount of food in the market
	 * @return The amount of food in the market.
	 */
	int getFood() {
		return food;
	}

	/**
	 * Set the amount of food in the market
	 *
	 * @param amount The amount of new food amount.
	 * @throws IllegalArgumentException If the new amount if negative, this exception will be thrown.
	 */
	synchronized void setFood(int amount) throws IllegalArgumentException {
		if (amount < 0) {
			throw new IllegalArgumentException("Error: Food can't be negative.");
		}

		this.food = amount;
	}

	/**
	 * Get the amount of energy in the market
	 * @return The amount of energy in the market.
	 */
	int getEnergy() {
		return energy;
	}

	/**
	 * Set the amount of energy in the market
	 *
	 * @param amount The amount of new energy count.
	 * @throws IllegalArgumentException If the new amount if negative, this exception will be thrown.
	 */
	synchronized void setEnergy(int amount) throws IllegalArgumentException {
		if (amount < 0) {
			throw new IllegalArgumentException("Error: Energy can't be negative.");
		}

		this.energy = amount;
	}

	/**
	 * Get the amount of ore in the market
	 * @return The amount of ore in the market.
	 */
	int getOre() {
		return ore;
	}

	/**
	 * Set the amount of ore in the market
	 * @param amount                     The amount of new ore count.
	 * @throws IllegalArgumentException  If the new amount if negative, this exception will be thrown.
	 */
	synchronized void setOre(int amount) throws IllegalArgumentException {
		if (amount < 0) {
			throw new IllegalArgumentException("Error: Ore can't be negative.");
		}

		this.ore = amount;
	}

	/**
	 * Get the amount of roboticon in the market
	 * @return The amount of roboticon in the market.
	 */
	int getRoboticon() {
		return roboticon;
	}

	/**
	 * Set the amount of roboticon in the market
	 * @param amount                     The amount of new roboticon count.
	 * @throws IllegalArgumentException  If the new amount if negative, this exception will be thrown.
	 */
	void setRoboticon(int amount) throws IllegalArgumentException {
		if (amount < 0) {
			throw new IllegalArgumentException("Error: Roboticon can't be negative.");
		}

		roboticon = amount;
	}

	/**
	 * Get the total amount of all available resources added together.
	 *
	 * @return The total amount.
	 */
	private synchronized int getTotalResourceCount() {
		return food + energy + ore + roboticon;
	}
	//</editor-fold>

	/**
	 * Get the amount of specific resource.
	 * @param type   The {@link ResourceType}.
	 * @return       The amount.
	 */
	public int getResource(ResourceType type) {
		switch (type) {
			case ORE:
				return getOre();

			case ENERGY:
				return getEnergy();

			case ROBOTICON:
				return getRoboticon();

			case FOOD:
				return getFood();
				
			case CUSTOMISATION:
				return 1000;

			default:
				throw new NotCommonResourceException(type);
		}
	}



	/**
	 * Set the amount of specific resource.
	 * @param type   The {@link ResourceType}.
	 * @param amount The new resource amount.
	 * @throws IllegalArgumentException      Will be thrown if the new amount is negative.
	 * @throws InvalidResourceTypeException  Will be thrown if the resource specified is invalid.
	 */
	void setResource(ResourceType type, int amount)
			throws IllegalArgumentException, InvalidResourceTypeException {

		switch (type) {
			case ORE:
				setOre(amount);
				break;

			case ENERGY:
				setEnergy(amount);
				break;

			case ROBOTICON:
				setRoboticon(amount);
				break;

			case FOOD:
				setFood(amount);
				break;
				
			case CUSTOMISATION:
				break;

			default:
				throw new NotCommonResourceException(type);
		}

	}

	/**
	 * Method to ensure the market have enough resources for user to purchase.
	 * @param type    The {@link ResourceType}.
	 * @param amount  the amount of resource to check.
	 * @return        If there are enough resources.
	 * @throws InvalidResourceTypeException  Will be thrown if the resource specified is invalid.
	 */
	boolean hasEnoughResources(ResourceType type, int amount)
			throws InvalidResourceTypeException {
		int resource = getResource(type);
		return amount <= resource;
	}

	/**
	 * Get the single price for a resource type.
	 * @param resource   The {@link ResourceType}.
	 * @return           The buy in price.
	 */
	public int getBuyPrice(ResourceType resource) {
		int buyPrice = (int) (getSellPrice(resource) * 0.6f);
		if (buyPrice < 5) {
			buyPrice = 5;
			return buyPrice;
		}
		else {
			return buyPrice;
		}

	}

	/**
	 * Calculates Sell price.
	 * @param resource	The {@link ResourceType}.
	 * @return			Returns new calculated Sell price value.
	 */
	private int calcSellPrice(ResourceType resource) {
		int sellPrice;
		if (getResource(resource) == 0) {
			sellPrice = 50;
			return sellPrice;
		}
		else {
			sellPrice = (50 / (getResource(resource) + 1));
			if (sellPrice < 10) {
				sellPrice = 10;
			}

			return sellPrice;
		}
	}

	/**
	 * Get the single price for a resource type.
	 * @param resource   The {@link ResourceType}.
	 * @return           The sell price.
	 */

	public int getSellPrice(ResourceType resource) { // some changes was made
		int price;
		switch (resource) {
			case ORE:
				price = calcSellPrice(ResourceType.ORE);
				return price;

			case ENERGY:
				price = calcSellPrice(ResourceType.ENERGY);
				return price;

			case FOOD:
				price = calcSellPrice(ResourceType.FOOD);
				return price;

			case ROBOTICON:
				price = calcSellPrice(ResourceType.ROBOTICON);
				return price;

			case CUSTOMISATION:
				price = 10;
				return price;

			default:
				throw new IllegalArgumentException("Error: Resource type is incorrect.");
		}
	}

	/**
	 * Buy Resource from the market, caller <i>must</i> be doing all the checks.
	 * For example, take money away from the player.
	 *
	 * This method will only increase the amount of specified resource.
	 *
	 * @param resource    The {@link ResourceType}
	 * @param amount      The amount of resource to buy in.
	 */
	public synchronized void buyResource(ResourceType resource, int amount){
		setResource(resource, getResource(resource) + amount);
	}

	/**
	 * Sell Resource from the market, caller <i>must</i> be doing all the checks.
	 * For example, add money in to the player.
	 *
	 * This method will only decrease the amount of specified resource.
	 *
	 * @param resource    The {@link ResourceType}
	 * @param amount      The amount of resource to sell out.
	 */
	public synchronized void sellResource(ResourceType resource, int amount) {
		setResource(resource, getResource(resource) - amount);
	}

	/**
	 * Generates a random amount of roboticons within a given range if the market contains ore.
	 */
	public void generateRoboticon(){
		Random rand = new Random();
		int roboticonsToGenerate = rand.nextInt(3) + 0;
		while(this.ore >= 2 && roboticonsToGenerate > 0){
			this.ore -= 2;
			this.roboticon += 1;
			roboticonsToGenerate -= 1;
		}
	}
}









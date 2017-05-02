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

import io.github.teamfractal.TesterFile;
import io.github.teamfractal.entity.enums.ResourceType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MarketTest extends TesterFile {
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private Market market;

	/**
	 * Reset market to its default status.
	 */
	@Before
	public void Contractor() {
		market = new Market();
	}

	/**
	 * test start mo
	 * The market should start with correct amount of resources.
	 * 16 Food and Energy, 0 Ore, 12 Robotics
	 */
	@Test
	public void marketShouldInitWithCorrectValues() {
		assertEquals(16, market.getFood());
		assertEquals(16, market.getEnergy());
		assertEquals(0, market.getOre());
		assertEquals(12, market.getRoboticon());
	}

	/**
	 * test setEnergy(), setOre(), setFood(), setRoboticon()
	 * The market should be able to set and get resources.
	 */
	@Test
	public void marketShouldAbleToGetAndSetResources() {
		Random rnd = new Random();
		int valueToTest = rnd.nextInt(100);
		market.setEnergy(valueToTest);
		market.setOre(valueToTest);
		market.setFood(valueToTest);
		market.setRoboticon(valueToTest);


		assertEquals(valueToTest, market.getEnergy());
		assertEquals(valueToTest, market.getOre());
		assertEquals(valueToTest, market.getFood());
		assertEquals(valueToTest, market.getRoboticon());
	}

	/**
	 * test: getBuyPrice()
	 * The market should start with correct price for player to buy.
	 * The price is 90% of the sell price.
	 * This could change in later development.
	 */
	@Test
	public void marketShouldHaveCorrectPricesForResources() throws Exception {
		assertEquals(30, market.getBuyPrice(ResourceType.ORE));
		assertEquals(6, market.getBuyPrice(ResourceType.ENERGY));
		assertEquals(6, market.getBuyPrice(ResourceType.FOOD));
		assertEquals(6, market.getBuyPrice(ResourceType.ROBOTICON));
	}


	/**
	 * test: hasEnoughResources
	 * player class can use this method to find out that the amount of resource
	 * player want to buy is available in the market, if the amount of resource
	 * in the market is less than the amount of resources player want to buy then
	 * throw exception
	 */

	@Test
	public void marketCanCheckResourceMoreThanAmountYouWantToBuy() {
		assertFalse(market.hasEnoughResources(ResourceType.ORE, 1000000));
		assertFalse(market.hasEnoughResources(ResourceType.ENERGY, 1000000));
		assertFalse(market.hasEnoughResources(ResourceType.ROBOTICON, 1000000));
		assertFalse(market.hasEnoughResources(ResourceType.FOOD, 1000000));
	}


	/**
	 * test: getSellPrice()
	 */

	@Test
	public void marketShouldReturnCorrectSellPrice(){
		int valueToTest1 = 20;
		market.setEnergy(valueToTest1);
		market.setOre(valueToTest1);
		market.setFood(valueToTest1);
		market.setRoboticon(valueToTest1);

		assertEquals(10,market.getSellPrice(ResourceType.FOOD));
		assertEquals(10,market.getSellPrice(ResourceType.ORE));
		assertEquals(10,market.getSellPrice(ResourceType.ROBOTICON));
		assertEquals(10,market.getSellPrice(ResourceType.ENERGY));
	}

	@Test
	public void marketShouldReduceResourcesWhenSells(){
		market.setEnergy(10);
		market.setOre(10);
		market.setFood(10);
		market.setRoboticon(10);

		market.sellResource(ResourceType.FOOD, 5);
		market.sellResource(ResourceType.ORE, 5);
		market.sellResource(ResourceType.ENERGY, 5);
		market.sellResource(ResourceType.ROBOTICON, 5);

		assertEquals(5, market.getFood() );
		assertEquals(5, market.getOre() );
		assertEquals(5, market.getEnergy() );
		assertEquals(5, market.getRoboticon() );

	}
	@Test
	public void marketShouldUseOreToCreateRoboticons(){
		market.setRoboticon(10);
		market.setOre(10);
		market.generateRoboticon();

		int roboticons = market.getRoboticon();

		assertEquals(10 - ((roboticons - 10)* 2),market.getOre());
	}
}

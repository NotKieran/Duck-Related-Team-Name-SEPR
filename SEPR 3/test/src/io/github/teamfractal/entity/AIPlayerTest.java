package io.github.teamfractal.entity;

import com.badlogic.gdx.utils.Array;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.TesterFile;
import io.github.teamfractal.entity.enums.ResourceType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the AI can be found here and in the Manual Testing part of the Test3 document
 * https://github.com/NotKieran/DRTN-Fractal/blob/Assessment3_Docs/Test3.pdf
 */

public class AIPlayerTest extends TesterFile {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private Player player;
    private RoboticonQuest game = new RoboticonQuest();

    @Before
    public void setUp() {
        //game.create();
        player = new AIPlayer(game);
    }

    /**
     * This part of the Test Class is a duplicate of PlayerTest but calling an AI player instead, this checks that the AI inherits correctly
     */
    //Money Tests
    @Test
    public void testPlayerInitialMoney() {
        assertEquals(20000, player.getMoney());
    }

    /**
     * Test to purchase and sell resource from the market.
     */
    @Test
    public void testAIPlayerBuyResource() {
        Market market = new Market();
        market.setOre(16);
        player.setMoney(1000);


        int playerMoney = player.getMoney();
        int orePrice = market.getSellPrice(ResourceType.ORE);
        //Purchase 5 ore
        player.purchaseResourceFromMarket(5, market, ResourceType.ORE);
        // Player should now have 5 more ores, and the market have 5 less ores.
        assertEquals(playerMoney - 5 * orePrice, player.getMoney());
        assertEquals(5, player.getOre());
        assertEquals(11, market.getOre());


        playerMoney = player.getMoney();
        int energyPrice = market.getSellPrice(ResourceType.ENERGY);
        //purchase 10 energy
        player.purchaseResourceFromMarket(10, market, ResourceType.ENERGY);
        assertEquals(playerMoney - 10 * energyPrice, player.getMoney());
        assertEquals(10, player.getEnergy());
        assertEquals(6, market.getEnergy());
    }

    @Test
    public void testAIPlayerSellResource() throws Exception {
        Market market = new Market();

        player.setMoney(1000);
        player.setResource(ResourceType.ORE, 15);
        player.setResource(ResourceType.ENERGY, 15);


        int orePrice = market.getBuyPrice(ResourceType.ORE);
        //sell 5 ore
        player.sellResourceToMarket(5, market, ResourceType.ORE);
        assertEquals(1000 + 5 * orePrice, player.getMoney());
        assertEquals(10, player.getOre());
        assertEquals(5, market.getOre());

        int energyPrice = market.getBuyPrice(ResourceType.ENERGY);
        player.setMoney(1000);
        //sell 5 energy
        player.sellResourceToMarket(5, market, ResourceType.ENERGY);
        assertEquals(1000 + 5 * energyPrice, player.getMoney());
        assertEquals(10, player.getEnergy());
        assertEquals(21, market.getEnergy());
    }

    @Test
    public void testAIPlayerCannotBuyMoreThanAllowed() throws Exception {
        Market market = new Market();
        // Attempt to purchase more ore than allowed
        try {
            player.purchaseResourceFromMarket(100, market, ResourceType.ORE);
        } catch (Exception exception1) {
            assertEquals(100, player.getMoney());
            assertEquals(0, player.getOre());
            // Attempt to purchase more energy than allowed
            try {
                player.purchaseResourceFromMarket(100, market, ResourceType.ENERGY);
            } catch (Exception exception2) {
                assertEquals(100, player.getMoney());
                assertEquals(0, player.getEnergy());
            }
        }
    }

    @Test
    public void testAIPlayerCannotSellMoreEnergyThanAllowed() {
        Market market = new Market();

        player.setEnergy(15);
        player.sellResourceToMarket(20, market, ResourceType.ENERGY);
        Assert.assertEquals(15, player.getEnergy());

    }

    @Test
    public void testAIPlayerCannotSellMoreOreThanAllowed() {
        Market market = new Market();

        player.setOre(15);
        player.sellResourceToMarket(20, market, ResourceType.ORE);
        Assert.assertEquals(15, player.getOre());
    }

    @Test
    public void testAIPlayerCannotSellMoreFoodThanAllowed() {
        Market market = new Market();

        player.setFood(15);
        player.sellResourceToMarket(20, market, ResourceType.FOOD);
        Assert.assertEquals(15, player.getFood());
    }

    @Test
    public void testAIPlayerCanCustomiseRoboticon() {
        // Setup
        Roboticon roboticon = new Roboticon(1);
        player.customiseRoboticon(roboticon, ResourceType.ORE);
        assertEquals(ResourceType.ORE, roboticon.getCustomisation());

        Roboticon roboticon2 = new Roboticon(2);
        player.customiseRoboticon(roboticon2, ResourceType.ENERGY);
        assertEquals(ResourceType.ENERGY, roboticon2.getCustomisation());
    }

    @Test
    public void testAIPlayerCanCustomiseOwnedRoboticons() {
        Roboticon roboticon3 = new Roboticon(3);
        Roboticon roboticon4 = new Roboticon(4);
        player.roboticonList = new Array<Roboticon>();
        player.roboticonList.add(roboticon3);
        player.roboticonList.add(roboticon4);
        player.customiseRoboticon(player.roboticonList.get(0), ResourceType.ORE);
        player.customiseRoboticon(player.roboticonList.get(1), ResourceType.ENERGY);
        assertEquals(ResourceType.ORE, player.roboticonList.get(0).getCustomisation());
        assertEquals(ResourceType.ENERGY, player.roboticonList.get(1).getCustomisation());
    }


}

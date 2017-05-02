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
import org.junit.Test;

import static org.junit.Assert.*;

public class LandPlotTest extends TesterFile {
	private LandPlot plot;
	
	@Before
	public void setup() {
		plot = new LandPlot(3, 0, 0);
	}
	
	@Test
	public void testInstallRobiticon() throws Exception {
		Roboticon roboticon = new Roboticon(0);
		int[] intProductionModifiers = new int[3];

		roboticon.setCustomisation(ResourceType.ORE);
		assertTrue(plot.installRoboticon(roboticon));

		for (int i = 0; i < 3; i++) {
			intProductionModifiers[i] = (int) plot.productionModifiers[i];
		}
		assertArrayEquals(new int[]{1, 0, 0}, intProductionModifiers);


		Roboticon roboticon2 = new Roboticon(0);
		roboticon2.setCustomisation(ResourceType.ENERGY);
		assertTrue(plot.installRoboticon(roboticon2));
		for (int i = 0; i < 3; i++) {
			intProductionModifiers[i] = (int) plot.productionModifiers[i];
		}
		assertArrayEquals(new int[] {1, 1, 0},intProductionModifiers);

		Roboticon roboticon3= new Roboticon(0);
		roboticon3.setCustomisation(ResourceType.ORE);
		assertTrue(plot.installRoboticon(roboticon3));
		for (int i = 0; i < 3; i++) {
			intProductionModifiers[i] = (int) plot.productionModifiers[i];
		}
		assertArrayEquals(new int[] {2, 1, 0}, intProductionModifiers);

		Roboticon roboticon4= new Roboticon(0);
		roboticon4.setCustomisation(ResourceType.FOOD);
		assertTrue(plot.installRoboticon(roboticon4));
		for (int i = 0; i < 3; i++) {
			intProductionModifiers[i] = (int) plot.productionModifiers[i];
		}
		assertArrayEquals(new int[] {2, 1, 1}, intProductionModifiers);
	}

	@Test
	public void landPlotShouldNotReinstallRoboticon () {
		Roboticon roboticon = new Roboticon(0);
		int[] intProductionModifiers = new int[3];

		roboticon.setCustomisation(ResourceType.ORE);
		assertTrue(plot.installRoboticon(roboticon));
		for (int i = 0; i < 3; i++) {
			intProductionModifiers[i] = (int) plot.productionModifiers[i];
		}
		assertArrayEquals(new int[] {1, 0, 0}, intProductionModifiers);

		assertFalse(plot.installRoboticon(roboticon));
		for (int i = 0; i < 3; i++) {
			intProductionModifiers[i] = (int) plot.productionModifiers[i];
		}
		assertArrayEquals(new int[] {1, 0, 0}, intProductionModifiers);
	}
	
	@Test
	public void testProduceResources() throws Exception {
		Roboticon roboticon = new Roboticon(0);
		roboticon.setCustomisation(ResourceType.ORE);
		plot.installRoboticon(roboticon);
		assertArrayEquals(new int[] {3, 0, 0}, plot.produceResources());
		Roboticon roboticon2 = new Roboticon(0);
		roboticon2.setCustomisation(ResourceType.ORE);
		plot.installRoboticon(roboticon2);
		assertArrayEquals(new int[] {6, 0, 0}, plot.produceResources());
	}

}

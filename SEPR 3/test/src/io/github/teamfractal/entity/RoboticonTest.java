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

public class RoboticonTest extends TesterFile {
	private Roboticon roboticon;
	
	@Before
	public void setup() {
		roboticon = new Roboticon(1);
	}
	
	@Test
	public void initialisationTest(){
		assertEquals(roboticon.getCustomisation(), ResourceType.Unknown);
		assertFalse(roboticon.isInstalled());
	}
	
	@Test
	public void customisationTest(){
		roboticon.setCustomisation(ResourceType.ORE);
		assertEquals(roboticon.getCustomisation(), ResourceType.ORE);
	}
	
	@Test
	public void installationTest(){
		LandPlot plot = new LandPlot(0, 0, 0);
		plot.installRoboticon(roboticon);
		assertTrue(roboticon.isInstalled());
	}

	

}

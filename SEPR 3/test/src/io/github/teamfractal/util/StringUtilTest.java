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

import io.github.teamfractal.TesterFile;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link StringUtil}
 */
public class StringUtilTest extends TesterFile {
	@Test
	public void capitaliseShouldBeDoneProperly(){
		assertEquals("Java", StringUtil.Capitalise("java"));
		assertEquals("JAVA", StringUtil.Capitalise("jAVA"));
		assertEquals("J", StringUtil.Capitalise("j"));
	}
}

package org.fagu.fmv.textprogressbar.part;

import static org.junit.Assert.assertEquals;

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;
import org.junit.Test;


/**
 * @author fagu
 */
public class PercentPartTest {

	@Test
	public void testOK() {
		Part part = new PercentPart();
		assertPart(part, - 1, "-1%");
		assertPart(part, 0, "0%");
		assertPart(part, 3, "3%");
		assertPart(part, 9, "9%");
		assertPart(part, 10, "10%");
		assertPart(part, 50, "50%");
		assertPart(part, 99, "99%");
		assertPart(part, 100, "100%");
		assertPart(part, 115, "115%");
	}

	// *******************************

	/**
	 * @param part
	 * @param value
	 * @param expectedText
	 */
	private void assertPart(Part part, int value, String expectedText) {
		assertEquals(part.getWith(new ProgressStatus() {

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public int getPercent() {
				return value;
			}
		}), expectedText);
	}

}

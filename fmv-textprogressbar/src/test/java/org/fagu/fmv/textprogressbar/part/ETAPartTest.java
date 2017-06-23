package org.fagu.fmv.textprogressbar.part;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;
import org.junit.Test;


/**
 * @author fagu
 */
public class ETAPartTest {

	@Test
	public void testNotNull() {
		AtomicInteger value = new AtomicInteger();

		Part part = new ETAPart(s -> value.get());
		assertPart(part, value, 0, "");
		assertPart(part, value, 3, "00:03 ETA");
		assertPart(part, value, 9, "00:09 ETA");
		assertPart(part, value, 10, "00:10 ETA");
		assertPart(part, value, 59, "00:59 ETA");
		assertPart(part, value, 60, "01:00 ETA");
		assertPart(part, value, 61, "01:01 ETA");
		assertPart(part, value, 100, "01:40 ETA");
		assertPart(part, value, 115, "01:55 ETA");
	}

	// *******************************

	/**
	 * @param part
	 * @param valueToSet
	 * @param seconds
	 * @param expectedText
	 */
	private void assertPart(Part part, AtomicInteger valueToSet, int seconds, String expectedText) {
		valueToSet.set(seconds);
		assertEquals(part.getWith(new ProgressStatus() {

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public int getPercent() {
				return 0;
			}
		}), expectedText);
	}

}

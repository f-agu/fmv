package org.fagu.fmv.ffmpeg.soft;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import org.fagu.version.Version;
import org.junit.Test;


/**
 * @author Oodrive
 * @author f.agu
 * @created 24 janv. 2017 12:36:47
 */
public class BuildMappingTestCase {

	/**
	 * 
	 */
	public BuildMappingTestCase() {}

	/**
	 * 
	 */
	@Test
	public void testDate() {
		assertEquals(LocalDate.of(2011, 6, 23), BuildMapping.versionToLocalDate(new Version(0, 0, 0)));
		assertEquals(date(2011, 6, 23), BuildMapping.versionToDate(new Version(0, 0, 0)));
	}

	// ********************************************

	/**
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	private Date date(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

}

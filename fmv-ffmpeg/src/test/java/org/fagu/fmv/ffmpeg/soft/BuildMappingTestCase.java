package org.fagu.fmv.ffmpeg.soft;

/*-
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
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

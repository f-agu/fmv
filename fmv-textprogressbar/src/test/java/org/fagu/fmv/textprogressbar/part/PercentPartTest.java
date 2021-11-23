package org.fagu.fmv.textprogressbar.part;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*-
 * #%L
 * fmv-textprogressbar
 * %%
 * Copyright (C) 2014 - 2020 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;
import org.junit.jupiter.api.Test;


/**
 * @author fagu
 */
class PercentPartTest {

	@Test
	void testOK() {
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

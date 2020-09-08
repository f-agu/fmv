package org.fagu.fmv.textprogressbar.part;

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

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class SpendTimePart implements Part {

	private final long startTime = System.currentTimeMillis();

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		StringBuilder buf = new StringBuilder();
		long endTime = System.currentTimeMillis();
		long diffSec = (endTime - startTime) / 1000;
		int seconds = (int)(diffSec % 60L);
		int minutes = (int)(diffSec / 60L);
		buf.append(StringUtils.leftPad(Integer.toString(minutes), 4)).append(':');
		buf.append(StringUtils.leftPad(Integer.toString(seconds), 2, '0'));
		return buf.toString();
	}

}

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

import java.util.OptionalLong;

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class SpinnerPart implements Part {

	private static final char[] CHARS = {'-', '\\', '|', '/'};

	private static final int NB_CHARS = CHARS.length;

	private static final String BLANK = " ";

	private volatile int counter;

	private final OptionalLong refresh;

	public SpinnerPart() {
		this(1);
	}

	public SpinnerPart(double numberOfTurnsPerSecond) {
		refresh = OptionalLong.of((long)(1000D / (numberOfTurnsPerSecond * NB_CHARS)));
	}

	@Override
	public String getWith(ProgressStatus status) {
		if(status.isFinished()) {
			return BLANK;
		}
		return Character.toString(CHARS[counter++ % NB_CHARS]);
	}

	@Override
	public OptionalLong getRefreshInMilliseconds() {
		return refresh;
	}

}

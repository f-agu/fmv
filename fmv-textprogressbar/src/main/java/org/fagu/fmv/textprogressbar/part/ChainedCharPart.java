package org.fagu.fmv.textprogressbar.part;

import java.util.Objects;

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
public class ChainedCharPart implements Part {

	private static final String BLANK = " ";

	private volatile int counter;

	private final char[] chars;

	private final OptionalLong refresh;

	public ChainedCharPart(char[] chars) {
		this(chars, 1);
	}

	public ChainedCharPart(char[] chars, double numberOfTurnsPerSecond) {
		this(chars, numberOfTurnsPerSecond, null);
	}

	public ChainedCharPart(char[] chars, double numberOfTurnsPerSecond, Integer startCounter) {
		this.chars = Objects.requireNonNull(chars);
		refresh = OptionalLong.of((long)(1000D / (numberOfTurnsPerSecond * chars.length)));
		this.counter = startCounter != null ? startCounter % chars.length : 0;
	}

	@Override
	public String getWith(ProgressStatus status) {
		if(status.isFinished()) {
			return BLANK;
		}
		return Character.toString(chars[counter++ % chars.length]);
	}

	@Override
	public OptionalLong getRefreshInMilliseconds() {
		return refresh;
	}

	public static Part fadeIn() {
		return new ChainedCharPart(new char[] {' ', '░', '▒', '▓', '█'});
	}

	public static Part fadeOut() {
		return new ChainedCharPart(new char[] {'█', '▓', '▒', '░', ' '});
	}

	public static Part fadeInOut() {
		return new ChainedCharPart(new char[] {' ', '░', '▒', '▓', '█', '▓', '▒', '░'});
	}

}

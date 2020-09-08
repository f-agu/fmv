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

import java.util.Objects;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;


/**
 * @author fagu
 */
public class FixWidthPart implements Part {

	public static final BiFunction<String, Integer, String> LEFT_PAD = StringUtils::leftPad;

	public static final BiFunction<String, Integer, String> RIGHT_PAD = StringUtils::rightPad;

	public static final BiFunction<String, Integer, String> CENTER_PAD = StringUtils::center;

	private final Part part;

	private final int width;

	private final BiFunction<String, Integer, String> padding;

	/**
	 * @param part
	 * @param width
	 * @param padding
	 */
	public FixWidthPart(Part part, int width, BiFunction<String, Integer, String> padding) {
		if(width < 0) {
			throw new IllegalArgumentException("width must be positive: " + width);
		}
		this.part = Objects.requireNonNull(part);
		this.width = width;
		this.padding = Objects.requireNonNull(padding);
	}

	/**
	 * @param part
	 * @param width
	 * @return
	 */
	public static FixWidthPart leftPad(Part part, int width) {
		return new FixWidthPart(part, width, LEFT_PAD);
	}

	/**
	 * @param part
	 * @param width
	 * @return
	 */
	public static FixWidthPart rightPad(Part part, int width) {
		return new FixWidthPart(part, width, RIGHT_PAD);
	}

	/**
	 * @param part
	 * @param width
	 * @return
	 */
	public static FixWidthPart centerPad(Part part, int width) {
		return new FixWidthPart(part, width, CENTER_PAD);
	}

	/**
	 * @see org.fagu.fmv.textprogressbar.Part#getWith(ProgressStatus)
	 */
	@Override
	public String getWith(ProgressStatus status) {
		String str = StringUtils.defaultString(part.getWith(status));
		return StringUtils.substring(padding.apply(str, width), 0, width);
	}

}

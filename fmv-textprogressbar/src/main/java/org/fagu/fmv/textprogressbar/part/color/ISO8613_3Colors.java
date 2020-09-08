package org.fagu.fmv.textprogressbar.part.color;

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

import java.awt.Color;

import org.fagu.fmv.textprogressbar.part.Colors;


/**
 * @author fagu
 */
public abstract class ISO8613_3Colors implements Colors {

	private static final String SUFFIX = (char)27 + "[0m";

	public ISO8613_3Colors() {}

	/**
	 * @see org.fagu.fmv.textprogressbar.part.Colors#getSuffix(java.awt.Color)
	 */
	@Override
	public String getSuffix(Color color) {
		return SUFFIX;
	}

	// ***************************************

	/**
	 * @param buf
	 * @param color
	 */
	protected void extendedRGB(StringBuilder buf, Color color) {
		buf.append(color.getRed()).append(';').append(color.getGreen()).append(';').append(color.getBlue()).append('m');
	}

}

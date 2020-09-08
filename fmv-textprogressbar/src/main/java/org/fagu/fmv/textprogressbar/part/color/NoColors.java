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
public class NoColors implements Colors {

	/**
	 * @see org.fagu.fmv.textprogressbar.part.Colors#getPrefix(java.awt.Color)
	 */
	@Override
	public String getPrefix(Color color) {
		return "";
	}

	/**
	 * @see org.fagu.fmv.textprogressbar.part.Colors#getSuffix(java.awt.Color)
	 */
	@Override
	public String getSuffix(Color color) {
		return "";
	}

}

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


/**
 * {@link https://en.wikipedia.org/wiki/ANSI_escape_code}<br>
 * {@link https://msdn.microsoft.com/en-us/library/windows/desktop/mt638032(v=vs.85).aspx}<br>
 * Extended Colors<br>
 * 
 * @author fagu
 */
public class ForegroundISO8613_3Colors extends ISO8613_3Colors {

	/**
	 * @see org.fagu.fmv.textprogressbar.part.Colors#getPrefix()
	 */
	@Override
	public String getPrefix(Color color) {
		StringBuilder buf = new StringBuilder();
		buf.append((char)27).append("38;2;");
		extendedRGB(buf, color);
		return buf.toString();
	}

}

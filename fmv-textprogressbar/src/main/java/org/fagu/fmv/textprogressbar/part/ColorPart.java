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

import java.awt.Color;
import java.util.Objects;

import org.fagu.fmv.textprogressbar.Part;
import org.fagu.fmv.textprogressbar.ProgressStatus;
import org.fagu.fmv.textprogressbar.part.color.BackgroundISO8613_3Colors;
import org.fagu.fmv.textprogressbar.part.color.ForegroundISO8613_3Colors;
import org.fagu.fmv.textprogressbar.part.color.NoColors;


/**
 * @author fagu
 */
public class ColorPart implements Part {

	private final Colors colors;

	private final Color color;

	private final Part part;

	public ColorPart(Colors colors, Color color, Part part) {
		this.colors = Objects.requireNonNull(colors);
		this.color = Objects.requireNonNull(color);
		this.part = Objects.requireNonNull(part);
	}

	public static ColorPart background(Color color, Part part) {
		Colors colors = isOnEclipse() ? new NoColors() : new BackgroundISO8613_3Colors();
		return new ColorPart(colors, color, part);
	}

	public static ColorPart foreground(Color color, Part part) {
		Colors colors = isOnEclipse() ? new NoColors() : new ForegroundISO8613_3Colors();
		return new ColorPart(colors, color, part);
	}

	@Override
	public String getWith(ProgressStatus status) {
		return new StringBuilder()
				.append(colors.getPrefix(color)).append(part.getWith(status)).append(colors.getSuffix(color))
				.toString();
	}

	// **********************************

	private static boolean isOnEclipse() {
		return System.getenv("PATH").contains("eclipse");
	}

}

package org.fagu.fmv.textprogressbar.part.color;

import java.awt.Color;


/**
 * {@link https://en.wikipedia.org/wiki/ANSI_escape_code}<br>
 * {@link https://msdn.microsoft.com/en-us/library/windows/desktop/mt638032(v=vs.85).aspx}<br>
 * Extended Colors<br>
 * 
 * @author fagu
 */
public class BackgroundISO8613_3Colors extends ISO8613_3Colors {

	/**
	 * @see org.fagu.fmv.textprogressbar.part.Colors#getPrefix()
	 */
	@Override
	public String getPrefix(Color color) {
		StringBuilder buf = new StringBuilder();
		buf.append((char)27).append("48;2;");
		extendedRGB(buf, color);
		return buf.toString();
	}

}

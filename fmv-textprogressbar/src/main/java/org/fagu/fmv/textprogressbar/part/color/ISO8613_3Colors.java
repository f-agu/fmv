package org.fagu.fmv.textprogressbar.part.color;

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

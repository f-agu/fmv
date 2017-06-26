package org.fagu.fmv.textprogressbar.part.color;

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

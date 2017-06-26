package org.fagu.fmv.textprogressbar.part;

import java.awt.Color;


/**
 * @author fagu
 */
public interface Colors {

	/**
	 * @param color
	 * @return
	 */
	String getPrefix(Color color);

	/**
	 * @param color
	 * @return
	 */
	String getSuffix(Color color);
}

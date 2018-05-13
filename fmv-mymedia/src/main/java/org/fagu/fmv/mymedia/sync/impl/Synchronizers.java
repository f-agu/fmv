package org.fagu.fmv.mymedia.sync.impl;

import org.fagu.fmv.mymedia.logger.Logger;
import org.fagu.fmv.mymedia.sync.Synchronizer;


/**
 * @author f.agu
 */
public class Synchronizers {

	/**
	 * 
	 */
	private Synchronizers() {}

	// --------------------------------------------------

	/**
	 * @param logger
	 * @return
	 */
	public static Synchronizer getDefault(Logger logger) {
		return new ConfirmDeleteSynchronizer(new LogSynchronizer(new StandardSynchronizer(), logger));
	}

}

package org.fagu.fmv.mymedia.reduce;

import java.io.File;


/**
 * @author Utilisateur
 * @created 26 mars 2018 16:02:51
 */
public class Reduced {

	private final File destFile;

	private boolean forceReplace;

	/**
	 * @param destFile
	 * @param forceReplace
	 */
	public Reduced(File destFile, boolean forceReplace) {
		this.destFile = destFile;
		this.forceReplace = forceReplace;
	}

	/**
	 * @return
	 */
	public File getDestFile() {
		return destFile;
	}

	/**
	 * @return
	 */
	public boolean isForceReplace() {
		return forceReplace;
	}

}
